package com.parkingapp.parkingappback.events;

import org.springframework.context.ApplicationEvent;

public class VehicleChangedEvent extends ApplicationEvent {
  public VehicleChangedEvent(Object source) {
    super(source);
  }
}
