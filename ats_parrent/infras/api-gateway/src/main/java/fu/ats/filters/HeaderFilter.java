package fu.ats.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Component
@Slf4j
public class HeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Header Filter");
        ServerHttpRequest request = exchange.getRequest();

        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        log.info("token: {}", token);

        ServerWebExchange serverWebExchange = exchange.mutate().request(new ComsumerImpl()).build();


        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .map(auth -> ((JwtAuthenticationToken) auth).getToken())
                .map(jwt -> {

                    log.info("jwt: {}", jwt);

                    ServerHttpRequest httpRequest = serverWebExchange.getRequest().mutate()
                            .headers(headers -> {
                                // Downstream services do not need the raw token
                                headers.remove(HttpHeaders.AUTHORIZATION);

                                headers.set(Constants.USER_ID, jwt.getSubject());

                                String email = jwt.getClaimAsString("email");
                                log.info("email: {}", email);
                                if (email != null) {
                                    headers.set(Constants.USER_EMAIL, email);
                                }

                                if (jwt.getClaimAsString("name") != null) {
                                    headers.set(Constants.USER_NAME, jwt.getClaimAsString("name"));
                                }
                            })
                            .build();
                    return serverWebExchange.mutate().request(httpRequest).build();

                })
                .defaultIfEmpty(serverWebExchange)
                .flatMap(chain::filter);

    }

    @Override
    public int getOrder() {
        return Constants.HEADER_ORDER;
    }
}


class ComsumerImpl implements Consumer<ServerHttpRequest.Builder> {
    @Override
    public void accept(ServerHttpRequest.Builder request) {
        request.build().getHeaders().remove(Constants.USER_ID);
        request.build().getHeaders().remove(Constants.USER_EMAIL);

        request.build().getHeaders().remove(Constants.USER_NAME);

    }
}

