package fu.ats.storage;

import fu.ats.config.CvParseProperties;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Reads a CV as UTF-8 text.
 *
 * <p>This is intentionally not an OCR/PDF parser. For the student demo, the
 * uploaded object should be a text file, or a file whose content is already
 * plain text. A later version can replace this class with PDF/DOCX readers.</p>
 */
@Service
public class MinioCvFileReader {

    private final MinioClient minioClient;
    private final CvParseProperties properties;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public MinioCvFileReader(
            MinioClient minioClient,
            CvParseProperties properties
    ) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    public String readText(String filePath) {
        if (isHttpUrl(filePath)) {
            return readPresignedUrl(filePath);
        }

        ObjectLocation location = resolveLocation(filePath);
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(location.bucket())
                        .object(location.objectName())
                        .build())) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new CvFileReadException(
                    "Cannot read CV object " + filePath,
                    exception
            );
        }
    }

    public String fileName(String filePath) {
        String path = filePath;
        if (isHttpUrl(filePath)) {
            path = URI.create(filePath).getPath();
        } else if (filePath.startsWith("s3://") || filePath.startsWith("minio://")) {
            path = URI.create(filePath).getPath();
        }

        int lastSlash = path.lastIndexOf('/');
        String name = lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
        return URLDecoder.decode(name, StandardCharsets.UTF_8);
    }

    private ObjectLocation resolveLocation(String filePath) {
        if (filePath.startsWith("s3://") || filePath.startsWith("minio://")) {
            URI uri = URI.create(filePath);
            String objectName = removeLeadingSlash(uri.getPath());
            if (uri.getHost() == null || objectName.isBlank()) {
                throw new CvFileReadException(
                        "MinIO path must look like s3://bucket/object-key"
                );
            }
            return new ObjectLocation(uri.getHost(), objectName);
        }

        return new ObjectLocation(
                properties.minio().bucket(),
                removeLeadingSlash(filePath)
        );
    }

    private String readPresignedUrl(String filePath) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(filePath))
                    .GET()
                    .build();
            HttpResponse<byte[]> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofByteArray()
            );
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException(
                        "HTTP status " + response.statusCode()
                );
            }
            return new String(response.body(), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new CvFileReadException(
                    "Cannot download CV URL " + filePath,
                    exception
            );
        }
    }

    private boolean isHttpUrl(String filePath) {
        return filePath.startsWith("http://") || filePath.startsWith("https://");
    }

    private String removeLeadingSlash(String value) {
        return value.startsWith("/") ? value.substring(1) : value;
    }

    private record ObjectLocation(String bucket, String objectName) {
    }
}
