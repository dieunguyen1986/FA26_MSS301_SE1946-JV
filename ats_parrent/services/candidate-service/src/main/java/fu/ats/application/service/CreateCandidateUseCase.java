package fu.ats.application.service;

import fu.ats.application.command.CandidateCommand;
import fu.ats.application.port.in.CreateCandidatePort;
import fu.ats.domain.aggregate.CandidateAggregate;
import fu.ats.domain.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCandidateUseCase implements CreateCandidatePort {
    private  final CandidateRepository candidateRepository;
    @Override
    public CandidateAggregate execute(CandidateCommand command) {
        // map cmd -> aggregate root
        CandidateAggregate aggregate = CandidateAggregate.create(command);

        // Business Validate

        // call doamin/repository
       return candidateRepository.save(aggregate);

    }
}
