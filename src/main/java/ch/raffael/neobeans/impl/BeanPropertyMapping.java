package ch.raffael.neobeans.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.GraphDatabaseService;

import ch.raffael.neobeans.BeanStoreException;
import ch.raffael.neobeans.Converter;
import ch.raffael.neobeans.NodeKey;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class BeanPropertyMapping extends AbstractPropertyMapping {

    private final Method readMethod;
    private final Method writeMethod;

    public BeanPropertyMapping(@NotNull GraphDatabaseService database, String propertyName, Converter converter, IndexMapping indexMapping, Method readMethod, Method writeMethod) {
        super(database, propertyName, converter, indexMapping);
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
    }

    @Override
    protected Object readBeanValue(Object bean) {
        try {
            return readMethod.invoke(bean);
        }
        catch ( IllegalAccessException e ) {
            throw new BeanStoreException("Error reading value from " + bean, e);
        }
        catch ( InvocationTargetException e ) {
            throw new BeanStoreException("Error reading value from " + bean, e);
        }
    }

    @Override
    protected void writeBeanValue(Object bean, Object value) {
        try {
            writeMethod.invoke(bean, value);
        }
        catch ( IllegalAccessException e ) {
            throw new BeanStoreException("Error reading value from " + bean, e);
        }
        catch ( InvocationTargetException e ) {
            throw new BeanStoreException("Error reading value from " + bean, e);
        }
    }

    public static class BeanPropertyKeyAdapter implements BeanMapping.KeyAdapter {

        private final Method readMethod;
        private final Method writeMethod;

        public BeanPropertyKeyAdapter(Method readMethod, Method writeMethod) {
            this.readMethod = readMethod;
            this.writeMethod = writeMethod;
        }

        @Override
        public NodeKey getKey(Object bean) {
            try {
                return (NodeKey)readMethod.invoke(bean);
            }
            catch ( IllegalAccessException e ) {
                throw new BeanStoreException("Error reading value from " + bean, e);
            }
            catch ( InvocationTargetException e ) {
                throw new BeanStoreException("Error reading value from " + bean, e);
            }
        }

        @Override
        public void setKey(Object bean, NodeKey key) {
            try {
                writeMethod.invoke(bean, key);
            }
            catch ( IllegalAccessException e ) {
                throw new BeanStoreException("Error reading value from " + bean, e);
            }
            catch ( InvocationTargetException e ) {
                throw new BeanStoreException("Error reading value from " + bean, e);
            }
        }
    }

}
