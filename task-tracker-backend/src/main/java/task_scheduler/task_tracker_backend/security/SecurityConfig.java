package task_scheduler.task_tracker_backend.security;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import task_scheduler.task_tracker_backend.jwt.JwtFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtFilter jwtFilter;
  private final InternalApiKeyFilter internalApiKeyFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.POST, "/api/user")
                    .permitAll()
                    .requestMatchers("/api/auth/login")
                    .permitAll()
                    .requestMatchers(
                        "/swagger-ui/**", "/api-docs/**", "/openapi.yml", "/v3/api-docs")
                    .permitAll()
                    .requestMatchers("/internal/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(internalApiKeyFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                        (request, response, authException) -> {
                          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                          response.setCharacterEncoding("UTF-8");
                          response
                              .getWriter()
                              .write(
                                  "{\"status\":401,\"message\":\"Неавторизован или токен истёк\"}");
                        })
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) -> {
                          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                          response.setCharacterEncoding("UTF-8");
                          response
                              .getWriter()
                              .write("{\"status\":403,\"message\":\"Недостаточно прав\"}");
                        }));

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration config = new CorsConfiguration();

    config.setAllowedOrigins(List.of("http://localhost:5173"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/api/**", config);

    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
