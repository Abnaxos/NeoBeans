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
public class AnnotatedBeanStorageDelegateFactory implements StorageDelegateFactory {

    @NotNull
    @Override
    public StorageDelegate createStorageDelegate(@NotNull Class<?> beanClass) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(beanClass);
        }
        catch ( IntrospectionException e ) {
            throw new InvalidBeanException(beanClass, "Error during bean introspection", e);
        }
        PropertyDescriptor keyProperty = null;
        for ( PropertyDescriptor prop : beanInfo.getPropertyDescriptors() ) {
            if ( prop.getPropertyType().equals(NodeKey.class) || prop.getPropertyType().equals(RelationshipKey.class) ) {
                if ( keyProperty != null ) {
                    throw new InvalidBeanException(beanClass, "Found more than one key property: " + keyProperty.getName() + ", " + prop.getName());
                }
                keyProperty = prop;
            }
        }
        if ( keyProperty == null ) {
            throw new InvalidBeanException(beanClass, "Missing key property");
        }
        // FIXME: create delegate
        return notImplemented();
    }
}
