package uk.gov.moj.cpp.activiti.spring.conf;

import static jakarta.transaction.Status.STATUS_ACTIVE;
import static jakarta.transaction.Status.STATUS_COMMITTED;
import static jakarta.transaction.Status.STATUS_MARKED_ROLLBACK;
import static jakarta.transaction.Status.STATUS_NO_TRANSACTION;
import static jakarta.transaction.Status.STATUS_ROLLEDBACK;
import static jakarta.transaction.Status.STATUS_UNKNOWN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.transaction.TransactionDefinition.ISOLATION_SERIALIZABLE;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;
import static org.springframework.transaction.support.TransactionSynchronization.STATUS_ROLLED_BACK;

import java.util.stream.Stream;

import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Synchronization;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.HeuristicCompletionException;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.InvalidIsolationLevelException;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@ExtendWith(MockitoExtension.class)
class JakartaJtaTransactionManagerTest {

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionSynchronization transactionSynchronization;

    private JakartaJtaTransactionManager jakartaJtaTransactionManager;

    @BeforeEach
    void createJakartaJtaTransactionManager() {
        jakartaJtaTransactionManager = new JakartaJtaTransactionManager(transactionManager);
    }

    @AfterEach
    void clearSpringTransactionSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void shouldBeginAndCommitAJtaTransactionWhenTheThreadHasNone() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE);

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition());
        jakartaJtaTransactionManager.commit(transactionStatus);

        assertThat(transactionStatus.isNewTransaction(), is(true));
        final InOrder inOrder = inOrder(transactionManager);
        inOrder.verify(transactionManager).begin();
        inOrder.verify(transactionManager).commit();
    }

    @Test
    void shouldRollBackTheJtaTransactionItBeganWhenTheCommandFails() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE);

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition());
        jakartaJtaTransactionManager.rollback(transactionStatus);

        verify(transactionManager).begin();
        verify(transactionManager).rollback();
        verify(transactionManager, never()).commit();
    }

    @Test
    void shouldJoinTheContainerTransactionWithoutBeginningOrCommittingIt() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_ACTIVE);

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition());
        jakartaJtaTransactionManager.commit(transactionStatus);

        assertThat(transactionStatus.isNewTransaction(), is(false));
        verify(transactionManager, never()).begin();
        verify(transactionManager, never()).commit();
    }

    @Test
    void shouldMarkTheContainerTransactionRollbackOnlyWhenAJoinedCommandFails() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_ACTIVE);

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition());
        jakartaJtaTransactionManager.rollback(transactionStatus);

        verify(transactionManager).setRollbackOnly();
        verify(transactionManager, never()).rollback();
    }

    @Test
    void shouldSuspendTheContainerTransactionForRequiresNewAndResumeItAfterCommit() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_ACTIVE);
        when(transactionManager.suspend()).thenReturn(transaction);
        final DefaultTransactionDefinition requiresNew = new DefaultTransactionDefinition(PROPAGATION_REQUIRES_NEW);

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(requiresNew);
        jakartaJtaTransactionManager.commit(transactionStatus);

        assertThat(transactionStatus.isNewTransaction(), is(true));
        final InOrder inOrder = inOrder(transactionManager);
        inOrder.verify(transactionManager).suspend();
        inOrder.verify(transactionManager).begin();
        inOrder.verify(transactionManager).commit();
        inOrder.verify(transactionManager).resume(transaction);
    }

    @Test
    void shouldRunAfterCommitCallbacksOnlyAfterTheJtaTransactionItBeganHasCommitted() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE);

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition());
        TransactionSynchronizationManager.registerSynchronization(transactionSynchronization);
        jakartaJtaTransactionManager.commit(transactionStatus);

        final InOrder inOrder = inOrder(transactionManager, transactionSynchronization);
        inOrder.verify(transactionSynchronization).beforeCommit(false);
        inOrder.verify(transactionManager).commit();
        inOrder.verify(transactionSynchronization).afterCommit();
        inOrder.verify(transactionSynchronization).afterCompletion(TransactionSynchronization.STATUS_COMMITTED);
    }

    @Test
    void shouldLeaveTheAfterCompletionCallbacksOfAJoinedCommandUntilTheContainerTransactionCompletes() throws Exception {
        commitAJoinedCommandAndCaptureItsJtaSynchronization();

        verify(transactionSynchronization, never()).afterCompletion(anyInt());
    }

    @ParameterizedTest
    @MethodSource("containerTransactionOutcomesAndTheirSpringCompletionStatus")
    void shouldRunTheAfterCompletionCallbacksOfAJoinedCommandWithTheOutcomeOfTheContainerTransaction(final int jtaStatus, final int springCompletionStatus) throws Exception {
        final Synchronization jtaSynchronization = commitAJoinedCommandAndCaptureItsJtaSynchronization();

        jtaSynchronization.afterCompletion(jtaStatus);

        verify(transactionSynchronization).afterCompletion(springCompletionStatus);
    }

    @Test
    void shouldRunAfterCompletionCallbacksAsRolledBackWhenTheContainerTransactionIsAlreadyRollingBack() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_ACTIVE);
        when(transactionManager.getTransaction()).thenReturn(transaction);
        doThrow(new RollbackException("rolling back")).when(transaction).registerSynchronization(any(Synchronization.class));

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition());
        TransactionSynchronizationManager.registerSynchronization(transactionSynchronization);
        jakartaJtaTransactionManager.commit(transactionStatus);

        verify(transactionSynchronization).afterCompletion(STATUS_ROLLED_BACK);
    }

    @ParameterizedTest
    @MethodSource("jtaCommitFailuresAndTheSpringExceptionsTheyBecome")
    void shouldReportAFailedJtaCommitAsTheMatchingSpringTransactionException(final Exception jtaCommitFailure,
                                                                             final Class<? extends TransactionException> springTransactionException) throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE);
        doThrow(jtaCommitFailure).when(transactionManager).commit();

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition());

        assertThrows(springTransactionException, () -> jakartaJtaTransactionManager.commit(transactionStatus));
    }

    @ParameterizedTest
    @MethodSource("heuristicJtaCommitFailuresAndTheirOutcomes")
    void shouldReportTheOutcomeOfAHeuristicJtaCommit(final Exception heuristicCommitFailure, final int outcomeState) throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE);
        doThrow(heuristicCommitFailure).when(transactionManager).commit();

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition());

        final HeuristicCompletionException heuristicCompletionException = assertThrows(HeuristicCompletionException.class, () -> jakartaJtaTransactionManager.commit(transactionStatus));
        assertThat(heuristicCompletionException.getOutcomeState(), is(outcomeState));
    }

    @Test
    void shouldReportThatATransactionCouldNotBeCreatedWhenTheJtaBeginFails() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_NO_TRANSACTION);
        doThrow(new SystemException("no transaction for you")).when(transactionManager).begin();

        assertThrows(CannotCreateTransactionException.class, () -> jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition()));
    }

    @Test
    void shouldRefuseACustomIsolationLevelBecauseJtaCannotApplyIt() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_NO_TRANSACTION);
        final DefaultTransactionDefinition serializable = new DefaultTransactionDefinition();
        serializable.setIsolationLevel(ISOLATION_SERIALIZABLE);

        assertThrows(InvalidIsolationLevelException.class, () -> jakartaJtaTransactionManager.getTransaction(serializable));
        verify(transactionManager, never()).begin();
    }

    @Test
    void shouldApplyACustomTimeoutToTheJtaTransactionItBeginsAndResetItAfterCommit() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE);
        final DefaultTransactionDefinition thirtySecondTimeout = new DefaultTransactionDefinition();
        thirtySecondTimeout.setTimeout(30);

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(thirtySecondTimeout);
        jakartaJtaTransactionManager.commit(transactionStatus);

        final InOrder inOrder = inOrder(transactionManager);
        inOrder.verify(transactionManager).setTransactionTimeout(30);
        inOrder.verify(transactionManager).begin();
        inOrder.verify(transactionManager).commit();
        inOrder.verify(transactionManager).setTransactionTimeout(0);
    }

    @Test
    void shouldStillCompleteTheCommitWhenTheJtaTimeoutCannotBeReset() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE);
        doNothing().when(transactionManager).setTransactionTimeout(30);
        doThrow(new SystemException("cannot reset")).when(transactionManager).setTransactionTimeout(0);
        final DefaultTransactionDefinition thirtySecondTimeout = new DefaultTransactionDefinition();
        thirtySecondTimeout.setTimeout(30);

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(thirtySecondTimeout);
        jakartaJtaTransactionManager.commit(transactionStatus);

        verify(transactionManager).commit();
        assertThat(transactionStatus.isCompleted(), is(true));
    }

    @Test
    void shouldNotRollBackWhenTheJtaTransactionHasAlreadyEnded() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_NO_TRANSACTION);

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition());
        jakartaJtaTransactionManager.rollback(transactionStatus);

        verify(transactionManager).begin();
        verify(transactionManager, never()).rollback();
    }

    @Test
    void shouldReportATransactionSystemExceptionWhenTheJtaRollbackFails() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE);
        doThrow(new SystemException("rollback failed")).when(transactionManager).rollback();

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition());

        assertThrows(TransactionSystemException.class, () -> jakartaJtaTransactionManager.rollback(transactionStatus));
    }

    @Test
    void shouldReportATransactionSystemExceptionWhenTheContainerTransactionCannotBeMarkedRollbackOnly() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_ACTIVE);
        doThrow(new SystemException("cannot mark")).when(transactionManager).setRollbackOnly();

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition());

        assertThrows(TransactionSystemException.class, () -> jakartaJtaTransactionManager.rollback(transactionStatus));
    }

    @Test
    void shouldReportATransactionSystemExceptionWhenTheContainerTransactionCannotBeSuspended() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_ACTIVE);
        doThrow(new SystemException("cannot suspend")).when(transactionManager).suspend();

        assertThrows(TransactionSystemException.class, () -> jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition(PROPAGATION_REQUIRES_NEW)));
        verify(transactionManager, never()).begin();
    }

    @Test
    void shouldReportAnIllegalTransactionStateWhenTheSuspendedTransactionIsNoLongerValid() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_ACTIVE);
        when(transactionManager.suspend()).thenReturn(transaction);
        doThrow(new InvalidTransactionException("gone")).when(transactionManager).resume(transaction);

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition(PROPAGATION_REQUIRES_NEW));

        assertThrows(IllegalTransactionStateException.class, () -> jakartaJtaTransactionManager.commit(transactionStatus));
    }

    @Test
    void shouldReportATransactionSystemExceptionWhenTheSuspendedTransactionCannotBeResumed() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_ACTIVE);
        when(transactionManager.suspend()).thenReturn(transaction);
        doThrow(new SystemException("cannot resume")).when(transactionManager).resume(transaction);

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition(PROPAGATION_REQUIRES_NEW));

        assertThrows(TransactionSystemException.class, () -> jakartaJtaTransactionManager.commit(transactionStatus));
    }

    @Test
    void shouldReportATransactionSystemExceptionWhenTheJtaStatusCannotBeRead() throws Exception {
        when(transactionManager.getStatus()).thenThrow(new SystemException("no status"));

        assertThrows(TransactionSystemException.class, () -> jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition()));
    }

    @Test
    void shouldReportATransactionSystemExceptionWhenAJoinedCommandCannotRegisterItsCallbacksWithTheContainerTransaction() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_ACTIVE);
        when(transactionManager.getTransaction()).thenThrow(new SystemException("no transaction"));

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition());
        TransactionSynchronizationManager.registerSynchronization(transactionSynchronization);

        assertThrows(TransactionSystemException.class, () -> jakartaJtaTransactionManager.commit(transactionStatus));
    }

    @Test
    void shouldReportTheTransactionAsRollbackOnlyWhenTheJtaTransactionIsMarkedRollbackOnly() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_NO_TRANSACTION, STATUS_MARKED_ROLLBACK);

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition());

        assertThat(transactionStatus.isRollbackOnly(), is(true));
    }

    @Test
    void shouldFlushTheRegisteredSynchronizationsWhenTheTransactionIsFlushed() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_NO_TRANSACTION);

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition());
        TransactionSynchronizationManager.registerSynchronization(transactionSynchronization);
        transactionStatus.flush();

        verify(transactionSynchronization).flush();
    }

    @Test
    void shouldNotRunTheBeforeCompletionCallbacksOfAJoinedCommandAgainWhenTheContainerTransactionCompletes() throws Exception {
        final Synchronization jtaSynchronization = commitAJoinedCommandAndCaptureItsJtaSynchronization();

        jtaSynchronization.beforeCompletion();

        verify(transactionSynchronization, times(1)).beforeCompletion();
    }

    static Stream<Arguments> jtaCommitFailuresAndTheSpringExceptionsTheyBecome() {
        return Stream.of(
                arguments(new RollbackException("rolled back"), UnexpectedRollbackException.class),
                arguments(new HeuristicMixedException("partly rolled back"), HeuristicCompletionException.class),
                arguments(new HeuristicRollbackException("rolled back heuristically"), HeuristicCompletionException.class),
                arguments(new SystemException("commit failed"), TransactionSystemException.class));
    }

    static Stream<Arguments> heuristicJtaCommitFailuresAndTheirOutcomes() {
        return Stream.of(
                arguments(new HeuristicMixedException("partly rolled back"), HeuristicCompletionException.STATE_MIXED),
                arguments(new HeuristicRollbackException("rolled back heuristically"), HeuristicCompletionException.STATE_ROLLED_BACK));
    }

    static Stream<Arguments> containerTransactionOutcomesAndTheirSpringCompletionStatus() {
        return Stream.of(
                arguments(STATUS_COMMITTED, TransactionSynchronization.STATUS_COMMITTED),
                arguments(STATUS_ROLLEDBACK, STATUS_ROLLED_BACK),
                arguments(STATUS_UNKNOWN, TransactionSynchronization.STATUS_UNKNOWN));
    }

    private Synchronization commitAJoinedCommandAndCaptureItsJtaSynchronization() throws Exception {
        when(transactionManager.getStatus()).thenReturn(STATUS_ACTIVE);
        when(transactionManager.getTransaction()).thenReturn(transaction);
        final ArgumentCaptor<Synchronization> jtaSynchronization = ArgumentCaptor.forClass(Synchronization.class);

        final TransactionStatus transactionStatus = jakartaJtaTransactionManager.getTransaction(new DefaultTransactionDefinition());
        TransactionSynchronizationManager.registerSynchronization(transactionSynchronization);
        jakartaJtaTransactionManager.commit(transactionStatus);

        verify(transaction).registerSynchronization(jtaSynchronization.capture());
        return jtaSynchronization.getValue();
    }
}
