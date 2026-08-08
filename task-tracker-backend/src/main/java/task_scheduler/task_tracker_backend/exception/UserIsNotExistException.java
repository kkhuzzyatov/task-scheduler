package task_scheduler.task_tracker_backend.exception;

public class UserIsNotExistException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public UserIsNotExistException() {
    super("User does not exist");
  }
}
