package com.globalrelay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalrelay.service.ServiceHealthMonitor;
import com.globalrelay.checker.Protocol;
import com.globalrelay.exception.HealthCheckConfigurationAlreadyExistsException;
import com.globalrelay.exception.NoHealthCheckConfigurationFound;
import com.globalrelay.model.Connection;
import com.globalrelay.model.HealthCheckConfiguration;
import com.globalrelay.notification.NotifierType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@RunWith(SpringJUnit4ClassRunner.class)
public class HealthCheckControllerTest {

  private MockMvc mockMvc;

  @Mock
  private ServiceHealthMonitor serviceHealthMonitor;

  private String url = "/configuration";

  private String outageUrl = "/configuration/1/2";

  private ObjectMapper objectMapper = new ObjectMapper();

  @InjectMocks
  private HealthCheckController controller;

  @Before
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  public void testAddConfiguration() throws Exception{
    String content = objectMapper.writeValueAsString(createHealthConfiguration());
    doNothing().when(serviceHealthMonitor).addRegistration(any());
    mockMvc.perform(post(url)
          .content(content)
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
  }

  @Test
  public void testAddConfiguration_failForMissingHost() throws Exception{
    HealthCheckConfiguration configuration = createHealthConfiguration();
    configuration.getConnection().setHost("");
    String content = objectMapper.writeValueAsString(configuration);
    doNothing().when(serviceHealthMonitor).addRegistration(any());
    mockMvc.perform(post(url)
          .content(content)
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
  }

  @Test
  public void testAddConfiguration_failForMissingPort() throws Exception{
    HealthCheckConfiguration configuration = createHealthConfiguration();
    configuration.getConnection().setPort(0);
    String content = objectMapper.writeValueAsString(configuration);
    doNothing().when(serviceHealthMonitor).addRegistration(any());
    mockMvc.perform(post(url)
          .content(content)
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
  }

  @Test
  public void testAddConfiguration_failForInvalidPort() throws Exception{
    HealthCheckConfiguration configuration = createHealthConfiguration();
    configuration.getConnection().setPort(233423423);
    String content = objectMapper.writeValueAsString(configuration);
    doNothing().when(serviceHealthMonitor).addRegistration(any());
    mockMvc.perform(post(url)
          .content(content)
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
  }

  @Test
  public void testAddConfiguration_failForDuplicateConfiguration() throws Exception{
    HealthCheckConfiguration configuration = createHealthConfiguration();
    String content = objectMapper.writeValueAsString(configuration);
    doThrow(HealthCheckConfigurationAlreadyExistsException.class).when(serviceHealthMonitor).addRegistration(any());
    mockMvc.perform(post(url)
          .content(content)
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
  }

  //outage
  @Test
  public void testAddOutage() throws Exception{
    String content = objectMapper.writeValueAsString(createConnection());
    doNothing().when(serviceHealthMonitor).registerOutageFor(any(), anyLong(), anyLong());
    mockMvc.perform(put(outageUrl)
          .content(content)
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
  }

  @Test
  public void testAddOutage_FailForEmptyHost() throws Exception{
    Connection connection = createConnection();
    connection.setHost("");
    String content = objectMapper.writeValueAsString(connection);
    doNothing().when(serviceHealthMonitor).registerOutageFor(any(), anyLong(), anyLong());
    mockMvc.perform(put(outageUrl)
          .content(content)
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
  }

  @Test
  public void testAddOutage_FailForInvalidPort() throws Exception{
    Connection connection = createConnection();
    connection.setPort(0);
    String content = objectMapper.writeValueAsString(connection);
    doNothing().when(serviceHealthMonitor).registerOutageFor(any(), anyLong(), anyLong());
    mockMvc.perform(put(outageUrl)
          .content(content)
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
  }

  @Test
  public void testAddOutage_FailForHighPort() throws Exception{
    Connection connection = createConnection();
    connection.setPort(2312222);
    String content = objectMapper.writeValueAsString(connection);
    doNothing().when(serviceHealthMonitor).registerOutageFor(any(), anyLong(), anyLong());
    mockMvc.perform(put(outageUrl)
          .content(content)
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
  }

  @Test
  public void testAddOutage_failForNonExistingConfiguration() throws Exception{
    Connection connection = createConnection();
    String content = objectMapper.writeValueAsString(connection);
    doThrow(NoHealthCheckConfigurationFound.class).when(serviceHealthMonitor).registerOutageFor(any(), anyLong(), anyLong());
    mockMvc.perform(put(outageUrl)
          .content(content)
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
  }

  //remove
  @Test
  public void testRemoveConfiguration() throws Exception{
    String content = objectMapper.writeValueAsString(new Connection());
    doNothing().when(serviceHealthMonitor).removeRegistration(any());
    mockMvc.perform(delete(url)
          .content(content)
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
  }

  //get

  @Test
  public void testGetConfiguration() throws Exception{
    when(serviceHealthMonitor.getServiceCongfigurations()).thenReturn(new ArrayList<>());
    mockMvc.perform(get(url)
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
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
