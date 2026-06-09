package com.parkingapp.parkingappback.events;

import org.springframework.context.ApplicationEvent;

public class OwnerChangedEvent extends ApplicationEvent {
  public OwnerChangedEvent(Object source) {
    super(source);
  }
}
