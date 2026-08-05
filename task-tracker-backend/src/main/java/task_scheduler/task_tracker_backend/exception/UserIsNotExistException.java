package task_scheduler.task_tracker_backend.exception;

public class UserIsNotExistException extends RuntimeException {
  public UserIsNotExistException(String message) {
    super(message);
  }
}
