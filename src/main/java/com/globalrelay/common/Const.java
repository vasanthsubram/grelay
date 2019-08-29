package com.globalrelay.common;

public class Const {

  //minimum port allowed
  public static int MIN_PORT = 1;
  //max port allowed
  public static int MAX_PORT = 65535;
  //minimum frequncy interval for health check
  public static int MIN_FREQUENCY = 1;
  //factor by which the grace time should be greater than frequency
  public static int GRACE_TIME_FACTOR = 2;
  //size of the scheduled thread pool
  public static int THREAD_POOL_SIZE = 5;
}
