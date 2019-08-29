package com.globalrelay.controller;

import com.globalrelay.checker.Protocol;
import com.globalrelay.util.TCPServer;
import com.globalrelay.model.Connection;
import com.globalrelay.model.HealthCheckConfiguration;
import com.globalrelay.notification.NotifierType;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertTrue;

/**
 * This is an integration test and requires the monitor running at 8080
 */
@Ignore
public class HealthCheckControllerTestIT {

  private RestTemplate restTemplate = new RestTemplate();

  private static String monitorUrl = "http://localhost:8080/configuration";
  private static int TEST_SERVICE_PORT = 5000;
  private HttpEntity<HealthCheckConfiguration> request;

  @Before
  public void setup() {
    HealthCheckConfiguration healthCheckConfiguration = new HealthCheckConfiguration();
    Connection connection = new Connection(Protocol.TCP, "localhost", TEST_SERVICE_PORT);
    healthCheckConfiguration.setConnection(connection);
    healthCheckConfiguration.setFrequency(3);
    healthCheckConfiguration.setNotifierType(NotifierType.CONSOLE);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    request = new HttpEntity<>(healthCheckConfiguration, headers);
  }

  @Test
  public void testAddConfiguration() throws Exception {
    Runnable runnable = () -> new TCPServer(TEST_SERVICE_PORT);
    Thread tcpServerThread = new Thread(runnable);
    tcpServerThread.start();

    ResponseEntity<String> response = restTemplate.postForEntity(monitorUrl, request, String.class);
    assertTrue(response.getStatusCode()== HttpStatus.OK);
    Thread.sleep(1000 * 10);

    //start TCP server again
    new Thread(runnable).start();
    Thread.sleep(1000 * 10);
  }

  @After
  public void cleanup() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    Connection connection = new Connection(Protocol.TCP, "localhost", 5000);
    HttpEntity<Connection> request = new HttpEntity<>(connection, headers);
    restTemplate.exchange(monitorUrl, HttpMethod.DELETE, request, String.class);
  }
}
