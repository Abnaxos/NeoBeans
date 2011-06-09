package ch.raffael.neobeans.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import ch.raffael.neobeans.NeoBeanStore;
import ch.raffael.neobeans.Update;

import static ch.raffael.util.common.NotImplementedException.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DefaultNeoBeanStore implements NeoBeanStore {

    private final GraphDatabaseService database;
    private final Map<Class<?>, StorageDelegate> delegates = new HashMap<Class<?>, StorageDelegate>();
    private final StorageDelegateFactory storageDelegateFactory;

    public DefaultNeoBeanStore(@NotNull GraphDatabaseService database) {
        this(database, new AnnotatedBeanStorageDelegateFactory());
    }

    public DefaultNeoBeanStore(@NotNull GraphDatabaseService database, @NotNull AnnotatedBeanStorageDelegateFactory storageDelegateFactory) {
        this.database = database;
        this.storageDelegateFactory = storageDelegateFactory;
    }

    @Override
    @NotNull
    public GraphDatabaseService database() {
        return database;
    }

    @Override
    @NotNull
    public Transaction beginTx() {
        return database.beginTx();
    }

    @Override
    @PreDestroy
    public void shutdown() {
        database.shutdown();
    }

    @Override
    public void performUpdate(@NotNull Update update) {
        notImplemented();
    }

    @NotNull
    protected StorageDelegate getDelegate(@NotNull Class<?> beanClass) {
        synchronized ( delegates ) {
            StorageDelegate delegate = delegates.get(beanClass);
            if ( delegate == null ) {
                delegate = storageDelegateFactory.createStorageDelegate(beanClass);
                delegates.put(beanClass, delegate);
            }
            return delegate;
        }
    }

}
