package com.parkingapp.parkingappback.events;

import org.springframework.context.ApplicationEvent;

public class ParkingSpotChangedEvent extends ApplicationEvent {
  public ParkingSpotChangedEvent(Object source) {
    super(source);
  }
}
