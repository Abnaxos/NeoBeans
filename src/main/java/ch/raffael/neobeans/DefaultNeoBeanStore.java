package ch.raffael.neobeans;

import javax.annotation.PreDestroy;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import static ch.raffael.util.common.NotImplementedException.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DefaultNeoBeanStore implements NeoBeanStore {

    private final GraphDatabaseService database;

    public DefaultNeoBeanStore(GraphDatabaseService database) {
        this.database = database;
    }

    @Override
    public GraphDatabaseService database() {
        return database;
    }

    @Override
    public Transaction beginTx() {
        return database.beginTx();
    }

    @Override
    @PreDestroy
    public void shutdown() {
        database.shutdown();
    }

    @Override
    public void performUpdate(Update update) {
        notImplemented();
    }

}
