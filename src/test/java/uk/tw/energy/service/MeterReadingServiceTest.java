package uk.tw.energy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.infrastructure.error.types.NotFoundException;

class MeterReadingServiceTest {

  public static final String RANDOM_ID = "random-id";
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

    meterReadingService.storeReadings(new MeterReadings(RANDOM_ID, readings));

    assertThat(meterReadingService.getReadings(RANDOM_ID))
        .usingRecursiveComparison()
        .isEqualTo(readings);
  }

  @Test
  void givenExistingMeterReadingsOnStoringNewShouldSaveAll() {
    ElectricityReading electricityReading = new ElectricityReading(Instant.now(), BigDecimal.TEN);
    List<ElectricityReading> readings = List.of(electricityReading);

    meterReadingService.storeReadings(new MeterReadings(RANDOM_ID, readings));

    ElectricityReading electricityReading2 = new ElectricityReading(Instant.now(), BigDecimal.TWO);
    List<ElectricityReading> readings2 = List.of(electricityReading2);
    meterReadingService.storeReadings(new MeterReadings(RANDOM_ID, readings2));

    List<ElectricityReading> combined = new ArrayList<>(readings);
    combined.addAll(readings2);

    assertThat(meterReadingService.getReadings(RANDOM_ID))
        .usingRecursiveComparison()
        .isEqualTo(combined);
  }
}
