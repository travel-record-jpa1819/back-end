package io.travel.map.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Value("${jwt.secret}")
    private String secretKey; // 환경 변수에서 관리하는 것이 좋음

    @Override
    // 모든 API 요청이 들어오면 이 메서드가 자동으로 실행됨
    // JWT 검증을 통해 사용자 인증을 수행하는 역할
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // JWT 토큰을 Authorization 헤더 대신 쿠키에서 가져옴
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            try {
                // 검증로직
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String email = claims.getSubject(); // 토큰이 정상적이라면 이메일을 가져옴

                // Spring Security에 인증 정보 저장
                // Spring Security 가 헤딩 요청을 인증된 사용자로 인식해야 하기 때문에 인증 적보를 저장
                UserDetails userDetails = new User(email, "", new ArrayList<>());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // JWT가 유효하지 않으면 401 에러 반환
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }else {
            System.out.println("JWT 쿠키가 존재하지 않음");
        }

        chain.doFilter(request, response);
    }
}
