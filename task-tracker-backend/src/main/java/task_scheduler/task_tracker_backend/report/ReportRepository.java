package task_scheduler.task_tracker_backend.report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import task_scheduler.task_tracker_backend.task.Task;

public interface ReportRepository extends JpaRepository<Report, UUID> {

  /*
   * All tasks of user created since freshest report
   */
  @Query(
      """
      SELECT t
      FROM Task t
      WHERE t.user.userId = :userId
        AND t.createdAt >
          (
            SELECT COALESCE(MAX(r.createdAt), TIMESTAMP '1970-01-01 00:00:00')
            FROM Report r
            WHERE r.user.userId = :userId
          )
      """)
  List<Task> findTasksCreatedSinceFreshestReport(@Param("userId") UUID userId);

  /*
   * Completed tasks of user completed since freshest report
   */
  @Query(
      """
      SELECT t
      FROM Task t
      WHERE t.user.userId = :userId
        AND t.completedAt IS NOT NULL
        AND t.completedAt >
          (
            SELECT COALESCE(MAX(r.createdAt), TIMESTAMP '1970-01-01 00:00:00')
            FROM Report r
            WHERE r.user.userId = :userId
          )
      """)
  List<Task> findCompletedTasksSinceFreshestReport(@Param("userId") UUID userId);

  /*
   * Incomplete tasks created since freshest report
   */
  @Query(
      """
      SELECT t
      FROM Task t
      WHERE t.user.userId = :userId
        AND t.completedAt IS NULL
        AND t.createdAt >
          (
            SELECT COALESCE(MAX(r.createdAt), TIMESTAMP '1970-01-01 00:00:00')
            FROM Report r
            WHERE r.user.userId = :userId
          )
      """)
  List<Task> findIncompleteTasksCreatedSinceFreshestReport(@Param("userId") UUID userId);

  /*
   * Completed tasks completed between previous and freshest reports
   */
  @Query(
      """
      SELECT t
      FROM Task t
      WHERE t.user.userId = :userId
        AND t.completedAt IS NOT NULL
        AND t.completedAt >
          (
            SELECT COALESCE(MAX(r2.createdAt), TIMESTAMP '1970-01-01 00:00:00')
            FROM Report r2
            WHERE r2.user.userId = :userId
              AND r2.createdAt <
                (
                  SELECT MAX(r1.createdAt)
                  FROM Report r1
                  WHERE r1.user.userId = :userId
                )
          )
        AND t.completedAt <=
          (
            SELECT MAX(r.createdAt)
            FROM Report r
            WHERE r.user.userId = :userId
          )
      """)
  List<Task> findCompletedTasksBetweenPreviousAndFreshestReport(@Param("userId") UUID userId);

  /*
   * Created_at of freshest report
   */
  @Query(
      """
      SELECT MAX(r.createdAt)
      FROM Report r
      WHERE r.user.userId = :userId
      """)
  LocalDateTime findFreshestReportCreatedAt(@Param("userId") UUID userId);
}
