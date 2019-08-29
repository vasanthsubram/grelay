package com.globalrelay.service;

import com.globalrelay.checker.Protocol;
import com.globalrelay.exception.NoHealthCheckConfigurationFound;
import com.globalrelay.model.Connection;
import com.globalrelay.model.HealthCheckConfiguration;
import com.globalrelay.notification.NotifierType;
import org.junit.Test;

public class ServiceHealthMonitorTest {

  private ServiceHealthMonitor serviceHealthMonitor = new ServiceHealthMonitor();

  @Test
  public void testAddRegistrationHappyPath(){
    serviceHealthMonitor.addRegistration(createHealthConfiguration());
  }

  @Test
  public void testAddAddOutageHappyPath(){
    serviceHealthMonitor.addRegistration(createHealthConfiguration());
    serviceHealthMonitor.registerOutageFor(createConnection(),1,2);
  }

  @Test(expected = NoHealthCheckConfigurationFound.class)
  public void testAddOutageForNonExistingConfig(){
    serviceHealthMonitor.registerOutageFor(createConnection(),1,2);
  }

  private HealthCheckConfiguration createHealthConfiguration(){
    Connection connection = new Connection(Protocol.TCP, "localhost",5000);
    HealthCheckConfiguration configuration = new HealthCheckConfiguration();
    configuration.setGraceTime(10);
    configuration.setFrequency(5);
    configuration.setServiceOutage(false);
    configuration.setConnection(connection);
    configuration.setNotifierType(NotifierType.CONSOLE);
    return configuration;
  }

  private Connection createConnection(){
    Connection connection = new Connection();
    connection.setPort(5000);
    connection.setHost("localhost");
    connection.setProtocol(Protocol.TCP);
    return connection;
  }

}
