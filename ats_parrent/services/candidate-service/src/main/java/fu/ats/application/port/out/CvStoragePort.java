package fu.ats.application.port.out;

import java.io.InputStream;

public interface CvStoragePort {

    String upload(
            Long candidateId,
            String originalFileName,
            String contentType,
            long sizeBytes,
            InputStream content
    );

    void delete(String filePath);
}
