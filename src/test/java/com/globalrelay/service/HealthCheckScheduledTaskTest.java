package com.globalrelay.service;


import com.globalrelay.checker.HealthChecker;
import com.globalrelay.model.Connection;
import com.globalrelay.model.HealthCheckConfiguration;
import com.globalrelay.notification.Notifier;
import com.globalrelay.notification.ServerStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class HealthCheckScheduledTaskTest {

  @Mock
  private HealthChecker healthChecker;

  @Mock
  private Notifier notifier;

  @Mock
  private HealthCheckConfiguration configuration;

  @InjectMocks
  private HealthCheckScheduledTask healthCheckScheduledTask;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testNoStatusChange(){
    Connection connection = Mockito.mock(Connection.class);
    when(healthChecker.doCheck(any())).thenReturn(true);
    when(configuration.isServiceOutage()).thenReturn(false);
    when(configuration.getConnection()).thenReturn(connection);
    healthCheckScheduledTask.run();
    verify(healthChecker,  times(1)).doCheck(configuration);
    verifyZeroInteractions(notifier);
  }

  @Test
  public void testStatusChange(){
    Connection connection = Mockito.mock(Connection.class);
    when(healthChecker.doCheck(any())).thenReturn(false);
    when(configuration.isServiceOutage()).thenReturn(false);
    when(configuration.getConnection()).thenReturn(connection);
    healthCheckScheduledTask.run();
    verify(healthChecker,  times(1)).doCheck(configuration);
    verify(notifier,  times(1)).notify(configuration, ServerStatus.DOWN);
  }


  @Test
  public void testOutage(){
    long currSec = (System.currentTimeMillis()/1000);
    when(configuration.isServiceOutage()).thenReturn(true);
    when(configuration.getOutageStartTime()).thenReturn(currSec-2000);
    when(configuration.getOutageEndTime()).thenReturn(currSec+10000);
    when(configuration.getConnection()).thenReturn(new Connection());
    healthCheckScheduledTask.run();
    verifyZeroInteractions(healthChecker);
  }

  @Test
  public void testExitingOutage(){
    long currSec = (System.currentTimeMillis()/1000);
    when(configuration.isServiceOutage()).thenReturn(true);
    when(configuration.getOutageStartTime()).thenReturn(currSec-2000);
    when(configuration.getOutageEndTime()).thenReturn(currSec-1000);
    when(configuration.getConnection()).thenReturn(new Connection());
    healthCheckScheduledTask.run();
    verify(healthChecker,  times(1)).doCheck(configuration);
  }

}