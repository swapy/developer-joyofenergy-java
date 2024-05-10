package uk.tw.energy.infrastructure.error;

import lombok.Getter;

//NOTE in future can be more enriched with error specific data
@Getter
public enum ErrorCode {
  // generic errors
  ERR001("An unknown server error occured!"),
  ERR002("Database access error occurred");

  ErrorCode(String code) {
    this.code = code;
  }

  private final String code;
}
