package fu.ats.client.job;

import fu.ats.dto.JobView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "job-service", url = "${job-service.url}", configuration =JobFeignConfig.class)
public interface JobClient {

    @GetMapping("/api/v1/jobs/{id}")
    JobView findById(@PathVariable("id")UUID id);
}
