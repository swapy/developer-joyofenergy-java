package uk.tw.energy.infrastructure.error.types;

import lombok.Getter;
import uk.tw.energy.infrastructure.error.ErrorCode;

@Getter
public class GenericException extends RuntimeException {

  private final ErrorCode errorCode;

  public GenericException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}
