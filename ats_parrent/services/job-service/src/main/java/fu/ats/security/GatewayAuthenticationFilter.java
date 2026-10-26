package fu.ats.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static fu.ats.security.GatewaySecurityHeaders.*;

@Slf4j
public final class GatewayAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String subject = request.getHeader(USER_ID);
        if (subject != null && !subject.isBlank()) {
            Set<String> roles = parseRoles(request.getHeader(USER_ROLES));
            GatewayUser user = new GatewayUser(
                    subject,
                    request.getHeader(USER_EMAIL),
                    request.getHeader(USER_NAME),
                    roles
            );

            log.info("Gateway User {} - {}", user.email(), user.roles());

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            roles.stream()
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                    .toList()
                    )
            );
        }

        filterChain.doFilter(request, response);
    }

    private Set<String> parseRoles(String rawRoles) {
        if (rawRoles == null || rawRoles.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(rawRoles.split(","))
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                .map(role -> role.toUpperCase(Locale.ROOT))
                .map(role -> "CANDIDATE".equals(role) ? "CANDIATE" : role)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
