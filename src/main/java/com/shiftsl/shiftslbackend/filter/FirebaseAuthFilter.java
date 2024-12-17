package com.shiftsl.shiftslbackend.filter;

import com.shiftsl.shiftslbackend.service.FirebaseAuthService;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component  // Ensure this class is recognized by Spring
@Slf4j  // Lombok logger
public class FirebaseAuthFilter extends OncePerRequestFilter {

    @Autowired
    private FirebaseAuthService firebaseAuthService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract JWT token from Authorization header
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String idToken = authorizationHeader.substring(7);

            Optional<FirebaseToken> decodedToken = firebaseAuthService.verifyIdToken(idToken);

            if (decodedToken.isPresent()) {
                // Token is valid, continue processing the request
                request.setAttribute("firebaseToken", decodedToken.get());
            } else {
                log.error("Invalid or expired token: {}", idToken);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if necessary
    }
}

