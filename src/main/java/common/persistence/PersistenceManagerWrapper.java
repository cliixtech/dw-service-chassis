package common.persistence;

import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceManagerWrapper {
    private static final Logger log = LoggerFactory.getLogger(PersistenceManagerWrapper.class);

    public static final PersistenceManagerWrapper db = new Builder().buildWrapper();
    private PersistenceManagerFactory pmf;
    private ThreadLocal<PersistenceManager> persistencePerThread;

    protected PersistenceManagerWrapper(PersistenceManagerFactory pmf, ThreadLocal<PersistenceManager> pmPerThread) {
        this.pmf = pmf;
        this.persistencePerThread = pmPerThread;
    }

    /**
     * Get's the active, or create one, {@link PersistenceManager} per thread.
     * Two consecutive calls, from the same Thread should return the same
     * object, except the {@link PersistenceManager} is explicitly closed,
     * either by calling {@link PersistenceManager#close()} or
     * {@link PersistenceManagerWrapper#closePersistenceManager()}
     */
    public PersistenceManager getPersistenceManager() {
        PersistenceManager pm = this.persistencePerThread.get();
        if (pm == null || pm.isClosed()) {
            log.debug("Creating a new Persistencemanager");
            pm = this.pmf.getPersistenceManager();
            this.persistencePerThread.set(pm);
        }
        return pm;
    }

    /**
     * Closes the {@link PersistenceManager} for the current thread, if there's
     * one.
     */
    public void closePersistenceManager() {
        PersistenceManager pm = this.persistencePerThread.get();
        if (pm != null && !pm.isClosed()) {
            log.debug("Closing existent Persistencemanager");
            pm.close();
            this.persistencePerThread.remove();
        }
    }

    /**
     * Executes the given {@link TransactionalOperation} inside a transaction.
     * It's important to notice the current thread {@link PersistenceManager}
     * will be closed after the transaction executes.
     */
    public <R> R transaction(TransactionalOperation<R> action) {
        Transaction tx = null;
        PersistenceManager pm = getPersistenceManager();
        log.debug("Preparing to execute DB Transaction");
        tx = pm.currentTransaction();
        tx.begin();

        try {
            R result = action.execute(pm);
            tx.commit();
            log.info("DB Transaction on commited");
            return result;
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                log.warn("DB Transaction has failed, rolling back.");
                tx.rollback();
            }
            throw ex;
        }
    }

    static class Builder {

        private DBConfig dbConfig;

        public Builder() {
            dbConfig = new DBConfig();
        }

        final protected PersistenceManagerWrapper buildWrapper() {
            Properties dbProps = buildProperties();
            PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(dbProps);
            ThreadLocal<PersistenceManager> tl = new ThreadLocal<>();
            return new PersistenceManagerWrapper(pmf, tl);
        }

        private Properties buildProperties() {
            Properties properties = new Properties();
            properties.setProperty(
                    "javax.jdo.PersistenceManagerFactoryClass",
                    "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
            properties.setProperty("javax.jdo.option.ConnectionDriverName", this.dbConfig.getDriver());
            properties.setProperty("javax.jdo.option.ConnectionURL", this.dbConfig.getConnectionURL());

            properties.setProperty("javax.jdo.option.ConnectionUserName", this.dbConfig.getUser());
            // properties.setProperty("javax.jdo.option.ConnectionPassword",
            // this.dbConfig.getPassword());
            properties.setProperty("datanucleus.schema.autoCreateAll", "true");
            return properties;
        }
    }
}
