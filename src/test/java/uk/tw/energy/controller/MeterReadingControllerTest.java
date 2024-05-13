package uk.tw.energy.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.tw.energy.builders.MeterReadingsBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.MeterReadingService;

class MeterReadingControllerTest {

  private static final String SMART_METER_ID = "10101010";
  private MeterReadingController meterReadingController;
  private MeterReadingService meterReadingService;

  @BeforeEach
  public void setUp() {
    this.meterReadingService = new MeterReadingService();
    this.meterReadingController = new MeterReadingController(meterReadingService);
  }

  @Test
  void givenNoMeterIdIsSuppliedWhenStoringShouldReturnErrorResponse() {
    MeterReadings meterReadings = new MeterReadings(null, Collections.emptyList());
    assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void givenEmptyMeterReadingShouldReturnErrorResponse() {
    MeterReadings meterReadings = new MeterReadings(SMART_METER_ID, Collections.emptyList());
    assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void givenNullReadingsAreSuppliedWhenStoringShouldReturnErrorResponse() {
    MeterReadings meterReadings = new MeterReadings(SMART_METER_ID, null);
    assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void givenMultipleBatchesOfMeterReadingsShouldStore() {
    MeterReadings meterReadings =
        new MeterReadingsBuilder()
            .setSmartMeterId(SMART_METER_ID)
            .generateElectricityReadings()
            .build();

    MeterReadings otherMeterReadings =
        new MeterReadingsBuilder()
            .setSmartMeterId(SMART_METER_ID)
            .generateElectricityReadings()
            .build();

    meterReadingController.storeReadings(meterReadings);
    meterReadingController.storeReadings(otherMeterReadings);

    List<ElectricityReading> expectedElectricityReadings = new ArrayList<>();
    expectedElectricityReadings.addAll(meterReadings.electricityReadings());
    expectedElectricityReadings.addAll(otherMeterReadings.electricityReadings());

    assertThat(meterReadingService.getReadings(SMART_METER_ID))
        .containsExactlyInAnyOrderElementsOf(expectedElectricityReadings);
  }

  @Test
  void givenMeterReadingsAssociatedWithTheUserShouldStoreAssociatedWithUser() {
    MeterReadings meterReadings =
        new MeterReadingsBuilder()
            .setSmartMeterId(SMART_METER_ID)
            .generateElectricityReadings()
            .build();

    MeterReadings otherMeterReadings =
        new MeterReadingsBuilder().setSmartMeterId("00001").generateElectricityReadings().build();

    meterReadingController.storeReadings(meterReadings);
    meterReadingController.storeReadings(otherMeterReadings);

    assertThat(meterReadingService.getReadings(SMART_METER_ID))
        .containsExactlyInAnyOrderElementsOf(meterReadings.electricityReadings());
  }

  @Test
  void givenMeterIdThatIsNotRecognisedShouldReturnNotFound() {
    assertThat(meterReadingController.readReadings(SMART_METER_ID).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }
}
