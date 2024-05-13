package uk.tw.energy.service;

import static uk.tw.energy.infrastructure.error.ErrorCode.*;

import java.util.List;
import java.util.Objects;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.infrastructure.error.ErrorCode;
import uk.tw.energy.infrastructure.error.types.BadRequestException;

// TODO can use hibernate validators instead at request level itself!
public class MeterReadingsValidator {

  public void validateMeterReadings(MeterReadings meterReadings) {
    if (Objects.isNull(meterReadings)) {
      createException(ERR003, "Invalid input supplied for storage");
    }

    final String smartMeterId = meterReadings.smartMeterId();
    final List<ElectricityReading> electricityReadings = meterReadings.electricityReadings();

    if (Objects.isNull(smartMeterId) || smartMeterId.isBlank()) {
      createException(ERR004, "Invalid smart meter id");
    }

    if (Objects.isNull(electricityReadings) || electricityReadings.isEmpty()) {
      createException(ERR005, "Invalid electricity readings supplied!");
    }
  }

  private BadRequestException createException(ErrorCode e, String input) {
    throw new BadRequestException(e, input);
  }
}
