package fu.ats.application.service;

import fu.ats.api.dto.CandidateResponse;
import fu.ats.application.port.in.FindCandidateUseCase;
import fu.ats.application.port.out.FindCandidatePort;
import fu.ats.application.query.CandidateQuery;
import fu.ats.domain.aggregate.CandidateAggregate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindCandidate implements FindCandidateUseCase {
    private final FindCandidatePort findCandidatePort;

    @Override
    public CandidateResponse execute(CandidateQuery query) {

        return toResponse(findCandidatePort.findById(query.id()).orElseThrow(() -> {
            throw new IllegalArgumentException("Candidate not found with id " + query.id());
        }));
    }

    private CandidateResponse toResponse(CandidateAggregate aggregate) {
        CandidateResponse response = new CandidateResponse();
        response.setFullName(aggregate.getFullName());
        response.setStatus(aggregate.getStatus().toString());
        response.setSource(aggregate.getSource());
        response.setUtmCampaign(aggregate.getUtmCampaign());
        response.setUtmSource(aggregate.getUtmSource());
        response.setUtmMedium(aggregate.getUtmMedium());

        return response;
    }
}
