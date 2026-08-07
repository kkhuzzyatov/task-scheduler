package task_scheduler.task_tracker_backend.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import task_scheduler.task_tracker_backend.jwt.JwtProvider;
import task_scheduler.task_tracker_backend.properties.LogProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final LogProperties logProperties;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      try {
        Claims claims = jwtProvider.validate(header.substring(7));

        String userId = claims.getSubject();
        String role = claims.get("role", String.class);

        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));

        auth.setDetails(claims);

        SecurityContextHolder.getContext().setAuthentication(auth);

        log.atDebug()
            .addKeyValue("service", logProperties.name())
            .addKeyValue("event", "jwt_authenticated")
            .addKeyValue("userId", userId)
            .log("JWT token accepted");

      } catch (JwtException e) {

        log.atWarn()
            .setCause(e)
            .addKeyValue("service", logProperties.name())
            .addKeyValue("event", "jwt_validation_failed")
            .addKeyValue("reason", e.getClass().getSimpleName())
            .addKeyValue("path", request.getRequestURI())
            .log("JWT token invalid");
      }
    }

    chain.doFilter(request, response);
  }
}
