package fu.ats.application.exception;

public class CandidateAlreadyExistsException extends RuntimeException {

    public CandidateAlreadyExistsException(String message) {
        super(message);
    }
}
