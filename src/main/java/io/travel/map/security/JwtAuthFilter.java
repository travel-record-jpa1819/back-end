package io.travel.map.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        log.info("JWT 토큰 확인: {}", token);

        if (token != null) {
            try {
                Claims claims = jwtTokenProvider.getClaimsFromToken(token); // ✅ 변경된 부분
                String email = claims.getSubject();

                // JWT 만료 여부 확인
                if (claims.getExpiration().before(new Date())) {
                    log.info("JWT 토큰이 만료됨");
                    response.addCookie(createExpiredJwtCookie());
                    SecurityContextHolder.clearContext();
                    chain.doFilter(request, response);
                    return;
                }

                UserDetails userDetails = new User(email, "", new ArrayList<>());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                log.info("유효하지 않은 JWT, 자동 로그아웃 수행");
                response.addCookie(createExpiredJwtCookie());
                SecurityContextHolder.clearContext();
                chain.doFilter(request, response);
                return;
            }
        } else {
            log.info("JWT 쿠키 없음, 인증되지 않은 상태");
        }

        chain.doFilter(request, response);
    }

    private Cookie createExpiredJwtCookie() {
        Cookie expiredCookie = new Cookie("jwt", "");
        expiredCookie.setHttpOnly(true);
        expiredCookie.setSecure(false);
        expiredCookie.setPath("/");
        expiredCookie.setMaxAge(0);
        return expiredCookie;
    }
}
