package uk.tw.energy.infrastructure.error.types;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.tw.energy.infrastructure.error.ErrorCode;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends GenericException {

  public NotFoundException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
