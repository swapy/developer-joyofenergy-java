package uk.tw.energy.infrastructure.error;

import lombok.Getter;

// NOTE in future can be more enriched with error specific data
@Getter
public enum ErrorCode {
  // generic errors
  ERR001("An unknown server error occured!"),
  ERR002("Database access error occurred"),

  // business errors
  ERR003("Invalid input supplied for storage api"),
  ERR004("Invalid smart meter id"),
  ERR005("Invalid electricityReadings supplied for given smart meter id"),
  ERR006("No meter readings found for given smart meter id"),
  ERR007("No pricings found for comparison"),
  ;

  ErrorCode(String code) {
    this.code = code;
  }

  private final String code;
}
