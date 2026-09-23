package fu.ats.service;

import fu.ats.dto.JobRequest;
import fu.ats.dto.JobResponse;
import fu.ats.entity.Job;

import java.util.UUID;

public interface JobService {
    JobResponse save(JobRequest request);

    JobResponse findById(UUID uuid);
}
