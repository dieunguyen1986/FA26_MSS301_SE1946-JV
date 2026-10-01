package fu.ats.exception;

public class BusinessException extends RuntimeException {
    private int type;
    private String message;
    private String code;

    public BusinessException() {
    }

    public BusinessException(int type, String message) {
        this.type = type;
        this.message = message;
    }
}
