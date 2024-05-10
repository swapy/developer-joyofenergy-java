package uk.tw.energy.infrastructure.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.tw.energy.infrastructure.error.types.BadRequestException;
import uk.tw.energy.infrastructure.error.types.InternalServerErrorException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(BadRequestException.class)
  ErrorMessage handleBadRequest(BadRequestException ex) {
    final ErrorCode errorCode = ex.getErrorCode();
    log.error("Bad request error with error code: {} with details : {}", ex.getMessage(), errorCode);
    return new ErrorMessage(errorCode.name(), "Bad request!");
  }

  @ExceptionHandler(value = {Exception.class, InternalServerErrorException.class})
  ErrorMessage handleInternalServerError(BadRequestException ex) {
    final ErrorCode errorCode = ex.getErrorCode();
    log.error(
        "Internal server error with error code: {} with details : {}", ex.getMessage(), errorCode);
    return new ErrorMessage(errorCode.name(), "Server error occurred!");
  }


}
