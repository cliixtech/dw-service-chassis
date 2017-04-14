package common.persistence;

import javax.jdo.PersistenceManager;

/**
 * A functional interface for execute operations inside the context of a
 * transaction. The interface provides only one method
 * {@link TransactionalOperation#execute(PersistenceManager)} that receives as
 * parameter the {@link PersistenceManager}.
 */
@FunctionalInterface
public interface TransactionalOperation<T> {

    public T execute(PersistenceManager pm);

}
