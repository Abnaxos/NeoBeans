package ch.raffael.neobeans;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class NeoBeansException extends RuntimeException {

    public NeoBeansException() {
    }

    public NeoBeansException(String message) {
        super(message);
    }

    public NeoBeansException(String message, Throwable cause) {
        super(message, cause);
    }

    public NeoBeansException(Throwable cause) {
        super(cause);
    }
}
