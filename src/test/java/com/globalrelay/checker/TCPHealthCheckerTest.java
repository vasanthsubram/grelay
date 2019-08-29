package com.globalrelay.checker;

import com.globalrelay.model.Connection;
import com.globalrelay.model.HealthCheckConfiguration;
import com.globalrelay.util.TCPServer;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TCPHealthCheckerTest {

  @Test
  public void testHappyPath(){
    Runnable runnable = () -> new TCPServer(5000);
    new Thread(runnable).start();
    HealthCheckConfiguration healthCheckConfiguration = new HealthCheckConfiguration();
    Connection connection = new Connection( Protocol.TCP, "localhost", 5000);
    healthCheckConfiguration.setConnection(connection);
    assertTrue(new TCPHealthChecker().doCheck(healthCheckConfiguration));
  }

  @Test
  public void testNonExistingServer(){
    HealthCheckConfiguration healthCheckConfiguration = new HealthCheckConfiguration();
    Connection connection = new Connection( Protocol.TCP, "localhost", 92121);
    healthCheckConfiguration.setConnection(connection);
    assertFalse(new TCPHealthChecker().doCheck(healthCheckConfiguration));
  }

}
