package task_scheduler.task_tracker_scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class SchedulerApplication {

  public static void main(String[] args) {
    SpringApplication.run(SchedulerApplication.class, args);
  }
}
