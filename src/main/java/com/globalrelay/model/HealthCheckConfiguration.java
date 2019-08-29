package com.globalrelay.model;

import com.globalrelay.checker.Protocol;
import com.globalrelay.notification.NotifierType;

import java.util.List;

/**
 * A class to hold the details of the configuration of the service to be monitored
 */
public class HealthCheckConfiguration {
  private Connection connection;
  private int frequency;
  private List<String> subscribers;
  private NotifierType notifierType;
  private boolean isServiceOutage;
  private long outageStartTime;
  private long outageEndTime;
  private int graceTime;

  public Protocol getProtocol() {
    return connection.getProtocol();
  }

  public String getHost() {
    return connection.getHost();
  }

  public int getPort() {
    return connection.getPort();
  }

  public Connection getConnection() {
    return connection;
  }

  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  public List<String> getSubscribers() {
    return subscribers;
  }

  public void setSubscribers(List<String> subscribers) {
    this.subscribers = subscribers;
  }

  public int getFrequency() {
    return frequency;
  }

  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  public NotifierType getNotifierType() {
    return notifierType;
  }

  public void setNotifierType(NotifierType notifierType) {
    this.notifierType = notifierType;
  }

  public boolean isServiceOutage() {
    return isServiceOutage;
  }

  public void setServiceOutage(boolean serviceOutage) {
    isServiceOutage = serviceOutage;
  }

  public long getOutageStartTime() {
    return outageStartTime;
  }

  public void setOutageStartTime(long outageStartTime) {
    this.outageStartTime = outageStartTime;
  }

  public long getOutageEndTime() {
    return outageEndTime;
  }

  public void setOutageEndTime(long outageEndTime) {
    this.outageEndTime = outageEndTime;
  }

  public int getGraceTime() {
    return graceTime;
  }

  public void setGraceTime(int graceTime) {
    this.graceTime = graceTime;
  }
}
