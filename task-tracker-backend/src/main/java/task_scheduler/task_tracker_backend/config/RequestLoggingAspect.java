package task_scheduler.task_tracker_backend.config;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RequestLoggingAspect {

  @Before("within(task_scheduler.task_tracker_backend.controller..*)")
  public void logRequest(JoinPoint joinPoint) {

    CodeSignature signature = (CodeSignature) joinPoint.getSignature();

    Map<String, Object> params = new LinkedHashMap<>();

    log.info(
        "Request: {}.{} {}",
        signature.getDeclaringType().getSimpleName(),
        signature.getName(),
        params);
  }
}
