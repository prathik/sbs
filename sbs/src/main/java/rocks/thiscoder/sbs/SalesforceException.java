package rocks.thiscoder.sbs;

/**
 * @author prathik.raj
 */
public class SalesforceException extends Exception {
    public SalesforceException(Throwable e) {
        super(e);
    }

    public SalesforceException(String e) {
        super(e);
    }
}
