package ch.raffael.neobeans.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.PropertyContainer;

import ch.raffael.neobeans.InvalidBeanException;
import ch.raffael.neobeans.NeoBeanStore;
import ch.raffael.neobeans.NodeKey;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class BeanMapping {

    private final Class<?> beanClass;
    private final boolean isNode;
    private final AbstractPropertyMapping keyMapping;
    private final Map<String, AbstractPropertyMapping> propertyMappings = new HashMap<String, AbstractPropertyMapping>();

    public BeanMapping(boolean node, Class<?> beanClass, AbstractPropertyMapping keyMapping) {
        isNode = node;
        this.beanClass = beanClass;
        this.keyMapping = keyMapping;
    }

    public void addPropertyMapping(AbstractPropertyMapping propertyMapping) {
        propertyMappings.put(propertyMapping.getPropertyName(), propertyMapping);
    }

    public boolean isNode() {
        return isNode;
    }

    public boolean isRelationship() {
        return !isNode;
    }

    public void writeType(@NotNull PropertyContainer entity) {
        entity.setProperty(NeoBeanStore.PROPERTY_TYPE, beanClass.getName());
    }

    public boolean matchType(@NotNull Object bean, @NotNull PropertyContainer entity) {
        return entity.getProperty(NeoBeanStore.PROPERTY_TYPE).equals(bean.getClass().getName());
    }

    public NodeKey getKey(@NotNull Object bean) {
        Preconditions.checkState(isNode, "getKey() supported only by node mappings");
        return (NodeKey)keyMapping.readBeanValue(bean); // FIXME: really read directly?
    }

    public void setKey(@NotNull Object bean, @NotNull NodeKey key) {
        Preconditions.checkState(isNode, "setKey() supported only by node mappings");
        keyMapping.writeBeanValue(bean, key); // FIXME: really read directly?
    }

    @NotNull
    public Object newBeanInstance() {
        try {
            return beanClass.newInstance();
        }
        catch ( InstantiationException e ) {
            throw new InvalidBeanException(beanClass, "Error invoking public zero-argument constructor", e);
        }
        catch ( IllegalAccessException e ) {
            throw new InvalidBeanException(beanClass, "Error invoking public zero-argument constructor", e);
        }
    }

    public void created(@NotNull Object bean, @NotNull PropertyContainer entity) {
        // FIXME: post-create actions (=> @RootRelationship)
    }

    public void store(@NotNull Object bean, @NotNull PropertyContainer entity) {
        // assumes the key has been written already
        for ( AbstractPropertyMapping property : propertyMappings.values() ) {
            property.beanToEntity(entity, bean);
        }
    }

    public void read(@NotNull Object bean, @NotNull PropertyContainer entity) {
        keyMapping.entityToBean(entity, bean);
        for ( AbstractPropertyMapping property : propertyMappings.values() ) {
            property.entityToBean(entity, bean);
        }
    }

    public void beforeCreate() {
        // FIXME: before-create actions (=> @BeforeCreate)
    }

    public void beforeUpdate(boolean creating) {
        // FIXME: before-update actions (=> @BeforeUpdate)
    }

    public void beforeDelete() {
        // FIXME: before-delete actions (=> @BeforeDelete)
    }

    public void afterRead() {
        // FIXME: after-read actions (=> @AfterRead)
    }

}
