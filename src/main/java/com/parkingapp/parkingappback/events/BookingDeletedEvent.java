package com.parkingapp.parkingappback.events;

import org.springframework.context.ApplicationEvent;

public class BookingDeletedEvent extends ApplicationEvent {
  public BookingDeletedEvent(Object source) {
    super(source);
  }
}
