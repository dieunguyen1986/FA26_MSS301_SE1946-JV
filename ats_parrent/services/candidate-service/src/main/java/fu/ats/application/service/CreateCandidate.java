package fu.ats.application.service;

import fu.ats.application.command.CandidateCommand;
import fu.ats.application.port.out.SaveCandidatePort;
import fu.ats.domain.aggregate.CandidateAggregate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCandidate implements fu.ats.application.port.in.CreateCandidateUseCase {
    private final SaveCandidatePort saveCandidatePort;

    @Override
    public CandidateAggregate execute(CandidateCommand command) {
        // map cmd -> aggregate root
        CandidateAggregate aggregate = CandidateAggregate.create(command);

        // Business Validate

        // call doamin/repository
        return saveCandidatePort.save(aggregate);

    }
}
