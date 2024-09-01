package com.example.inventoryonlyticketing.dto;

import static java.util.Objects.requireNonNull;

import com.example.inventoryonlyticketing.interfaces.Response;
import org.jetbrains.annotations.NotNull;

public record ResponseDto(int statusCode, Object body) implements Response {

  public ResponseDto {
    requireNonNull(body, "Body cannot be null");
  }

  @Override
  public int getStatusCode() {
    return statusCode;
  }

  @Override
  @NotNull
  public Object getBody() {
    return body;
  }
}
