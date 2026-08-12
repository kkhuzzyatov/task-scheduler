package task_scheduler.task_tracker_backend.service;

import io.jsonwebtoken.Claims;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import task_scheduler.task_tracker_backend.dto.auth.LoginResult;
import task_scheduler.task_tracker_backend.dto.email.WelcomeMessage;
import task_scheduler.task_tracker_backend.exception.UserAlreadyExistsException;
import task_scheduler.task_tracker_backend.exception.UserIsNotExistException;
import task_scheduler.task_tracker_backend.jwt.JwtProvider;
import task_scheduler.task_tracker_backend.kafka.WelcomeMessageProducer;
import task_scheduler.task_tracker_backend.properties.LogProperties;
import task_scheduler.task_tracker_backend.user.User;
import task_scheduler.task_tracker_backend.user.UserRepository;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthService {

  private static final String LOG_KEY_SERVICE = "service";
  private static final String LOG_KEY_EVENT = "event";
  private static final String LOG_KEY_EMAIL = "email";
  private static final String LOG_KEY_USER_ID = "userId";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;
  private final WelcomeMessageProducer welcomeMessageProducer;
  private final LogProperties logProperties;

  public void register(String email, String password) {
    if (userRepository.existsByEmail(email)) {
      log.atWarn()
          .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
          .addKeyValue(LOG_KEY_EVENT, "user_registration_failed")
          .addKeyValue("reason", "email_exists")
          .addKeyValue(LOG_KEY_EMAIL, email)
          .log("Email already exists");

      throw new UserAlreadyExistsException("Email уже зарегистрирован");
    }

    User user = new User();
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(password));

    user = userRepository.save(user);

    log.atInfo()
        .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
        .addKeyValue(LOG_KEY_EVENT, "user_registered")
        .addKeyValue(LOG_KEY_USER_ID, user.getUserId())
        .addKeyValue(LOG_KEY_EMAIL, user.getEmail())
        .log("User registered successfully");

    welcomeMessageProducer.send(
        WelcomeMessage.builder()
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
                      .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
                      .addKeyValue(LOG_KEY_EVENT, "login_failed")
                      .addKeyValue("reason", "user_not_found")
                      .addKeyValue(LOG_KEY_EMAIL, email)
                      .log("Login failed: user not found");

                  return new IllegalArgumentException("Неверный email или пароль");
                });

    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
      log.atWarn()
          .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
          .addKeyValue(LOG_KEY_EVENT, "login_failed")
          .addKeyValue("reason", "invalid_password")
          .addKeyValue(LOG_KEY_USER_ID, user.getUserId())
          .addKeyValue(LOG_KEY_EMAIL, email)
          .log("Login failed: invalid password");

      throw new IllegalArgumentException("Неверный email или пароль");
    }

    String token = jwtProvider.generate(user.getUserId(), user.getEmail());

    log.atInfo()
        .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
        .addKeyValue(LOG_KEY_EVENT, "login_success")
        .addKeyValue(LOG_KEY_USER_ID, user.getUserId())
        .addKeyValue(LOG_KEY_EMAIL, user.getEmail())
        .log("User logged in successfully");

    return new LoginResult(token);
  }

  public UUID getMyUuid(String token) {
    String normalizedToken = token.trim();

    if (normalizedToken.startsWith("Bearer ")) {
      normalizedToken = normalizedToken.substring(7);
    }

    if (normalizedToken.startsWith("\"") && normalizedToken.endsWith("\"")) {
      normalizedToken = normalizedToken.substring(1, normalizedToken.length() - 1);
    }

    Claims claims = jwtProvider.validate(normalizedToken);
    UUID userId = UUID.fromString(claims.getSubject());

    log.atDebug()
        .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
        .addKeyValue(LOG_KEY_EVENT, "jwt_validated")
        .addKeyValue(LOG_KEY_USER_ID, userId)
        .log("JWT validated successfully");

    return userId;
  }

  public User getUserById(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(
            () -> {
              log.atWarn()
                  .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
                  .addKeyValue(LOG_KEY_EVENT, "user_lookup_failed")
                  .addKeyValue(LOG_KEY_USER_ID, id)
                  .log("User not found");

              return new UserIsNotExistException();
            });
  }
}
