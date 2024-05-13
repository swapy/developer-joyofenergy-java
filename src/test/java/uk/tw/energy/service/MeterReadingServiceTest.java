package uk.tw.energy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.builders.MeterReadingsBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.infrastructure.error.types.NotFoundException;
import uk.tw.energy.service.meter.MeterReadingService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MeterReadingServiceTest {

  public static final String METER_ID = "random-id";
  private MeterReadingService meterReadingService;

  @BeforeEach
  public void setUp() {
    meterReadingService = new MeterReadingService(new HashMap<>());
  }

  @Test
  void givenMeterIdThatDoesNotExistShouldThrowException() {
    assertThatThrownBy(() -> meterReadingService.getReadings("unknown-id"))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void givenMeterReadingThatExistsShouldReturnMeterReadings() {
    ElectricityReading electricityReading = new ElectricityReading(Instant.now(), BigDecimal.ONE);
    List<ElectricityReading> readings = List.of(electricityReading);

    meterReadingService.storeReadings(new MeterReadings(METER_ID, readings));

    assertThat(meterReadingService.getReadings(METER_ID))
        .usingRecursiveComparison()
        .isEqualTo(readings);
  }

  @Test
  void givenMeterReadingsAssociatedWithTheUserShouldStoreAssociatedWithUser() {
    ElectricityReading electricityReading = new ElectricityReading(Instant.now(), BigDecimal.TEN);
    List<ElectricityReading> readings = List.of(electricityReading);

    meterReadingService.storeReadings(new MeterReadings(METER_ID, readings));

    ElectricityReading electricityReading2 = new ElectricityReading(Instant.now(), BigDecimal.TWO);
    List<ElectricityReading> readings2 = List.of(electricityReading2);
    meterReadingService.storeReadings(new MeterReadings(METER_ID, readings2));

    List<ElectricityReading> combined = new ArrayList<>(readings);
    combined.addAll(readings2);

    assertThat(meterReadingService.getReadings(METER_ID))
        .usingRecursiveComparison()
        .isEqualTo(combined);
  }

  @Test
  void givenMultipleBatchesOfMeterReadingsShouldStore() {
    MeterReadings meterReadings =
        new MeterReadingsBuilder().setSmartMeterId(METER_ID).generateElectricityReadings().build();

    MeterReadings otherMeterReadings =
        new MeterReadingsBuilder().setSmartMeterId(METER_ID).generateElectricityReadings().build();

    meterReadingService.storeReadings(meterReadings);
    meterReadingService.storeReadings(otherMeterReadings);

    List<ElectricityReading> expectedElectricityReadings = new ArrayList<>();
    expectedElectricityReadings.addAll(meterReadings.electricityReadings());
    expectedElectricityReadings.addAll(otherMeterReadings.electricityReadings());

    assertThat(meterReadingService.getReadings(METER_ID))
        .containsExactlyInAnyOrderElementsOf(expectedElectricityReadings);
  }
}
