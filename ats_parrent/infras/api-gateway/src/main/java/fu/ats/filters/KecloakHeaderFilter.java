package fu.ats.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class KecloakHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {
        // Never trust identity headers sent by the client.
        ServerWebExchange cleanExchange = exchange.mutate()
                .request(request -> request.headers(headers -> {
                    headers.remove(HttpHeaders.AUTHORIZATION);
                    headers.remove(Constants.USER_ID);
                    headers.remove(Constants.USER_EMAIL);
                    headers.remove(Constants.USER_NAME);
                    headers.remove(Constants.USER_ROLES);
                }))
                .build();

        return ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication())
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .map(authentication -> {
                    Jwt jwt = authentication.getToken();

                    return cleanExchange.mutate()
                            .request(request -> request.headers(headers -> {
                                if (jwt.getSubject() != null) {
                                    headers.set(Constants.USER_ID, jwt.getSubject());
                                }

                                String email = jwt.getClaimAsString("email");
                                if (email != null) {
                                    headers.set(Constants.USER_EMAIL, email);
                                }

                                String username = jwt.getClaimAsString("preferred_username");
                                if (username == null) {
                                    username = jwt.getClaimAsString("name");
                                }
                                if (username != null) {
                                    headers.set(Constants.USER_NAME, username);
                                }

                                String roles = extractRoles(jwt);
                                if (!roles.isBlank()) {
                                    headers.set(Constants.USER_ROLES, roles);
                                }
                            }))
                            .build();
                })
                // SecurityConfig decides whether the request is authenticated.
                .defaultIfEmpty(cleanExchange)
                .flatMap(chain::filter);
    }

    private String extractRoles(Jwt jwt) {
        Set<String> roles = new LinkedHashSet<>();
        List<Object> roleSources = new ArrayList<>();

        roleSources.add(jwt.getClaim("realm_access"));

        Object resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess instanceof Map<?, ?> clients) {
            roleSources.addAll(clients.values());
        }

        for (Object source : roleSources) {
            if (source instanceof Map<?, ?> access
                    && access.get("roles") instanceof Collection<?> values) {
                values.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .filter(role -> !role.isBlank())
                        .forEach(roles::add);
            }
        }

        return String.join(",", roles);
    }

    @Override
    public int getOrder() {
        return 200;
    }
}
