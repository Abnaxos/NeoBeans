package ch.raffael.neobeans;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ConvertException extends BeanStoreException {

    public ConvertException(Object value) {
        super(message(value, null, null));
    }

    public ConvertException(Object value, String message) {
        super(message(value, message, null));
    }

    public ConvertException(Object value, String message, Throwable cause) {
        super(message(value, message, cause), cause);
    }

    public ConvertException(Object value, Throwable cause) {
        super(message(value, null, cause), cause);
    }

    private static String message(Object value, String message, Throwable cause) {
        if ( message == null && cause != null ) {
            message = cause.toString();
        }
        return "Cannot convert " + value + (message != null ? ": " + message : "");
    }

}
