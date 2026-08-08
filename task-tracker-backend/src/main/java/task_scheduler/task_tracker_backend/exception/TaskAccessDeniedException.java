package task_scheduler.task_tracker_backend.exception;

public class TaskAccessDeniedException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public TaskAccessDeniedException() {
    super("Access denied");
  }
}
