package fu.ats.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class HeaderFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Header Filter");
        ServerHttpRequest request = exchange.getRequest();

        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        log.info("token: {}", token);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Constants.HEADER_ORDER;
    }
}
