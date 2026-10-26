package fu.ats.service;

import fu.ats.dto.CvAnalysisRequest;
import fu.ats.dto.CvAnalysisResponse;
import fu.ats.dto.CvMatchRequest;
import fu.ats.dto.CvMatchResponse;

public interface CvParseService {

    CvAnalysisResponse analyze(CvAnalysisRequest request);

    CvMatchResponse match(CvMatchRequest request);
}
