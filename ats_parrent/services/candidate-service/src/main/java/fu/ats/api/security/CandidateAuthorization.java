package fu.ats.api.security;

import fu.ats.infrastructure.persistence.repository.CandidateJpaRepository;
import fu.ats.security.GatewayUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component("candidateAuthorization")
@RequiredArgsConstructor
public class CandidateAuthorization {

    private final CandidateJpaRepository candidateJpaRepository;

    public boolean canRead(Long candidateId, Authentication authentication) {
        if (hasRole(authentication, "ROLE_RECRUITER") || hasRole(authentication, "ROLE_ADMIN")) {
            return true;
        }

        if (!hasRole(authentication, "ROLE_CANDIATE")
                || !(authentication.getPrincipal() instanceof GatewayUser user)) {
            return false;
        }

        return candidateJpaRepository.findById(candidateId)
                .map(candidate -> candidate.getUserId() != null
                        && candidate.getUserId().toString().equals(user.subject()))
                .orElse(false);
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication != null
                && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }
}
