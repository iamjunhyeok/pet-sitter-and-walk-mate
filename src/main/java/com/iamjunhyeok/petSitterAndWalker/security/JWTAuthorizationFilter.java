package com.iamjunhyeok.petSitterAndWalker.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.iamjunhyeok.petSitterAndWalker.constants.Security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

public class JWTAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(Security.AUTHORIZATION);
        if (header == null || !header.startsWith(Security.BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.replace(Security.BEARER, "");
        String user = JWT.require(Algorithm.HMAC512(Security.SECRET_KEY))
                .build()
                .verify(token)
                .getSubject();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Arrays.asList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
