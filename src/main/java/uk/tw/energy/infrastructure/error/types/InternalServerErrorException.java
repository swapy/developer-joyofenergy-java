package uk.tw.energy.infrastructure.error.types;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.tw.energy.infrastructure.error.ErrorCode;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends GenericException {

  public InternalServerErrorException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
