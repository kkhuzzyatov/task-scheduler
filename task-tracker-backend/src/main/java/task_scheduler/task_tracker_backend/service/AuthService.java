package task_scheduler.task_tracker_backend.service;

import io.jsonwebtoken.Claims;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import task_scheduler.task_tracker_backend.dto.auth.LoginResult;
import task_scheduler.task_tracker_backend.exception.UserAlreadyExistsException;
import task_scheduler.task_tracker_backend.exception.UserIsNotExistException;
import task_scheduler.task_tracker_backend.jwt.JwtProvider;
import task_scheduler.task_tracker_backend.user.User;
import task_scheduler.task_tracker_backend.user.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;

  public void register(String email, String password) {
    if (userRepository.existsByEmail(email)) {
      throw new UserAlreadyExistsException("Email уже зарегистрирован");
    }

    User user = new User();
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(password));

    userRepository.save(user);
  }

  public LoginResult login(String email, String password) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Неверный email или пароль"));

    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
      throw new IllegalArgumentException("Неверный email или пароль");
    }

    String token = jwtProvider.generate(user.getUserId(), user.getEmail());

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
    return UUID.fromString(claims.getSubject());
  }

  public User getUserById(UUID id) {
    return userRepository.findById(id).orElseThrow((UserIsNotExistException::new));
  }
}
