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
   * All tasks of user created since given report time
   */
  @Query(
      """
      SELECT t
      FROM Task t
      WHERE t.user.userId = :userId
        AND t.createdAt > :since
      """)
  List<Task> findTasksCreatedSince(
      @Param("userId") UUID userId, @Param("since") LocalDateTime since);

  /*
   * Completed tasks of user completed since given report time
   */
  @Query(
      """
      SELECT t
      FROM Task t
      WHERE t.user.userId = :userId
        AND t.completedAt IS NOT NULL
        AND t.completedAt > :since
      """)
  List<Task> findCompletedTasksSince(
      @Param("userId") UUID userId, @Param("since") LocalDateTime since);

  /*
   * Incomplete tasks of user created since given report time
   */
  @Query(
      """
      SELECT t
      FROM Task t
      WHERE t.user.userId = :userId
        AND t.completedAt IS NULL
        AND t.createdAt > :since
      """)
  List<Task> findIncompleteTasksCreatedSince(
      @Param("userId") UUID userId, @Param("since") LocalDateTime since);

  /*
   * Completed tasks completed between previous and freshest reports
   */
  @Query(
      """
      SELECT t
      FROM Task t
      WHERE t.user.userId = :userId
        AND t.completedAt IS NOT NULL
        AND t.completedAt > :previousReportTime
        AND t.completedAt <= :freshestReportTime
      """)
  List<Task> findCompletedTasksBetweenReports(
      @Param("userId") UUID userId,
      @Param("previousReportTime") LocalDateTime previousReportTime,
      @Param("freshestReportTime") LocalDateTime freshestReportTime);

  /*
   * Created_at time of freshest report
   */
  @Query(
      """
      SELECT MAX(r.createdAt)
      FROM Report r
      WHERE r.user.userId = :userId
      """)
  LocalDateTime findFreshestReportCreatedAt(@Param("userId") UUID userId);

  /*
   * Created_at time of previous report
   */
  @Query(
      """
      SELECT MAX(r1.createdAt)
      FROM Report r1
      WHERE r1.user.userId = :userId
        AND r1.createdAt < (
            SELECT MAX(r2.createdAt)
            FROM Report r2
            WHERE r2.user.userId = :userId
        )
      """)
  LocalDateTime findPreviousReportCreatedAt(@Param("userId") UUID userId); /*
   * Number of reports created by user
   */

  @Query(
      """
          SELECT COUNT(r)
          FROM Report r
          WHERE r.user.userId = :userId
          """)
  int countReportsByUserId(@Param("userId") UUID userId);
}
