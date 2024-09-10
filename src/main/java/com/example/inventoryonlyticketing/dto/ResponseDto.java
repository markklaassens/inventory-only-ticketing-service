package com.example.inventoryonlyticketing.dto;

import static java.util.Objects.requireNonNull;

public record ResponseDto(int statusCode, Object body) {

  public ResponseDto {
    requireNonNull(body, "Body cannot be null");
  }

}
