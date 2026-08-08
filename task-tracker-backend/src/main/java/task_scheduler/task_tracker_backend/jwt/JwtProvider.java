package task_scheduler.task_tracker_backend.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_backend.properties.JwtProperties;

@Component
@RequiredArgsConstructor
public class JwtProvider {

  private final JwtProperties jwtProperties;

  private SecretKey key() {
    return Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.getSecret()));
  }

  public String generate(UUID userId, String email) {
    return Jwts.builder()
        .subject(userId.toString())
        .claim("email", email)
        .claim("role", "USER")
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L))
        .signWith(key())
        .compact();
  }

  public Claims validate(String token) {
    return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
  }
}
