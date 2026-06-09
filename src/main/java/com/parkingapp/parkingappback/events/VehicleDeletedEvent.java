package com.parkingapp.parkingappback.events;

import org.springframework.context.ApplicationEvent;

public class VehicleDeletedEvent extends ApplicationEvent {
  public VehicleDeletedEvent(Object source) {
    super(source);
  }
}
