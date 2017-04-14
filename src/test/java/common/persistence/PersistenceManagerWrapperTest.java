package common.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.junit.Before;
import org.junit.Test;

import common.persistence.PersistenceManagerWrapper;

public class PersistenceManagerWrapperTest {

    private PersistenceManagerFactory pmf;
    private ThreadLocal<PersistenceManager> tl;

    private PersistenceManager pm;
    private Transaction tx;

    private PersistenceManagerWrapper pmw;

    @SuppressWarnings("unchecked")
    @Before
    public void before() {
        tl = mock(ThreadLocal.class);
        pmf = mock(PersistenceManagerFactory.class);
        pmw = new PersistenceManagerWrapper(pmf, tl);
        pm = mock(PersistenceManager.class);
        tx = mock(Transaction.class);

        when(pmf.getPersistenceManager()).thenReturn(pm);
        when(pm.currentTransaction()).thenReturn(tx);
    }

    @Test
    public void getPersistenceManagerShouldReturnANewPersistenceManager() {
        when(tl.get()).thenReturn(null);
        PersistenceManager pm = pmw.getPersistenceManager();
        assertThat(pm).isSameAs(this.pm);
        verify(pmf).getPersistenceManager();
    }

    @Test
    public void getPersistenceManagerShouldReturnTheSamePersistenceManagerIfThereIsOneAndIsntClosed() {
        when(tl.get()).thenReturn(pm);
        when(pm.isClosed()).thenReturn(false);
        assertThat(pmw.getPersistenceManager()).isSameAs(pm);
        verify(tl).get();
        verify(pmf, never()).getPersistenceManager();
    }

    @Test
    public void getPersistenceManagerShouldReturnANewPersistenceManagerIfTheCurrentOneIsClosed() {
        PersistenceManager closedPm = mock(PersistenceManager.class);
        when(closedPm.isClosed()).thenReturn(true);
        when(tl.get()).thenReturn(closedPm);
        assertThat(pmw.getPersistenceManager()).isSameAs(pm);

        verify(pmf).getPersistenceManager();
        verify(tl).set(pm);
    }

    @Test
    public void transactionShouldExecuteTheActionWithinATransactionalContext() {
        pmw.transaction((localPm) -> {
            verify(tx).begin();
            assertThat(localPm).isSameAs(pm);
            return null;
        });
        verify(tx).commit();
    }

    @Test
    public void transactionShouldReturnTheReturnedValueFromTheActionItself() {
        final Object value = new Object();

        Object returnedValue = pmw.transaction((pm) -> {
            return value;
        });

        assertThat(returnedValue).isSameAs(value);
    }

    @Test(expected = RuntimeException.class)
    public void transactionShouldRollbackTheTransactionIfItIsStillActiveAtTheEndOfThings() {
        when(tx.isActive()).thenReturn(true);
        try {
            pmw.transaction((pm) -> {
                throw new RuntimeException();
            });
        } finally {
            verify(tx).rollback();
        }
    }

}
