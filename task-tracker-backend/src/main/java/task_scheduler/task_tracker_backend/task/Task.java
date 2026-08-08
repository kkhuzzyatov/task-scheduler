package task_scheduler.task_tracker_backend.task;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import task_scheduler.task_tracker_backend.user.User;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "task_id")
  private UUID taskId;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  public void complete() {
    this.completedAt = LocalDateTime.now();
  }

  @SuppressWarnings("PMD.NullAssignment")
  public void uncomplete() {
    this.completedAt = null;
  }

  public void delete() {
    this.deletedAt = LocalDateTime.now();
  }
}
