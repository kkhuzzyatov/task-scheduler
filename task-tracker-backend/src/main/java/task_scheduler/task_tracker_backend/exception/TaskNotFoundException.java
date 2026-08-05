package task_scheduler.task_tracker_backend.exception;

public class TaskNotFoundException extends RuntimeException {

  public TaskNotFoundException() {
    super("Task not found");
  }
}
