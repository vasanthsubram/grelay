package com.globalrelay.exception;

public class NoHealthCheckConfigurationFound extends RuntimeException{

  public NoHealthCheckConfigurationFound(String message) {
    super(message);
  }
}
