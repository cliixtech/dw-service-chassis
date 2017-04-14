package common.persistence;

import static common.persistence.PersistenceManagerWrapper.db;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Optional;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class Dao<T> {

    protected Class<T> klass;

    @SuppressWarnings("unchecked")
    public Dao() {
        this.klass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected PersistenceManager pm() {
        return db.getPersistenceManager();
    }

    protected Query<T> newQuery() {
        return pm().newQuery(this.klass);
    }

    public Optional<T> getById(Object id) {
        try {
            return Optional.of(pm().getObjectById(this.klass, id));
        } catch (JDOObjectNotFoundException e) {
            return Optional.empty();
        }
    }

    public T save(T object) {
        return pm().makePersistent(object);
    }

    public void delete(T object) {
        pm().deletePersistent(object);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Collection<T> all() {
        Query q = this.newQuery();
        return (Collection<T>) q.execute();
    }

    @SuppressWarnings("rawtypes")
    public Long count() {
        Query q = this.newQuery();
        q.setResult("count(this)");
        return (long) q.execute();
    }
}
