package uk.gov.moj.cpp.activiti.spring.conf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class NoOpPlatformTransactionManagerTest {

    private final NoOpPlatformTransactionManager manager = new NoOpPlatformTransactionManager();

    @AfterEach
    void clearSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void getTransactionShouldInitialiseSynchronisationWhenNotAlreadyActive() {
        final TransactionStatus status = manager.getTransaction(null);

        assertThat(TransactionSynchronizationManager.isSynchronizationActive(), is(true));
        assertThat(status.isNewTransaction(), is(true));
    }

    @Test
    void getTransactionShouldNotReinitialiseWhenSynchronisationAlreadyActive() {
        TransactionSynchronizationManager.initSynchronization();

        final TransactionStatus status = manager.getTransaction(null);

        assertThat(status.isNewTransaction(), is(false));
        assertThat(TransactionSynchronizationManager.isSynchronizationActive(), is(true));
    }

    @Test
    void commitShouldFireSynchronisationCallbacksInOrderAndClearState() {
        final TransactionStatus status = manager.getTransaction(null);

        final TransactionSynchronization sync = mock(TransactionSynchronization.class);
        TransactionSynchronizationManager.registerSynchronization(sync);

        manager.commit(status);

        final InOrder order = inOrder(sync);
        order.verify(sync).beforeCommit(false);
        order.verify(sync).beforeCompletion();
        order.verify(sync).afterCommit();
        order.verify(sync).afterCompletion(TransactionSynchronization.STATUS_COMMITTED);

        assertThat(TransactionSynchronizationManager.isSynchronizationActive(), is(false));
    }

    @Test
    void commitShouldNotFireCallbacksWhenNotNewTransaction() {
        TransactionSynchronizationManager.initSynchronization();
        final TransactionStatus status = manager.getTransaction(null);

        final TransactionSynchronization sync = mock(TransactionSynchronization.class);
        TransactionSynchronizationManager.registerSynchronization(sync);

        manager.commit(status);

        verify(sync, never()).afterCommit();
        assertThat(TransactionSynchronizationManager.isSynchronizationActive(), is(true));
    }

    @Test
    void rollbackShouldFireRollbackCallbackAndClearState() {
        final TransactionStatus status = manager.getTransaction(null);

        final TransactionSynchronization sync = mock(TransactionSynchronization.class);
        TransactionSynchronizationManager.registerSynchronization(sync);

        manager.rollback(status);

        verify(sync).afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
        assertThat(TransactionSynchronizationManager.isSynchronizationActive(), is(false));
    }
}
