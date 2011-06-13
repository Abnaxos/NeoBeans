package ch.raffael.neobeans;

import javax.annotation.PreDestroy;

import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface NeoBeanStore {

    String PROPERTY_TYPE = "neobeans:type";
    String PROPERTY_KEY = "neobeans:key";

    GraphDatabaseService database();

    Transaction beginTx();

    @PreDestroy
    void shutdown();

    <T> T retrieve(@NotNull T bean);

    <T> T retrieve(@NotNull NodeKey key, @NotNull T bean);

    // maybe add:
    // Object retrieve(@NotNull NodeKey key);

    void store(@NotNull Object bean);

    boolean delete(@NotNull Object bean);

    void performUpdate(@NotNull Update update);

}
