package com.globalrelay.service;

import com.globalrelay.checker.HealthChecker;
import com.globalrelay.common.Const;
import com.globalrelay.exception.HealthCheckConfigurationAlreadyExistsException;
import com.globalrelay.exception.NoHealthCheckConfigurationFound;
import com.globalrelay.model.Connection;
import com.globalrelay.model.HealthCheckConfiguration;
import com.globalrelay.notification.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main class handling registering/deregistering services to be monitored
 */
@Component
public class ServiceHealthMonitor {

  private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckScheduledTask.class);

  //map to hold health check configurations
  private Map<Connection, HealthCheckConfiguration> configurationMap = new HashMap<>();

  //Maintains a thread pool whose threads monitor the services periodically
  private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(Const.THREAD_POOL_SIZE);

  private Map<Connection, ScheduledFuture> scheduledFutureMap = new HashMap<>();

  // Allow for set of configurations to be loaded on start of jvm
  @PostConstruct
  public void init(){
    ((ScheduledThreadPoolExecutor)scheduledExecutorService).setRemoveOnCancelPolicy(true);
  }

  /**
   * Add a new health check configuration
   * To make sure only one registration is made to a server, this method is synchronized
   *
   * @param healthCheckConfiguration
   */
  synchronized public void addRegistration(HealthCheckConfiguration healthCheckConfiguration){
    if(configurationMap.get(healthCheckConfiguration.getConnection())!=null){
      throw new HealthCheckConfigurationAlreadyExistsException("The connection has already been registered");
    }
    configurationMap.put(healthCheckConfiguration.getConnection(), healthCheckConfiguration);

    HealthChecker healthChecker = HealthChecker.getHealthChecker(healthCheckConfiguration.getProtocol());
    Notifier notifier = Notifier.getNotifier(healthCheckConfiguration.getNotifierType());
    Runnable scheduledTask = new HealthCheckScheduledTask(healthCheckConfiguration, healthChecker, notifier);

    ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(scheduledTask, 0,
          healthCheckConfiguration.getFrequency(), TimeUnit.SECONDS);
    scheduledFutureMap.put(healthCheckConfiguration.getConnection(), scheduledFuture);
    LOGGER.info("Health Check configuration successfully added for : " + healthCheckConfiguration.getConnection().toString());
  }

  /**
   * Remove the configuration for a server.
   * Also remove the scheduled task for the server
   *
   * @param connection
   */
  public void removeRegistration(Connection connection){
    configurationMap.remove(connection);
    ScheduledFuture scheduledFuture = scheduledFutureMap.get(connection);
    if(scheduledFuture!=null) {
      scheduledFuture.cancel(false);
    }
    LOGGER.info("Health Check configuration removed for : " + connection.toString());
  }


  /**
   * Add server outage information to a health check configuration
   *
   * @param connection
   * @param outageStartTime
   * @param outageEndTime
   */
  synchronized public void registerOutageFor(Connection connection, long outageStartTime, long outageEndTime){
    HealthCheckConfiguration configuration = configurationMap.get(connection);
    if(configuration==null){
      throw new NoHealthCheckConfigurationFound("No configuration was found for the given connection");
    }
    configuration.setServiceOutage(true);
    configuration.setOutageStartTime(outageStartTime);
    configuration.setOutageEndTime(outageEndTime);
    LOGGER.info("Outage information added for Health Check configuration : " + connection.toString()
              + " - OutageStartTime = " + outageStartTime + " OutageEndTime = " + outageEndTime);
  }

  public Collection<HealthCheckConfiguration> getServiceCongfigurations() {
    return configurationMap.values();
  }

}
