package com.nigahae.security.jwt;

import com.nigahae.security.util.CookieUtil;
import com.nigahae.user.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/api/")) {
            cookieUtil.getCookie(request, CookieUtil.ACCESS_TOKEN)
                    .filter(token -> jwtTokenProvider.validateToken(token, JwtTokenProvider.ACCESS))
                    .ifPresentOrElse(token -> {
                Long userId = jwtTokenProvider.getUserId(token);
                String nickname = jwtTokenProvider.getNickname(token);
                var authentication = new UsernamePasswordAuthenticationToken(
                        new AuthenticatedUser(userId, nickname),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                    }, SecurityContextHolder::clearContext);
        }
        chain.doFilter(request, response);
    }
}
