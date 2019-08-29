package com.globalrelay.controller;

import com.globalrelay.service.ServiceHealthMonitor;
import com.globalrelay.common.Const;
import com.globalrelay.exception.HealthCheckConfigurationAlreadyExistsException;
import com.globalrelay.exception.NoHealthCheckConfigurationFound;
import com.globalrelay.model.Connection;
import com.globalrelay.model.HealthCheckConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class HealthCheckController {

  @Autowired
  private ServiceHealthMonitor serviceHealthMonitor;

  @PostMapping(path = "/configuration", consumes = "application/json")
  public ResponseEntity<String> addNewConfiguration(@RequestBody HealthCheckConfiguration healthCheckConfiguration){
    if(StringUtils.isEmpty(healthCheckConfiguration.getConnection().getHost())){
      return new ResponseEntity<>("Connection host cannot be empty", HttpStatus.BAD_REQUEST);
    }
    int port = healthCheckConfiguration.getConnection().getPort();
    if(port< Const.MIN_PORT || port > Const.MAX_PORT){
      return new ResponseEntity<>("Connection host must be positive number less than  " + Const.MAX_PORT, HttpStatus.BAD_REQUEST);
    }

    if(healthCheckConfiguration.getFrequency()< Const.MIN_FREQUENCY){
      healthCheckConfiguration.setFrequency(Const.MIN_FREQUENCY);
    }

    if(healthCheckConfiguration.getGraceTime()<= healthCheckConfiguration.getFrequency()){
      healthCheckConfiguration.setGraceTime(healthCheckConfiguration.getFrequency() * Const.GRACE_TIME_FACTOR);
    }

    try {
      serviceHealthMonitor.addRegistration(healthCheckConfiguration);
    } catch (HealthCheckConfigurationAlreadyExistsException ex){
      return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping(path = "/configuration", consumes = "application/json")
  public ResponseEntity<String> deleteConfiguration(@RequestBody Connection connection){
    serviceHealthMonitor.removeRegistration(connection);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(path = "/configuration/{startTime}/{endTime}", consumes = "application/json")
  public ResponseEntity<String> addServiceOutage(@RequestBody Connection connection,
                                                 @PathVariable("startTime") long startTime,
                                                 @PathVariable("endTime") long endTime){
    if(StringUtils.isEmpty(connection.getHost())){
      return new ResponseEntity<>("Connection host cannot be empty", HttpStatus.BAD_REQUEST);
    }
    if(connection.getPort()< Const.MIN_PORT || connection.getPort()> Const.MAX_PORT){
      return new ResponseEntity<>("Connection host must be positive number less than " + Const.MAX_PORT, HttpStatus.BAD_REQUEST);
    }
    if(endTime <= startTime){
      return new ResponseEntity<>("Outage end time cannot be less than start time", HttpStatus.BAD_REQUEST);
    }

    try {
      serviceHealthMonitor.registerOutageFor(connection, startTime, endTime);
    } catch(NoHealthCheckConfigurationFound ex){
      return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(path = "/configuration")
  public ResponseEntity<Collection<HealthCheckConfiguration>> getServiceConfigurations(){
    Collection<HealthCheckConfiguration> configs = serviceHealthMonitor.getServiceCongfigurations();
    return new ResponseEntity<Collection<HealthCheckConfiguration>>(configs, HttpStatus.OK);
  }

}
