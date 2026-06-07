package com.parkingapp.parkingappback.exceptions.owners;

public class DuplicatePhoneNumberException extends RuntimeException {
  public DuplicatePhoneNumberException(String phoneNumber){
    super("Owner with this phone number already exists: " + phoneNumber);
  }
}
