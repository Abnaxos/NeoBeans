package ch.raffael.neobeans.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.jetbrains.annotations.NotNull;

import ch.raffael.neobeans.InvalidBeanException;
import ch.raffael.neobeans.NodeKey;
import ch.raffael.neobeans.RelationshipKey;

import static ch.raffael.util.common.NotImplementedException.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class AnnotatedBeanMappingFactory implements BeanMappingFactory {

    public AnnotatedBeanMappingFactory() {
    }

    @NotNull
    @Override
    public Object mappingKey(@NotNull Object bean) {
        return bean.getClass();
    }

    @NotNull
    @Override
    public BeanMapping createBeanMapping(@NotNull Object bean) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(bean.getClass());
        }
        catch ( IntrospectionException e ) {
            throw new InvalidBeanException(bean.getClass(), "Error during bean introspection", e);
        }
        PropertyDescriptor keyProperty = null;
        for ( PropertyDescriptor prop : beanInfo.getPropertyDescriptors() ) {
            if ( prop.getPropertyType().equals(NodeKey.class) || prop.getPropertyType().equals(RelationshipKey.class) ) {
                if ( keyProperty != null ) {
                    throw new InvalidBeanException(bean.getClass(), "Found more than one key property: " + keyProperty.getName() + ", " + prop.getName());
                }
                keyProperty = prop;
            }
        }
        if ( keyProperty == null ) {
            throw new InvalidBeanException(bean.getClass(), "Missing key property");
        }
        // FIXME: create delegate
        return notImplemented();
    }
}
