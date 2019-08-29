package com.globalrelay.notification;

public enum ServerStatus {
  UP, DOWN;

  public static ServerStatus statusForBoolean(boolean flag){
    return flag ?UP : DOWN;
  }
}
