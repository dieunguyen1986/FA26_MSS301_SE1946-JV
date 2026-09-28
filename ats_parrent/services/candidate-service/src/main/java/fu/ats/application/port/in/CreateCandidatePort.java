package fu.ats.application.port.in;

import fu.ats.application.command.CandidateCommand;
import fu.ats.domain.aggregate.CandidateAggregate;

public interface CreateCandidatePort {
    CandidateAggregate execute(CandidateCommand command);
}
