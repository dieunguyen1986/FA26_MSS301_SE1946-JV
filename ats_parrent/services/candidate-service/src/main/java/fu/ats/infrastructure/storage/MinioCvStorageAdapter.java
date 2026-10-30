package fu.ats.infrastructure.storage;

import fu.ats.application.port.out.CvStoragePort;
import fu.ats.application.exception.CvStorageException;
import fu.ats.config.CandidateStorageProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MinioCvStorageAdapter implements CvStoragePort {

    private static final int STREAM_PART_SIZE = 10 * 1024 * 1024;

    private final MinioClient minioClient;
    private final CandidateStorageProperties properties;

    @Override
    public String upload(
            Long candidateId,
            String originalFileName,
            String contentType,
            long sizeBytes,
            InputStream content
    ) {
        try {
            ensureBucketExists();

            String objectName = "candidates/"
                    + candidateId
                    + "/cvs/"
                    + UUID.randomUUID()
                    + "-"
                    + safeFileName(originalFileName);

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectName)
                    .stream(content, sizeBytes, STREAM_PART_SIZE)
                    .contentType(contentType)
                    .build());

            return "s3://" + properties.bucket() + "/" + objectName;
        } catch (Exception exception) {
            throw new CvStorageException("Cannot upload CV to MinIO", exception);
        }
    }

    @Override
    public void delete(String filePath) {
        ObjectLocation location = resolve(filePath);
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(location.bucket())
                    .object(location.objectName())
                    .build());
        } catch (Exception exception) {
            throw new CvStorageException("Cannot delete CV from MinIO", exception);
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(properties.bucket())
                .build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(properties.bucket())
                    .build());
        }
    }

    private ObjectLocation resolve(String filePath) {
        URI uri = URI.create(filePath);
        if (!"s3".equals(uri.getScheme()) || uri.getHost() == null) {
            throw new IllegalArgumentException("CV path must look like s3://bucket/object-key");
        }

        String objectName = uri.getPath();
        if (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        return new ObjectLocation(uri.getHost(), objectName);
    }

    private String safeFileName(String originalFileName) {
        String fileName = originalFileName == null || originalFileName.isBlank()
                ? "cv-file"
                : originalFileName;
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private record ObjectLocation(String bucket, String objectName) {
    }
}
