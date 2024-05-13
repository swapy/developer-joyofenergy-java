package uk.tw.energy.infrastructure.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.tw.energy.infrastructure.error.types.BadRequestException;
import uk.tw.energy.infrastructure.error.types.GenericException;
import uk.tw.energy.infrastructure.error.types.InternalServerErrorException;
import uk.tw.energy.infrastructure.error.types.NotFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(BadRequestException.class)
  ErrorMessage handleBadRequest(BadRequestException ex) {
    final ErrorCode errorCode = ex.getErrorCode();
    log.error(
        "Bad request error with error code: {} with details : {}", ex.getMessage(), errorCode);
    return new ErrorMessage(errorCode.name(), "Bad request!");
  }

  @ExceptionHandler({NotFoundException.class})
  ErrorMessage handleNotFound(Exception ex) {
    if (ex instanceof GenericException genericException) {
      final ErrorCode errorCode = genericException.getErrorCode();
      log.error(
          "Not found error with error code: {} with details : {}", ex.getMessage(), errorCode);
      return new ErrorMessage(errorCode.name(), "Not found!");
    }
    return new ErrorMessage("NOT_FOUND", "Resource not found");
  }

  @ExceptionHandler(value = {InternalServerErrorException.class})
  ErrorMessage handleInternalServerError(Exception ex) {
    final String message = "Server error occurred!";
    if (ex instanceof GenericException genericException) {
      final ErrorCode errorCode = genericException.getErrorCode();
      log.error(
          "Internal server error with error code: {} with details : {}",
          ex.getMessage(),
          errorCode);
      return new ErrorMessage(errorCode.name(), message);
    }

    return new ErrorMessage("SERVER_ERROR", message);
  }
}
