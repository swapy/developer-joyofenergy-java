package uk.tw.energy.infrastructure.error.types;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.tw.energy.infrastructure.error.ErrorCode;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends GenericException {

  public BadRequestException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
