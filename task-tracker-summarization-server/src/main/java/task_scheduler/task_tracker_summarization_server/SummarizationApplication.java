package task_scheduler.task_tracker_summarization_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class SummarizationApplication {

  public static void main(String[] args) {
    SpringApplication.run(SummarizationApplication.class, args);
  }
}
