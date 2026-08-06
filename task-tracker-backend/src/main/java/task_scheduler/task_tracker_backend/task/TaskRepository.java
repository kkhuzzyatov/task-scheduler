package task_scheduler.task_tracker_backend.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, UUID> {

  List<Task> findAllByUserUserIdAndDeletedAtIsNull(UUID userId);

  Optional<Task> findByTaskIdAndUserUserIdAndDeletedAtIsNull(UUID taskId, UUID userId);

  List<Task> findAllByUserUserIdAndCompletedAtIsNullAndDeletedAtIsNull(UUID userId);

  List<Task> findAllByUserUserIdAndCompletedAtIsNotNullAndDeletedAtIsNull(UUID userId);

  List<Task> findAllByUserUserIdAndCompletedAtBetweenAndDeletedAtIsNull(
      UUID userId, LocalDateTime from, LocalDateTime to);
}
