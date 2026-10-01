package fu.ats.application.port.out;

import fu.ats.domain.aggregate.CandidateAggregate;

public interface SaveCandidatePort {
    CandidateAggregate save(CandidateAggregate aggregate);
}
