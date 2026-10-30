package fu.ats.storage;

public class CvFileReadException extends RuntimeException {

    public CvFileReadException(String message) {
        super(message);
    }

    public CvFileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
