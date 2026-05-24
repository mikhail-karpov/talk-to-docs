package com.mikhailkarpov.docs.config;

import com.mikhailkarpov.docs.auth.User;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockAuthenticatedUserSecurityContextFactory implements WithSecurityContextFactory<WithMockAuthenticatedUser> {

    @Override
    @NonNull
    public SecurityContext createSecurityContext(WithMockAuthenticatedUser annotation) {

        var user = new User(annotation.id(), "test@example.com", "test-password", "test", "test");

        Authentication authentication = new TestingAuthenticationToken(user, null, user.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
