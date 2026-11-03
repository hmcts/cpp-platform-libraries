package uk.gov.moj.cpp.activiti.spring.conf;

import static jakarta.transaction.Status.STATUS_COMMITTED;
import static jakarta.transaction.Status.STATUS_MARKED_ROLLBACK;
import static jakarta.transaction.Status.STATUS_NO_TRANSACTION;
import static jakarta.transaction.Status.STATUS_ROLLEDBACK;
import static org.springframework.transaction.support.TransactionSynchronization.STATUS_ROLLED_BACK;
import static org.springframework.transaction.support.TransactionSynchronization.STATUS_UNKNOWN;

import java.util.List;
import java.util.Map;

import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Synchronization;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;

import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.HeuristicCompletionException;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.InvalidIsolationLevelException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.SmartTransactionObject;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationUtils;

/**
 * Spring PlatformTransactionManager over WildFly's Jakarta JTA TransactionManager, for Activiti with
 * transactionsExternallyManaged=true.
 *
 * Activiti runs every engine command through Spring's transaction template, so this manager decides which transaction
 * a command's database writes and JMS sends belong to:
 * - on a thread with no transaction, which is Activiti's job executor running a timer job or an async step, it begins
 *   a JTA transaction. A job that fails part way through then rolls back everything it did, its JMS sends included,
 *   before Activiti retries it.
 * - inside a container transaction (an event processor, a command handler or a REST call) it joins that transaction,
 *   and a failed command marks it rollback-only.
 * - for REQUIRES_NEW and NOT_SUPPORTED it suspends the current transaction and resumes it afterwards.
 *
 * On Java 17 this library used Spring's JtaTransactionManager, which does the same. Activiti 5.22 brings in Spring 4.1,
 * whose JtaTransactionManager is written against javax.transaction, and WildFly 40 only provides jakarta.transaction.
 * As in Spring's JtaTransactionManager, propagation and Spring's transaction synchronisation are left to
 * AbstractPlatformTransactionManager; this class only makes the JTA calls.
 */
public class JakartaJtaTransactionManager extends AbstractPlatformTransactionManager {

    private static final long serialVersionUID = 1L;

    private static final String COULD_NOT_RESUME = "Could not resume the JTA transaction";

    private static final Map<Integer, Integer> SPRING_COMPLETION_STATUS_FOR_JTA_STATUS = Map.of(
            STATUS_COMMITTED, TransactionSynchronization.STATUS_COMMITTED,
            STATUS_ROLLEDBACK, STATUS_ROLLED_BACK);

    private final transient TransactionManager transactionManager;

    public JakartaJtaTransactionManager(final TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Return a new handle for each call, as Spring's JtaTransactionManager does. Spring calls this once per
     * getTransaction and passes the handle to the other methods for that call only. The transaction itself is bound to
     * the thread by the JTA TransactionManager, so a new handle still sees it. A shared handle would be wrong: an inner
     * command joining an outer transaction would see the outer call's timeout flag and reset the timeout too early.
     */
    @Override
    protected Object doGetTransaction() {
        return new JtaTransactionObject();
    }

    @Override
    protected boolean isExistingTransaction(final Object transaction) {
        return jtaStatus() != STATUS_NO_TRANSACTION;
    }

    @Override
    protected void doBegin(final Object transaction, final TransactionDefinition definition) {
        if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
            throw new InvalidIsolationLevelException("JTA does not support custom isolation levels");
        }

        final int timeout = determineTimeout(definition);
        try {
            if (timeout > TransactionDefinition.TIMEOUT_DEFAULT) {
                transactionManager.setTransactionTimeout(timeout);
                ((JtaTransactionObject) transaction).timeoutApplied();
            }
            transactionManager.begin();
        } catch (final NotSupportedException | SystemException e) {
            throw new CannotCreateTransactionException("Could not begin a JTA transaction", e);
        }
    }

    @Override
    protected Object doSuspend(final Object transaction) {
        try {
            return transactionManager.suspend();
        } catch (final SystemException e) {
            throw new TransactionSystemException("Could not suspend the JTA transaction", e);
        }
    }

