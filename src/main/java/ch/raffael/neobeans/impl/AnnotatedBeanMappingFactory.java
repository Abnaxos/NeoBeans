package ch.raffael.neobeans.impl;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.GraphDatabaseService;

import ch.raffael.neobeans.Converter;
import ch.raffael.neobeans.InvalidBeanException;
import ch.raffael.neobeans.NodeKey;
import ch.raffael.neobeans.annotations.Transient;
import ch.raffael.neobeans.annotations.UseConverter;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class AnnotatedBeanMappingFactory implements BeanMappingFactory {

    @SuppressWarnings( { "UnusedDeclaration" })
    private final Logger log = LoggerFactory.getLogger(getClass());

    public AnnotatedBeanMappingFactory() {
    }

    @NotNull
    @Override
    public Object mappingKey(@NotNull Object bean) {
        return bean.getClass();
    }

    @NotNull
    @Override
    public BeanMapping createBeanMapping(@NotNull GraphDatabaseService database, @NotNull Object bean) {
        // FIXME: handle relations
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(bean.getClass());
        }
        catch ( IntrospectionException e ) {
            throw new InvalidBeanException(bean.getClass(), "Error during bean introspection", e);
        }
        BeanMapping.KeyAdapter keyAdapter = null;
        final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        final List<BeanPropertyMapping> propertyMappings = new LinkedList<BeanPropertyMapping>();
        for ( PropertyDescriptor prop : propertyDescriptors ) {
            BeanPropertyMappingFactory factory = new BeanPropertyMappingFactory(database, bean.getClass(), prop);
            if ( factory.isMappable() ) {
                if ( factory.isKey() ) {
                    if ( keyAdapter != null ) {
                        throw new InvalidBeanException(bean.getClass(), "Duplicate bean property: " + prop.getName());
                    }
                    keyAdapter = factory.createKeyAdapter();
                }
                else {
                    propertyMappings.add(factory.createMapping());
                }
            }
        }
        if ( keyAdapter == null ) {
            throw new InvalidBeanException(bean.getClass(), "Missing key property");
        }
        BeanMapping beanMapping = new BeanMapping(true, bean.getClass(), keyAdapter);
        for ( BeanPropertyMapping mapping : propertyMappings ) {
            beanMapping.addPropertyMapping(mapping);
        }
        return beanMapping;
    }

    private class BeanPropertyMappingFactory {

        private final GraphDatabaseService database;
        private final Class<?> beanClass;
        private final PropertyDescriptor propertyDescriptor;

        private boolean mappable;

        private Converter converter = null;

        private BeanPropertyMappingFactory(GraphDatabaseService database, Class<?> beanClass, PropertyDescriptor propertyDescriptor) {
            this.database = database;
            this.beanClass = beanClass;
            if ( propertyDescriptor instanceof IndexedPropertyDescriptor ) {
                // FIXME: throw?
                log.error("{}: Non-transient property {} not supported (is indexed)", beanClass, propertyDescriptor.getName());
                mappable = false;
            }
            else if ( propertyDescriptor.getName().equals("class") ) {
                // skip silently
                mappable = false;
            }
            else if ( propertyDescriptor.getReadMethod() == null || propertyDescriptor.getWriteMethod() == null ) {
                // FIXME: throw?
                log.error("{}: Non-transient property {} must be readable and writable", beanClass, propertyDescriptor.getName());
                mappable = false;
            }
            else if ( propertyDescriptor.getReadMethod().getAnnotation(Transient.class) != null || propertyDescriptor.getWriteMethod().getAnnotation(Transient.class) != null ) {
                mappable = false;
            }
            else {
                mappable = true;
            }
            this.propertyDescriptor = propertyDescriptor;
        }

        private boolean isMappable() {
            return mappable;
        }

        private boolean isKey() {
            return mappable && (propertyDescriptor.getPropertyType() == NodeKey.class);
        }

        private BeanPropertyMapping.BeanPropertyKeyAdapter createKeyAdapter() {
            Preconditions.checkState(isKey(), "Property " + propertyDescriptor.getName() + " is not a key, cannot create key adapter");
            if ( isAnnotated(UseConverter.class) ) {
                throw new InvalidBeanException(beanClass, "Key property " + propertyDescriptor.getName() + " cannot be further configured");
            }
            return new BeanPropertyMapping.BeanPropertyKeyAdapter(propertyDescriptor.getReadMethod(), propertyDescriptor.getWriteMethod());
        }

        private BeanPropertyMapping createMapping() {
            Preconditions.checkState(!isKey(), "Property " + propertyDescriptor.getName() + " is a key, cannot create property mapping");
            if ( !mappable ) {
                return null;
            }
            UseConverter converterAnnotation = getAnnotation(UseConverter.class);
            if ( converterAnnotation != null ) {
                try {
                    converter = (Converter)converterAnnotation.value().newInstance();
                }
                catch ( InstantiationException e ) {
                    throw new InvalidBeanException(beanClass, "Cannot instanciate converter " + converterAnnotation.value(), e);
                }
                catch ( IllegalAccessException e ) {
                    throw new InvalidBeanException(beanClass, "Cannot instanciate converter " + converterAnnotation.value(), e);
                }
            }
            // FIXME: configure index
            return new BeanPropertyMapping(database, propertyDescriptor.getName(),
                                           converter, null,
                                           propertyDescriptor.getReadMethod(), propertyDescriptor.getWriteMethod());
        }

        private boolean isAnnotated(Class<? extends Annotation> annotationClass) {
            return propertyDescriptor.getReadMethod().getAnnotation(annotationClass) != null
                    || propertyDescriptor.getWriteMethod().getAnnotation(annotationClass) != null;
        }

        private <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
            T readAnnotation = propertyDescriptor.getReadMethod().getAnnotation(annotationClass);
            T writeAnnotation = propertyDescriptor.getWriteMethod().getAnnotation(annotationClass);
            if ( readAnnotation != null && writeAnnotation != null ) {
                throw new InvalidBeanException(beanClass, "Conflicting annotation " + annotationClass);
            }
            else if ( readAnnotation != null ) {
                return readAnnotation;
            }
            else {
                return writeAnnotation;
            }
        }

    }

}
