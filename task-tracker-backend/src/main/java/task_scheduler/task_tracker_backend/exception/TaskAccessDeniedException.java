package task_scheduler.task_tracker_backend.exception;

public class TaskAccessDeniedException extends RuntimeException {

  public TaskAccessDeniedException() {
    super("Access denied");
  }
}
