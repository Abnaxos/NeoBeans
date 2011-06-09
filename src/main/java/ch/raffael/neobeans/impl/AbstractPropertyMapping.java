package ch.raffael.neobeans.impl;

import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.PropertyContainer;

import ch.raffael.neobeans.Converter;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class AbstractPropertyMapping {

    private final GraphDatabaseService database;
    private final String propertyName;
    private final Converter converter;
    private final IndexMapping indexMapping;

    protected AbstractPropertyMapping(@NotNull GraphDatabaseService database, String propertyName, Converter converter, IndexMapping indexMapping) {
        this.database = database;
        this.propertyName = propertyName;
        this.converter = converter;
        this.indexMapping = indexMapping;
    }

    public Object entityToBean(@NotNull PropertyContainer entity, @NotNull Object bean) {
        Object value = entity.getProperty(propertyName, null);
        if ( converter != null && value != null ) {
            value = converter.fromNeo4j(value);
        }
        writeBeanValue(bean, value);
        return value;
    }

    public Object beanToEntity(@NotNull PropertyContainer entity, @NotNull Object bean) {
        Object value = readBeanValue(bean);
        if ( converter != null && value != null ) {
            value = converter.toNeo4j(value);
        }
        boolean updateIndex = false;
        Object oldValue = null;
        if ( indexMapping != null ) {
            oldValue = entity.getProperty(propertyName, null);
            updateIndex = !Neo4jUtils.equals(oldValue, value);
        }
        if ( value != null ) {
            entity.setProperty(propertyName, value);
        }
        else {
            entity.removeProperty(propertyName);
        }
        if ( updateIndex ) {
            indexMapping.update(entity, oldValue, value);
        }
        return value;
    }

    protected abstract Object readBeanValue(Object bean);
    protected abstract void writeBeanValue(Object bean, Object value);

    public GraphDatabaseService getDatabase() {
        return database;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Converter getConverter() {
        return converter;
    }

    public IndexMapping getIndexMapping() {
        return indexMapping;
    }
}
