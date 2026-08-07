package task_scheduler.task_tracker_backend.service;

import io.jsonwebtoken.Claims;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import task_scheduler.task_tracker_backend.dto.auth.LoginResult;
import task_scheduler.task_tracker_backend.dto.email.EmailTask;
import task_scheduler.task_tracker_backend.exception.UserAlreadyExistsException;
import task_scheduler.task_tracker_backend.exception.UserIsNotExistException;
import task_scheduler.task_tracker_backend.jwt.JwtProvider;
import task_scheduler.task_tracker_backend.kafka.EmailProducer;
import task_scheduler.task_tracker_backend.properties.LogProperties;
import task_scheduler.task_tracker_backend.user.User;
import task_scheduler.task_tracker_backend.user.UserRepository;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;
  private final EmailProducer emailProducer;

  private final LogProperties logProperties;

  public void register(String email, String password) {
    if (userRepository.existsByEmail(email)) {
      log.atWarn()
          .addKeyValue("service", logProperties.name())
          .addKeyValue("event", "user_registration_failed")
          .addKeyValue("reason", "email_exists")
          .addKeyValue("email", email)
          .log("Email already exists");

      throw new UserAlreadyExistsException("Email уже зарегистрирован");
    }

    User user = new User();
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(password));

    user = userRepository.save(user);

    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "user_registered")
        .addKeyValue("userId", user.getUserId())
        .addKeyValue("email", user.getEmail())
        .log("User registered successfully");

    emailProducer.send(
        EmailTask.builder()
            .recipient(user.getEmail())
            .subject("Welcome!")
            .text("Спасибо за регистрацию!")
            .build(),
        user);
  }

  public LoginResult login(String email, String password) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> {
                  log.atWarn()
                      .addKeyValue("service", logProperties.name())
                      .addKeyValue("event", "login_failed")
                      .addKeyValue("reason", "user_not_found")
                      .addKeyValue("email", email)
                      .log("Login failed: user not found");

                  return new IllegalArgumentException("Неверный email или пароль");
                });

    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
      log.atWarn()
          .addKeyValue("service", logProperties.name())
          .addKeyValue("event", "login_failed")
          .addKeyValue("reason", "invalid_password")
          .addKeyValue("userId", user.getUserId())
          .addKeyValue("email", email)
          .log("Login failed: invalid password");

      throw new IllegalArgumentException("Неверный email или пароль");
    }

    String token = jwtProvider.generate(user.getUserId(), user.getEmail());

    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "login_success")
        .addKeyValue("userId", user.getUserId())
        .addKeyValue("email", user.getEmail())
        .log("User logged in successfully");

    return new LoginResult(token);
  }

  public UUID getMyUuid(String token) {
    token = token.trim();

    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    if (token.startsWith("\"") && token.endsWith("\"")) {
      token = token.substring(1, token.length() - 1);
    }

    Claims claims = jwtProvider.validate(token);
    UUID userId = UUID.fromString(claims.getSubject());

    log.atDebug()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "jwt_validated")
        .addKeyValue("userId", userId)
        .log("JWT validated successfully");

    return userId;
  }

  public User getUserById(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(
            () -> {
              log.atWarn()
                  .addKeyValue("service", logProperties.name())
                  .addKeyValue("event", "user_lookup_failed")
                  .addKeyValue("userId", id)
                  .log("User not found");

              return new UserIsNotExistException();
            });
  }
}
