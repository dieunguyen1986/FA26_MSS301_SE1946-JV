package fu.ats.exception;

public class DownStreamUnavailableException extends  RuntimeException {
    private String serviceName;
    public  DownStreamUnavailableException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }
}
