package fu.ats.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //
        log.info("LoggingFilter started");
        ServerHttpRequest request = exchange.getRequest();
        List<String> correlatioId = request.getHeaders().get(Constants.CORRELATION_ID);

        log.info("correlatioId: {}", correlatioId);

        ServerHttpRequest mutateRequest = null;

        if (correlatioId == null || correlatioId.isEmpty()) {
            mutateRequest= request.mutate().header(Constants.CORRELATION_ID, UUID.randomUUID().toString()).build();
        }

        return chain.filter(exchange.mutate().request(mutateRequest).build());
    }

    @Override
    public int getOrder() {
        return Constants.LOGGING_ORDER;
    }
}
