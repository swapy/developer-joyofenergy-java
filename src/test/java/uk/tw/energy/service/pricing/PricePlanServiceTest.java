package uk.tw.energy.service.pricing;

import org.junit.jupiter.api.BeforeEach;
import uk.tw.energy.controller.PricePlanComparatorController;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.service.meter.MeterReadingService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PricePlanServiceTest {

  private static final String PRICE_PLAN_1_ID = "test-supplier";
  private static final String PRICE_PLAN_2_ID = "best-supplier";
  private static final String PRICE_PLAN_3_ID = "second-best-supplier";
  private static final String SMART_METER_ID = "smart-meter-id";
  private PricePlanComparatorController controller;
  private MeterReadingService meterReadingService;
  private AccountService accountService;
  private PricePlanService tariffService;

  @BeforeEach
  public void setUp() {
    meterReadingService = new MeterReadingService(new HashMap<>());
    PricePlan pricePlan1 = new PricePlan(PRICE_PLAN_1_ID, null, BigDecimal.TEN, null);
    PricePlan pricePlan2 = new PricePlan(PRICE_PLAN_2_ID, null, BigDecimal.ONE, null);
    PricePlan pricePlan3 = new PricePlan(PRICE_PLAN_3_ID, null, BigDecimal.valueOf(2), null);

    List<PricePlan> pricePlans = Arrays.asList(pricePlan1, pricePlan2, pricePlan3);
    tariffService = new PricePlanService(pricePlans, meterReadingService);

    Map<String, String> meterToTariffs = new HashMap<>();
    meterToTariffs.put(SMART_METER_ID, PRICE_PLAN_1_ID);
    accountService = new AccountService(meterToTariffs);

    controller = new PricePlanComparatorController(tariffService, accountService);
  }
}
