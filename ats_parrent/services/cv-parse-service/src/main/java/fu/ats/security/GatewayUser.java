package fu.ats.security;

import java.util.Set;

/** Identity established by API Gateway and stored as the authenticated principal. */
public record GatewayUser(
        String subject,
        String email,
        String name,
        Set<String> roles
) {
    public GatewayUser {
        roles = Set.copyOf(roles);
    }
}
