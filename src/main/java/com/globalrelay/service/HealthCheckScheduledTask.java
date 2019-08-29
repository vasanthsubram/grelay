package com.globalrelay.service;

import com.globalrelay.checker.HealthChecker;
import com.globalrelay.model.HealthCheckConfiguration;
import com.globalrelay.notification.Notifier;
import com.globalrelay.notification.ServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runnable that is scheduled to do the actual verification of service status
 */
public class HealthCheckScheduledTask implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckScheduledTask.class);

  private HealthCheckConfiguration healthCheckConfiguration;
  private HealthChecker healthChecker;
  private Notifier notifier;
  private int gracePeriodCounter;
  private ServerStatus currentServerStatus = ServerStatus.UP;

  public HealthCheckScheduledTask(HealthCheckConfiguration healthCheckConfiguration,
                                  HealthChecker healthChecker,
                                  Notifier notifier) {
    this.healthCheckConfiguration = healthCheckConfiguration;
    this.healthChecker = healthChecker;
    this.notifier = notifier;
    this.gracePeriodCounter = healthCheckConfiguration.getGraceTime();
  }

  @Override
  public void run() {
    if (checkServiceOutage(healthCheckConfiguration)) {
      return;
    }
    boolean result = healthChecker.doCheck(healthCheckConfiguration);
    ServerStatus newServerStatus = ServerStatus.statusForBoolean(result);
    LOGGER.info("Status : " + newServerStatus.toString() + " for " + healthCheckConfiguration.getConnection());

    //notify only if the status changes
    if (currentServerStatus != newServerStatus) {
      if (newServerStatus == ServerStatus.UP) {
        handleNotification(newServerStatus);
        return;
      }
      //allow grace period
      gracePeriodCounter = gracePeriodCounter - healthCheckConfiguration.getFrequency();
      if (gracePeriodCounter > 0) {
        return;
      }
      LOGGER.info("Grace Period over " + healthCheckConfiguration.getConnection());
      handleNotification(newServerStatus);
    }
    //reset the grace period counter
    this.gracePeriodCounter = healthCheckConfiguration.getGraceTime();
  }

  private void handleNotification(ServerStatus newServerStatus) {
    notifier.notify(healthCheckConfiguration, newServerStatus);
    currentServerStatus = newServerStatus;
    LOGGER.info("Status of Service " + healthCheckConfiguration.getConnection()
          + " changed to " + newServerStatus.toString());
  }

  protected boolean checkServiceOutage(HealthCheckConfiguration healthCheckConfiguration) {
    if (healthCheckConfiguration.isServiceOutage()) {
      long currentTime = System.currentTimeMillis() / 1000;
      long startTime = healthCheckConfiguration.getOutageStartTime();
      long endTime = healthCheckConfiguration.getOutageEndTime();
      if (currentTime > startTime && currentTime < (endTime)) {
        LOGGER.info("Service " + healthCheckConfiguration.getConnection().toString()
              + " is currently in planned outage");
        return true;
      } else {
        healthCheckConfiguration.setServiceOutage(false);
        LOGGER.info("Service " + healthCheckConfiguration.getConnection().toString()
              + " is out of planned outage");
      }
    }
    return false;
  }
}
