package br.ufscar.ppgcc.domain.security;

import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class SecurityService {

    private final AuthenticationContext authenticationContext;
    private final AccessAnnotationChecker accessAnnotationChecker;

    public SecurityService(AuthenticationContext authenticationContext,
                           AccessAnnotationChecker accessAnnotationChecker) {
        this.authenticationContext = authenticationContext;
        this.accessAnnotationChecker = accessAnnotationChecker;
    }

    public UserDetails getAuthenticatedUser() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class).orElse(null);
    }

    public void logout() {
        authenticationContext.logout();
    }

    public boolean hasAccessTo(Class<?> view) {
        return accessAnnotationChecker.hasAccess(view);
    }

    public boolean isCarrier() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(UserDetails::getAuthorities).orElse(Collections.emptySet())
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(it -> it.equals("ROLE_CARRIER"));
    }

}