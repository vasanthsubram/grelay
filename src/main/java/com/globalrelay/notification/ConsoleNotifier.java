package com.globalrelay.notification;

import com.globalrelay.model.HealthCheckConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * A notifier that sends notification to console
 */
@Component
public class ConsoleNotifier implements Notifier {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleNotifier.class);

  @PostConstruct
  public void init(){
    Notifier.registerNotifier(getNotifierType(), this);
  }
  @Override
  public void notify(HealthCheckConfiguration healthCheckConfiguration, ServerStatus serverStatus) {
    LOGGER.info("NOTIFICATION: Status changed to : " + serverStatus.toString()
          + " for server at :" + healthCheckConfiguration.getConnection());
  }

  @Override
  public NotifierType getNotifierType() {
    return NotifierType.CONSOLE;
  }
}
