package fu.ats.config;

import feign.RequestInterceptor;
import fu.ats.security.GatewaySecurityHeaders;
import fu.ats.security.GatewayUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration(proxyBeanMethods = false)
public class GatewayIdentityFeignConfig {

    @Bean
    RequestInterceptor gatewayIdentityRequestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof GatewayUser user)) {
                return;
            }

            requestTemplate.header(GatewaySecurityHeaders.USER_ID, user.subject());
            setHeader(requestTemplate, GatewaySecurityHeaders.USER_EMAIL, user.email());
            setHeader(requestTemplate, GatewaySecurityHeaders.USER_NAME, user.name());
            if (!user.roles().isEmpty()) {
                requestTemplate.header(GatewaySecurityHeaders.USER_ROLES, String.join(",", user.roles()));
            }
        };
    }

    private void setHeader(feign.RequestTemplate requestTemplate, String name, String value) {
        if (value != null && !value.isBlank()) {
            requestTemplate.header(name, value);
        }
    }
}
