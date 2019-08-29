package com.globalrelay.model;


import com.globalrelay.checker.Protocol;

/**
 * A class to hold the connection details of the service
 */
public class Connection {
  private Protocol protocol;
  private String host;
  private int port;

  public Connection(){
  }

  public Connection(Protocol protocol, String host, int port) {
    this.protocol = protocol;
    this.host = host;
    this.port = port;
  }

  public Protocol getProtocol() {
    return protocol;
  }

  public void setProtocol(Protocol protocol) {
    this.protocol = protocol;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Connection)) return false;

    Connection that = (Connection) o;

    if (port != that.port) return false;
    return host.equals(that.host);

  }

  @Override
  public int hashCode() {
    int result = host != null ? host.hashCode() : 0;
    result = 31 * result + port;
    return result;
  }

  @Override
  public String toString() {
    return "Connection{" +
          "protocol=" + protocol +
          ", host='" + host + '\'' +
          ", port=" + port +
          '}';
  }
}
