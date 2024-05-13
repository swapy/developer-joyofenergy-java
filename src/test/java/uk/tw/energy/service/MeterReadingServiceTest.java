package uk.tw.energy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.infrastructure.error.types.NotFoundException;

class MeterReadingServiceTest {

  private MeterReadingService meterReadingService;

  @BeforeEach
  public void setUp() {
    meterReadingService = new MeterReadingService();
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

    meterReadingService.storeReadings(new MeterReadings("random-id", readings));

    assertThat(meterReadingService.getReadings("random-id"))
        .containsExactlyInAnyOrderElementsOf(readings);
  }
}
