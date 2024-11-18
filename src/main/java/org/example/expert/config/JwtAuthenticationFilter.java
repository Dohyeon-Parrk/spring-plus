package org.example.expert.config;

import java.io.IOException;
import java.util.Collections;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
		throws ServletException, IOException {
		String bearerToken = request.getHeader("Authorization");

		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			String jwt = jwtUtil.substringToken(bearerToken);

			if (jwtUtil.validateToken(jwt)) {
				Claims claims = jwtUtil.extractClaims(jwt);

				Long userId = Long.parseLong(claims.getSubject());
				String email = claims.get("email", String.class);
				String nickname = claims.get("nickname", String.class);
				UserRole userRole = UserRole.of(claims.get("userRole", String.class));

				AuthUser authUser = new AuthUser(userId, email, nickname, userRole);

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					authUser, null, Collections.singletonList(userRole.toGrantedAuthority()));
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}

		chain.doFilter(request, response);
	}
}










