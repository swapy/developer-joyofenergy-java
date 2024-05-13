package uk.tw.energy.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.infrastructure.error.ErrorCode;
import uk.tw.energy.infrastructure.error.types.BadRequestException;
import uk.tw.energy.service.meter.MeterReadingsValidator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MeterReadingsValidatorTest {

  public static final String SMART_0 = "smart-0";
  MeterReadingsValidator validator = new MeterReadingsValidator();

  @Test
  void givenMeterReadingsIsNotPresentThenThrowError() {
    assertThatThrownBy(() -> validator.validateMeterReadings(null))
        .isInstanceOf(BadRequestException.class);
  }

  @ParameterizedTest(name = "{1}")
  @MethodSource("smartMeterIdInvalid")
  void givenSmartMeterIdIsInvalidThrowError(MeterReadings input, String description) {
    assertThatThrownBy(() -> validator.validateMeterReadings(input))
        .isInstanceOf(BadRequestException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.ERR004);
  }

  @ParameterizedTest(name = "{1}")
  @MethodSource("electricityReadingsInvalid")
  void givenElectricityReadingsNotPresentThrowError(MeterReadings input, String description) {
    assertThatThrownBy(() -> validator.validateMeterReadings(input))
        .isInstanceOf(BadRequestException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.ERR005);
  }

  @Test
  void givenMeterReadingsValidThenReturnSuccessfully() {
    ElectricityReading electricityReading = new ElectricityReading(Instant.now(), BigDecimal.ONE);
    MeterReadings meterReadings = new MeterReadings(SMART_0, List.of(electricityReading));
    assertThatNoException().isThrownBy(() -> validator.validateMeterReadings(meterReadings));
  }

  private static Stream<Arguments> smartMeterIdInvalid() {
    ElectricityReading electricityReading = new ElectricityReading(Instant.now(), BigDecimal.ONE);
    return Stream.of(
        Arguments.of(new MeterReadings(null, List.of(electricityReading)), "null meter id"),
        Arguments.of(new MeterReadings("", List.of(electricityReading)), "empty meter id"),
        Arguments.of(new MeterReadings(" ", List.of(electricityReading)), "blank meter id"));
  }

  private static Stream<Arguments> electricityReadingsInvalid() {
    return Stream.of(
        Arguments.of(new MeterReadings(SMART_0, null), "null electricity readings"),
        Arguments.of(new MeterReadings(SMART_0, List.of()), "no electricity readings provided"));
  }
}
