package ch.raffael.neobeans;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class InconsistencyException extends BeanStoreException {

    public InconsistencyException() {
        super();
    }

    public InconsistencyException(String message) {
        super(message);
    }

    public InconsistencyException(Throwable cause) {
        super(cause);
    }

    public InconsistencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
