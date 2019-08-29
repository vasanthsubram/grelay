package com.globalrelay.exception;

public class HealthCheckConfigurationAlreadyExistsException extends RuntimeException{

  public HealthCheckConfigurationAlreadyExistsException(String message) {
    super(message);
  }
}
