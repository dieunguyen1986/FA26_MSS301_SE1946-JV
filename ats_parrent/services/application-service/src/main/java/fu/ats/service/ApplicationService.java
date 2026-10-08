package fu.ats.service;

import fu.ats.dto.ApplicationRequest;
import fu.ats.dto.ApplicationResponse;

public interface ApplicationService {
    ApplicationResponse create(ApplicationRequest request);
}