    @Override
    protected void doResume(final Object transaction, final Object suspendedResources) {
        try {
            transactionManager.resume((Transaction) suspendedResources);
        } catch (final InvalidTransactionException e) {
            throw new IllegalTransactionStateException(COULD_NOT_RESUME, e);
        } catch (final SystemException e) {
            throw new TransactionSystemException(COULD_NOT_RESUME, e);
        }
    }

    /**
     * Commit a transaction marked rollback-only all the same, so that JTA rolls it back and reports it, as Spring's
     * JtaTransactionManager does.
     */
    @Override
    protected boolean shouldCommitOnGlobalRollbackOnly() {
        return true;
    }

    @Override
    protected void doCommit(final DefaultTransactionStatus status) {
        try {
            transactionManager.commit();
        } catch (final RollbackException e) {
            throw new UnexpectedRollbackException("The JTA transaction was rolled back instead of committed", e);
        } catch (final HeuristicMixedException e) {
            throw new HeuristicCompletionException(HeuristicCompletionException.STATE_MIXED, e);
        } catch (final HeuristicRollbackException e) {
            throw new HeuristicCompletionException(HeuristicCompletionException.STATE_ROLLED_BACK, e);
        } catch (final SystemException | IllegalStateException e) {
            throw new TransactionSystemException("Could not commit the JTA transaction", e);
        }
    }

    @Override
    protected void doRollback(final DefaultTransactionStatus status) {
        try {
            if (jtaStatus() != STATUS_NO_TRANSACTION) {
                transactionManager.rollback();
            }
        } catch (final SystemException | IllegalStateException e) {
            throw new TransactionSystemException("Could not roll back the JTA transaction", e);
        }
    }

    @Override
    protected void doSetRollbackOnly(final DefaultTransactionStatus status) {
        try {
            transactionManager.setRollbackOnly();
        } catch (final SystemException | IllegalStateException e) {
            throw new TransactionSystemException("Could not mark the JTA transaction rollback-only", e);
        }
    }

    /**
     * A command that joined a container transaction finishes before that transaction does, so its synchronisations'
     * afterCompletion callbacks are registered with the JTA transaction and run when it really commits or rolls back.
     */
    @Override
    protected void registerAfterCompletionWithExistingTransaction(final Object transaction,
                                                                  final List<TransactionSynchronization> synchronizations) {
        try {
            transactionManager.getTransaction().registerSynchronization(new AfterCompletionSynchronization(synchronizations));
        } catch (final RollbackException e) {
            invokeAfterCompletion(synchronizations, STATUS_ROLLED_BACK);
        } catch (final SystemException | IllegalStateException e) {
            throw new TransactionSystemException("Could not register a synchronization with the JTA transaction", e);
        }
    }

    @Override
    protected void doCleanupAfterCompletion(final Object transaction) {
        if (((JtaTransactionObject) transaction).isTimeoutApplied()) {
            try {
                transactionManager.setTransactionTimeout(0);
            } catch (final SystemException e) {
                logger.debug("Could not reset the JTA transaction timeout", e);
            }
        }
    }

    private int jtaStatus() {
        try {
            return transactionManager.getStatus();
        } catch (final SystemException e) {
            throw new TransactionSystemException("Could not read the JTA transaction status", e);
        }
    }

    private class JtaTransactionObject implements SmartTransactionObject {

        private boolean timeoutApplied;

        void timeoutApplied() {
            timeoutApplied = true;
        }

        boolean isTimeoutApplied() {
            return timeoutApplied;
        }

        @Override
        public boolean isRollbackOnly() {
            return jtaStatus() == STATUS_MARKED_ROLLBACK;
        }

        @Override
        public void flush() {
            TransactionSynchronizationUtils.triggerFlush();
        }
    }

    private class AfterCompletionSynchronization implements Synchronization {

        private final List<TransactionSynchronization> synchronizations;

        AfterCompletionSynchronization(final List<TransactionSynchronization> synchronizations) {
            this.synchronizations = synchronizations;
        }

        @Override
        public void beforeCompletion() {
            // the joined command already ran its beforeCommit and beforeCompletion callbacks when it finished
        }

        @Override
        public void afterCompletion(final int jtaStatus) {
            invokeAfterCompletion(synchronizations, SPRING_COMPLETION_STATUS_FOR_JTA_STATUS.getOrDefault(jtaStatus, STATUS_UNKNOWN));
        }
    }
}
