package fu.ats.service;

import fu.ats.dto.JobRequest;
import fu.ats.dto.JobResponse;
import fu.ats.entity.Department;
import fu.ats.entity.Job;
import fu.ats.entity.JobStatus;
import fu.ats.exception.BusinessException;
import fu.ats.mapper.JobMapper;
import fu.ats.repository.DepartmentRepository;
import fu.ats.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final DepartmentRepository departmentRepository;
    private final JobMapper jobMapper;


    @Override
    public JobResponse save(JobRequest request) {
        Job job = jobMapper.toEntity(request);

        // Validate
        Department department = departmentRepository.findById(request.getDepartmentId()).orElse(null);
        if (department != null) {
            job.setDepartment(department);
        }

        if (job.getSalaryMax().compareTo(job.getSalaryMin()) < 0) {
            throw new BusinessException(1, "Max sal must be greater than or equal min sal!");
        }

        if (jobRepository.existsByTitle(job.getTitle())) {
            throw new BusinessException(2, "Title must be not duplicate");
        }

        job.setStatus(JobStatus.DRAFT);

        return jobMapper.fromEntity(jobRepository.save(job));
    }

    @Override
    public JobResponse findById(UUID uuid) {
        Job job = jobRepository.findById(uuid).orElseThrow(() -> new BusinessException(2, "Job not found"));


        return jobMapper.fromEntity(job);
    }

}
