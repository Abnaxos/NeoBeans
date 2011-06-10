package ch.raffael.neobeans;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class InvalidBeanException extends BeanStoreException {

    private final Class<?> beanClass;

    public InvalidBeanException(Class<?> beanClass, String message) {
        super(beanClass + ": " + message);
        this.beanClass = beanClass;
    }

    public InvalidBeanException(Class<?> beanClass, String message, Throwable cause) {
        super(beanClass + ": " + message, cause);
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }
}
