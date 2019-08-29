package com.globalrelay.checker;

import com.globalrelay.model.HealthCheckConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Perform health check of a service
 */
public interface HealthChecker {

   Map<Protocol, HealthChecker> healthCheckers = new HashMap<>();

   boolean doCheck(HealthCheckConfiguration healthCheckConfiguration);

   Protocol getProtocol();

   static HealthChecker getHealthChecker(Protocol protocol){
      return healthCheckers.get(protocol);
   }

   static void registerHealthChecker(Protocol protocol, HealthChecker healthChecker){
      healthCheckers.put(protocol, healthChecker);
   }

}
