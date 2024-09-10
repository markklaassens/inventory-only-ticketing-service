package com.example.inventoryonlyticketing.entities.enums;

public enum CarriageType {
  FIRST_CLASS("First Class", 10),
  SECOND_CLASS("Second Class", 20);

  private final String displayName;
  private final int amountOfSeats;

  CarriageType(final String displayName, final int amountOfSeats) {
    this.displayName = displayName;
    this.amountOfSeats = amountOfSeats;
  }

  public String getDisplayName() {
    return displayName;
  }

  public int getAmountOfSeats() {
    return amountOfSeats;
  }
}