package fu.ats.application.port.in;

import fu.ats.api.dto.CandidateResponse;
import fu.ats.application.query.CandidateQuery;
import fu.ats.domain.aggregate.CandidateAggregate;

public interface FindCandidateUseCase {
    CandidateResponse execute(CandidateQuery query);
}
