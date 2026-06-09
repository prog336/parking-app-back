package com.parkingapp.parkingappback.events;

import org.springframework.context.ApplicationEvent;

public class OwnerDeletedEvent extends ApplicationEvent {
  public OwnerDeletedEvent(Object source) {
    super(source);
  }
}
