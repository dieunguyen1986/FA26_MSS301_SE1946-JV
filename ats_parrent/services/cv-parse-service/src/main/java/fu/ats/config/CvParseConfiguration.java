package fu.ats.config;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CvParseProperties.class)
public class CvParseConfiguration {

    @Bean
    MinioClient minioClient(CvParseProperties properties) {
        CvParseProperties.MinioProperties minio = properties.minio();

        return MinioClient.builder()
                .endpoint(minio.endpoint())
                .credentials(minio.accessKey(), minio.secretKey())
                .build();
    }
}
