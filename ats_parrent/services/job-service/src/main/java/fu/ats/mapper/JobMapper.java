package fu.ats.mapper;

import fu.ats.dto.JobRequest;
import fu.ats.dto.JobResponse;
import fu.ats.entity.Job;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") //
public interface JobMapper {
    Job toEntity(JobRequest request);

    JobResponse fromEntity(Job job);
}
