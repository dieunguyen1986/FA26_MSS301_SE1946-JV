package fu.ats.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .authorizeExchange(auth -> auth
                        .pathMatchers("/public/**", "/actuator/health").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

    /*
     * OLD CUSTOM APPROACH - kept here for training reference.
     *
     * Spring Security can validate the JWT automatically when
     * spring.security.oauth2.resourceserver.jwt.issuer-uri is configured.
     * The old code manually configured the JWK URL, issuer validator and
     * optional audience validator, so it is not needed for the simple setup.
     *
     * @Value("${app.oidc.jwk-set-uri}")
     * private String jwkSetUri;
     *
     * @Value("${app.oidc.issuer}")
     * private String issuer;
     *
     * @Value("${app.oidc.audience:}")
     * private String audience;
     *
     * @Bean
     * ReactiveJwtDecoder jwtDecoder() {
     *     NimbusReactiveJwtDecoder decoder =
     *             NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
     *
     *     OAuth2TokenValidator<Jwt> issuerValidator =
     *             JwtValidators.createDefaultWithIssuer(issuer);
     *
     *     decoder.setJwtValidator(issuerValidator);
     *     return decoder;
     * }
     */
}
