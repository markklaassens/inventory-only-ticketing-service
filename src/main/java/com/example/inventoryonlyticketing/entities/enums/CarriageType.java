package com.example.inventoryonlyticketing.entities.enums;

public enum CarriageType {
  FIRST_CLASS("First Class"),
  SECOND_CLASS("Second Class");

  private final String displayName;

  CarriageType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}