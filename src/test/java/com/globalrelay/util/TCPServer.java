package com.globalrelay.util;

import java.net.*;
import java.io.*;

// One time use TCP server - as soon as a client connects, the server shuts down
public class TCPServer {
  private Socket socket = null;
  private ServerSocket server = null;
  private int port;

  public TCPServer(int port) {
    this.port = port;
    try {
      server = new ServerSocket(port);
      System.out.println("Started Server at port " + port);
      socket = server.accept();
      socket.close();
      try {
        Thread.sleep(10 * 1000);
      }catch (Exception ex){}
      server.close();
    } catch (IOException ex) {
      System.out.println(ex);
    }
  }

  public static void main(String[] args){
    new Thread(new Runnable(){ public void run(){new TCPServer(5000);}}).start();
    new Thread(new Runnable(){ public void run(){new TCPServer(6000);}}).start();
//    System.out.println(System.currentTimeMillis());
  }
}
