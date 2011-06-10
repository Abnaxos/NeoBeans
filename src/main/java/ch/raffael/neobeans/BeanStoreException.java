package ch.raffael.neobeans;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class BeanStoreException extends RuntimeException {

    public BeanStoreException() {
    }

    public BeanStoreException(String message) {
        super(message);
    }

    public BeanStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanStoreException(Throwable cause) {
        super(cause);
    }
}
