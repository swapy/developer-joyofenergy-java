package uk.tw.energy.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.infrastructure.error.types.NotFoundException;
import uk.tw.energy.service.meter.MeterReadingService;
import uk.tw.energy.service.pricing.AccountService;
import uk.tw.energy.service.pricing.PricePlanService;

class PricePlanComparatorControllerTest {

  private static final String PRICE_PLAN_1_ID = "test-supplier";
  private static final String PRICE_PLAN_2_ID = "best-supplier";
  private static final String PRICE_PLAN_3_ID = "second-best-supplier";
  private static final String SMART_METER_ID = "smart-meter-id";
  private PricePlanComparatorController controller;
  private MeterReadingService meterReadingService;
  private AccountService accountService;

  @BeforeEach
  public void setUp() {
    meterReadingService = new MeterReadingService(new HashMap<>());
    PricePlan pricePlan1 = new PricePlan(PRICE_PLAN_1_ID, null, BigDecimal.TEN, null);
    PricePlan pricePlan2 = new PricePlan(PRICE_PLAN_2_ID, null, BigDecimal.ONE, null);
    PricePlan pricePlan3 = new PricePlan(PRICE_PLAN_3_ID, null, BigDecimal.valueOf(2), null);

    List<PricePlan> pricePlans = Arrays.asList(pricePlan1, pricePlan2, pricePlan3);
    PricePlanService tariffService = new PricePlanService(pricePlans, meterReadingService);

    Map<String, String> meterToTariffs = new HashMap<>();
    meterToTariffs.put(SMART_METER_ID, PRICE_PLAN_1_ID);
    accountService = new AccountService(meterToTariffs);

    controller = new PricePlanComparatorController(tariffService, accountService);
  }

  @Test
  void shouldCalculateCostForMeterReadingsForEveryPricePlan() {

    ElectricityReading electricityReading =
        new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(15.0));
    ElectricityReading otherReading =
        new ElectricityReading(Instant.now(), BigDecimal.valueOf(5.0));
    meterReadingService.storeReadings(
        new MeterReadings(SMART_METER_ID, Arrays.asList(electricityReading, otherReading)));

    Map<String, BigDecimal> expectedPricePlanToCost = new HashMap<>();
    expectedPricePlanToCost.put(PRICE_PLAN_1_ID, BigDecimal.valueOf(100.0));
    expectedPricePlanToCost.put(PRICE_PLAN_2_ID, BigDecimal.valueOf(10.0));
    expectedPricePlanToCost.put(PRICE_PLAN_3_ID, BigDecimal.valueOf(20.0));

    Map<String, Object> expected = new HashMap<>();
    expected.put(PricePlanComparatorController.PRICE_PLAN_ID_KEY, PRICE_PLAN_1_ID);
    expected.put(PricePlanComparatorController.PRICE_PLAN_COMPARISONS_KEY, expectedPricePlanToCost);
    assertThat(controller.calculatedCostForEachPricePlan(SMART_METER_ID).getBody())
        .isEqualTo(expected);
  }

  @Test
  void shouldRecommendCheapestPricePlansNoLimitForMeterUsage() throws Exception {

    ElectricityReading electricityReading =
        new ElectricityReading(Instant.now().minusSeconds(1800), BigDecimal.valueOf(35.0));
    ElectricityReading otherReading =
        new ElectricityReading(Instant.now(), BigDecimal.valueOf(3.0));
    meterReadingService.storeReadings(
        new MeterReadings(SMART_METER_ID, Arrays.asList(electricityReading, otherReading)));

    List<Map.Entry<String, BigDecimal>> expectedPricePlanToCost = new ArrayList<>();
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_2_ID, BigDecimal.valueOf(38.0)));
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_3_ID, BigDecimal.valueOf(76.0)));
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_1_ID, BigDecimal.valueOf(380.0)));

    assertThat(controller.recommendCheapestPricePlans(SMART_METER_ID, null).getBody())
        .isEqualTo(expectedPricePlanToCost);
  }

  @Test
  void shouldRecommendLimitedCheapestPricePlansForMeterUsage() throws Exception {

    ElectricityReading electricityReading =
        new ElectricityReading(Instant.now().minusSeconds(2700), BigDecimal.valueOf(5.0));
    ElectricityReading otherReading =
        new ElectricityReading(Instant.now(), BigDecimal.valueOf(20.0));
    meterReadingService.storeReadings(
        new MeterReadings(SMART_METER_ID, Arrays.asList(electricityReading, otherReading)));

    List<Map.Entry<String, BigDecimal>> expectedPricePlanToCost = new ArrayList<>();
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_2_ID, BigDecimal.valueOf(16.7)));
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_3_ID, BigDecimal.valueOf(33.4)));

    assertThat(controller.recommendCheapestPricePlans(SMART_METER_ID, 2).getBody())
        .isEqualTo(expectedPricePlanToCost);
  }

  @Test
  void shouldRecommendCheapestPricePlansMoreThanLimitAvailableForMeterUsage() throws Exception {

    ElectricityReading electricityReading =
        new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(25.0));
    ElectricityReading otherReading =
        new ElectricityReading(Instant.now(), BigDecimal.valueOf(3.0));
    meterReadingService.storeReadings(
        new MeterReadings(SMART_METER_ID, Arrays.asList(electricityReading, otherReading)));

    List<Map.Entry<String, BigDecimal>> expectedPricePlanToCost = new ArrayList<>();
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_2_ID, BigDecimal.valueOf(14.0)));
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_3_ID, BigDecimal.valueOf(28.0)));
    expectedPricePlanToCost.add(
        new AbstractMap.SimpleEntry<>(PRICE_PLAN_1_ID, BigDecimal.valueOf(140.0)));

    assertThat(controller.recommendCheapestPricePlans(SMART_METER_ID, 5).getBody())
        .isEqualTo(expectedPricePlanToCost);
  }

  @Test
  void givenNoMatchingMeterIdShouldReturnNotFound() {
    assertThatThrownBy(() -> controller.calculatedCostForEachPricePlan("not-found"))
        .isInstanceOf(NotFoundException.class);
  }
}
