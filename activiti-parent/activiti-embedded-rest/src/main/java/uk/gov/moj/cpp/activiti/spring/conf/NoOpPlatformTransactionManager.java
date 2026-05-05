package uk.gov.moj.cpp.activiti.spring.conf;

import java.util.List;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * PlatformTransactionManager for use with Activiti when transactionsExternallyManaged=true.
 *
 * WildFly's JTA container manages the real database transaction. Spring's
 * TransactionSynchronizationManager is used by Activiti's SpringTransactionContext to register
 * post-commit listeners (e.g. to fire JMS events after a timer job). This implementation
 * initialises Spring's thread-local synchronisation state so those listeners can be registered,
 * and then fires them when the command finishes (commit).
 *
 * When synchronisation is already active (nested command), it is left in place and the inner
 * commit is a no-op so only the outermost commit fires and clears the synchronisations.
 */
public class NoOpPlatformTransactionManager implements PlatformTransactionManager {

    @Override
    public TransactionStatus getTransaction(final TransactionDefinition definition) throws TransactionException {
        final boolean initiatedSync = !TransactionSynchronizationManager.isSynchronizationActive();
        if (initiatedSync) {
            TransactionSynchronizationManager.initSynchronization();
        }
        return new SimpleTransactionStatus(initiatedSync);
    }

    @Override
    public void commit(final TransactionStatus status) throws TransactionException {
        if (status.isNewTransaction() && TransactionSynchronizationManager.isSynchronizationActive()) {
            final List<TransactionSynchronization> syncs =
                    TransactionSynchronizationManager.getSynchronizations();
            syncs.forEach(sync -> {
                try { sync.beforeCommit(false); } catch (final Exception ignored) {}
            });
            syncs.forEach(sync -> {
                try { sync.beforeCompletion(); } catch (final Exception ignored) {}
            });
            syncs.forEach(sync -> {
                try { sync.afterCommit(); } catch (final Exception ignored) {}
            });
            syncs.forEach(sync -> {
                try { sync.afterCompletion(TransactionSynchronization.STATUS_COMMITTED); } catch (final Exception ignored) {}
            });
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Override
    public void rollback(final TransactionStatus status) throws TransactionException {
        if (status.isNewTransaction() && TransactionSynchronizationManager.isSynchronizationActive()) {
            final List<TransactionSynchronization> syncs =
                    TransactionSynchronizationManager.getSynchronizations();
            syncs.forEach(sync -> {
                try { sync.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK); } catch (final Exception ignored) {}
            });
            TransactionSynchronizationManager.clearSynchronization();
        }
    }
}
