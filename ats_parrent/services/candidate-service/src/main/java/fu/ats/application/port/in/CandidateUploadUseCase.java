package fu.ats.application.port.in;

import fu.ats.api.dto.CandidateResponse;
import fu.ats.api.dto.CandidateUploadRequest;

import java.util.UUID;

public interface CandidateUploadUseCase {

    CandidateResponse registerForCurrentUser(
            UUID currentUserId,
            CandidateUploadRequest request
    );

    CandidateResponse createByRecruiter(
            CandidateUploadRequest request
    );
}
