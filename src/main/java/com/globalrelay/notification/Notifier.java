package com.globalrelay.notification;

import com.globalrelay.model.HealthCheckConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Notify the subscribers about the status of a service
 */
public interface Notifier {

  Map<NotifierType, Notifier> notifiers = new HashMap<>();

  void notify(HealthCheckConfiguration healthCheckConfiguration, ServerStatus serverStatus);

  NotifierType getNotifierType();

  static Notifier getNotifier(NotifierType notifierType){
    return notifiers.get(notifierType);
  }

  static void registerNotifier(NotifierType notifierType, Notifier notifier){
     notifiers.put(notifierType, notifier);
  }

}
