package fu.ats.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${app.oidc.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${app.oidc.issuer}")
    private String issuer;

    @Value("${app.oidc.client-id}")
    private String clientId;

    public SecurityWebFilterChain configure(ServerHttpSecurity http) {
        return http.cors(Customizer.withDefaults())
                .csrf((csrf) -> csrf.disable())
                .authorizeExchange(exchange -> {
                    exchange.pathMatchers("/public/**").permitAll()
                            .pathMatchers("/actuator/health").permitAll()
                            .anyExchange().authenticated();
                }).build();


    }

    @Bean
    ReactiveJwtDecoder jwtDecoder() {
        NimbusReactiveJwtDecoder decoder =
                NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();

        // 1. chữ ký do decoder kiểm tra; validator này kiểm tra exp, nbf và iss
        OAuth2TokenValidator<Jwt> withIssuer =
                JwtValidators.createDefaultWithIssuer(issuer);

        // 2. aud phải chứa Client ID của CHÍNH ỨNG DỤNG, nếu không token được cấp cho ứng dụng khác
        OAuth2TokenValidator<Jwt> withAudience =
                new JwtClaimValidator<List<String>>(
                        "aud",
                        aud -> aud != null && aud.contains(clientId));

        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience));
        return decoder;
    }
}
