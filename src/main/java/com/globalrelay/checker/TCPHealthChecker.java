package com.globalrelay.checker;

import com.globalrelay.model.HealthCheckConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.Socket;

/**
 * Perform health check using TCP
 */
@Component
public class TCPHealthChecker implements HealthChecker{

  @PostConstruct
  public void init(){
    HealthChecker.registerHealthChecker(getProtocol(), this);
  }

  public boolean doCheck(HealthCheckConfiguration healthCheckConfiguration) {
    Socket socket;
    try {
      socket = new Socket(healthCheckConfiguration.getHost(),
                            healthCheckConfiguration.getPort());
      socket.close();
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public Protocol getProtocol(){
    return Protocol.TCP;
  }
}