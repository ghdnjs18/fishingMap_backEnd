package com.sparta.fishingload_backend.security;

import com.sparta.fishingload_backend.entity.UserRoleEnum;
import com.sparta.fishingload_backend.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    // 필터 검증
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.getJwtFromHeader(req, JwtUtil.AUTHORIZATION_HEADER);
        String refreshToken = jwtUtil.getJwtFromHeader(req, JwtUtil.REFRESH_HEADER);

        if (req.getRequestURI().equals("/user/login") || req.getRequestURI().equals("/user/signup") || req.getRequestURI().equals("/")) {
            logger.info("Pass Authorization");
            filterChain.doFilter(req, res);
        }

        // 토큰이 null인지, 길이가 0인지, 공백이 포함 되어 있는지 확인 있으면 (false)
        if (StringUtils.hasText(accessToken)) {

            if (!jwtUtil.validateToken(accessToken)) {

                String refresh = req.getHeader(JwtUtil.REFRESH_HEADER);
                if (!jwtUtil.validateToken(refreshToken) || !refreshTokenRepository.existsByToken(refresh)) {
                    logger.error("Refresh Token Error");
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                logger.info("Access Token Recreate");
                Claims info = jwtUtil.getUserInfoFromToken(refreshToken);
                String username = info.getSubject();
                UserRoleEnum role = UserRoleEnum.valueOf(String.valueOf(info.get("auth")));

                accessToken = jwtUtil.createAccessToken(username, role);
                res.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
                accessToken = jwtUtil.substringToken(accessToken);
            }

            Claims info = jwtUtil.getUserInfoFromToken(accessToken);

            logger.info("Token Authentication");
            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        filterChain.doFilter(req, res);
    }

    // 권한 인증 처리
    private void setAuthentication(String username) {
        // 인증 컨텍스트 생성
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        // 인증 객체 생성
        Authentication authentication = createAuthentication(username);
        // 인증 컨텍스트에 인증 정보를 담는다.
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}