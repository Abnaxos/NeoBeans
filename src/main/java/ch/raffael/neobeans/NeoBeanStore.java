package ch.raffael.neobeans;

import javax.annotation.PreDestroy;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface NeoBeanStore {

    GraphDatabaseService database();

    Transaction beginTx();

    @PreDestroy
    void shutdown();

    void performUpdate(Update update);
}
