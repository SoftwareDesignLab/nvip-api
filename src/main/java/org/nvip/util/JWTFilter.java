package org.nvip.util;

import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final UserAuthProvider userAuthProvider;

    /**
     * HTTP filter to intercept requests and validate the JSON Web Token (JWT).
     * @param request - HTTP request
     * @param response - HTTP response
     * @param filterChain - filter chain
     * @throws ServletException - if a servlet-specific error occurs
     * @throws IOException - if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // check for Authorization header
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // extract token from header
            String token = authorizationHeader.substring(7);
            // validate token
            try {
                SecurityContextHolder.getContext().setAuthentication(userAuthProvider.validateToken(token));
            } catch (TokenExpiredException e) {
                // if token is expired, clear the security context
                SecurityContextHolder.clearContext();
                throw new AppException("Token expired.", HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                // if token is invalid, clear the security context
                SecurityContextHolder.clearContext();
                throw e;
            }
        }
        // continue filter chain
        filterChain.doFilter(request, response);
    }
}
