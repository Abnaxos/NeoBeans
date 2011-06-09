package ch.raffael.neobeans.impl;

import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.index.Index;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class IndexMapping<T extends PropertyContainer> {

    private final Index<T> index;
    private final String key;

    public IndexMapping(Index<T> index, String key) {
        this.index = index;
        this.key = key;
    }

    public void update(@NotNull T entity, @NotNull Object oldValue, @NotNull Object value) {
        // FIXME: handle arrays
        index.remove(entity, key, oldValue);
        index.add(entity, key, value);
    }

    public void remove(@NotNull T entity) {
        index.remove(entity, key);
    }

}
