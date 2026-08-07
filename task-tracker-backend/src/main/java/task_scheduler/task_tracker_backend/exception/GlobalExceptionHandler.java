package task_scheduler.task_tracker_backend.exception;

import jakarta.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import task_scheduler.task_tracker_backend.properties.LogProperties;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final LogProperties logProperties;

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
      IllegalArgumentException ex) {

    logException("invalid_argument", "Invalid request argument", ex);

    return buildResponse(ex, HttpStatus.BAD_REQUEST, "Некорректный запрос");
  }

  @ExceptionHandler(UserIsNotExistException.class)
  public ResponseEntity<Map<String, String>> handleUserIsNotExistException(
      UserIsNotExistException ex) {

    logException("user_not_found", "User not found", ex);

    return buildResponse(ex, HttpStatus.NOT_FOUND, "Пользователь не найден");
  }

  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleTaskNotFoundException(TaskNotFoundException ex) {

    logException("task_not_found", "Task not found", ex);

    return buildResponse(ex, HttpStatus.NOT_FOUND, "Задача не найдена");
  }

  @ExceptionHandler(TaskAccessDeniedException.class)
  public ResponseEntity<Map<String, String>> handleTaskAccessDeniedException(
      TaskAccessDeniedException ex) {

    logException("task_access_denied", "Task access denied", ex);

    return buildResponse(ex, HttpStatus.FORBIDDEN, "Нет доступа к задаче");
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(
      UserAlreadyExistsException ex) {

    logException("user_already_exists", "User already exists", ex);

    return buildResponse(ex, HttpStatus.CONFLICT, "Ошибка конфликта с существующим пользователем");
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<Map<String, String>> handleHttpMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException ex) {

    logException("unsupported_media_type", "Content-Type is not supported", ex);

    return buildResponse(ex, HttpStatus.BAD_REQUEST, "Content-Type is not supported");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {

    logException("validation_failed", "Validation failed", ex);

    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(FieldError::getDefaultMessage)
            .orElse("Некорректный запрос");

    return buildResponse(ex, HttpStatus.BAD_REQUEST, message);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Map<String, String>> handleAuthenticationException(
      AuthenticationException ex) {

    logException("authentication_failed", "Authentication failed", ex);

    return buildResponse(ex, HttpStatus.UNAUTHORIZED, "Не авторизован");
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {

    logException("access_denied", "Access denied", ex);

    return buildResponse(ex, HttpStatus.FORBIDDEN, "Доступ запрещён");
  }

  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<Map<String, String>> handleSecurityException(SecurityException ex) {

    logException("security_error", "Security error", ex);

    return buildResponse(ex, HttpStatus.FORBIDDEN, "Ошибка безопасности: доступ запрещён");
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Map<String, String>> handleNoSuchElementException(
      NoSuchElementException ex) {

    logException("element_not_found", "Element not found", ex);

    return buildResponse(ex, HttpStatus.NOT_FOUND, "Запрашиваемый элемент не найден");
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleEntityNotFoundException(
      EntityNotFoundException ex) {

    logException("entity_not_found", "Entity not found", ex);

    return buildResponse(ex, HttpStatus.NOT_FOUND, "Сущность не найдена");
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  public ResponseEntity<Map<String, String>> handleEmptyResultDataAccessException(
      EmptyResultDataAccessException ex) {

    logException("data_not_found", "Data not found", ex);

    return buildResponse(ex, HttpStatus.NOT_FOUND, "Данные не найдены");
  }

  @ExceptionHandler(SQLException.class)
  public ResponseEntity<Map<String, String>> handleSqlException(SQLException ex) {

    log.atError()
        .setCause(ex)
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "database_error")
        .addKeyValue("exception", ex.getClass().getSimpleName())
        .addKeyValue("message", ex.getMessage())
        .addKeyValue("sqlState", ex.getSQLState())
        .addKeyValue("errorCode", ex.getErrorCode())
        .log("Database error");

    return buildResponse(ex, HttpStatus.CONFLICT, "Ошибка базы данных. Попробуйте позже");
  }

  @ExceptionHandler(DuplicateKeyException.class)
  public ResponseEntity<Map<String, String>> handleDuplicateKeyException(DuplicateKeyException ex) {

    logException("duplicate_key", "Duplicate key conflict", ex);

    return buildResponse(ex, HttpStatus.CONFLICT, "Конфликт данных: запись уже существует");
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {

    logException("illegal_state", "Invalid application state", ex);

    return buildResponse(
        ex, HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера. Попробуйте ещё раз");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleException(Exception ex) {

    logException("unexpected_error", "Unexpected server error", ex);

    return buildResponse(
        ex, HttpStatus.INTERNAL_SERVER_ERROR, "Непредвиденная ошибка. Попробуйте позже");
  }

  private void logException(String event, String message, Exception ex) {
    log.atError()
        .setCause(ex)
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", event)
        .addKeyValue("exception", ex.getClass().getSimpleName())
        .addKeyValue("message", ex.getMessage())
        .log(message);
  }

  private ResponseEntity<Map<String, String>> buildResponse(
      Exception ex, HttpStatus status, String defaultMessage) {

    String rawMessage = ex.getMessage();

    String message =
        rawMessage != null && rawMessage.startsWith("message: ")
            ? rawMessage.replace("message: ", "")
            : defaultMessage;

    return ResponseEntity.status(status).body(Map.of("message", message));
  }
}
