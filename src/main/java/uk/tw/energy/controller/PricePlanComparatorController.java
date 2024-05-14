package uk.tw.energy.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.service.pricing.AccountService;
import uk.tw.energy.service.pricing.PricePlanService;

@RestController
@RequestMapping("/price-plans")
public class PricePlanComparatorController {

  public static final String PRICE_PLAN_ID_KEY = "pricePlanId";
  public static final String PRICE_PLAN_COMPARISONS_KEY = "pricePlanComparisons";
  private final PricePlanService pricePlanService;
  private final AccountService accountService;

  public PricePlanComparatorController(
      PricePlanService pricePlanService, AccountService accountService) {
    this.pricePlanService = pricePlanService;
    this.accountService = accountService;
  }

  @GetMapping("/compare-all/{smartMeterId}")
  public ResponseEntity<Map<String, Object>> calculatedCostForEachPricePlan(
      @PathVariable String smartMeterId) {
    String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
    Map<String, BigDecimal> consumptionsForPricePlans =
        pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

    Map<String, Object> pricePlanComparisons = new HashMap<>();
    pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
    pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, consumptionsForPricePlans);

    return ResponseEntity.ok(pricePlanComparisons);
  }

  @GetMapping("/recommend/{smartMeterId}")
  public ResponseEntity<List<Map.Entry<String, BigDecimal>>> recommendCheapestPricePlans(
      @PathVariable String smartMeterId,
      @RequestParam(value = "limit", required = false) Integer limit) {
    final List<Map.Entry<String, BigDecimal>> recommendations =
        pricePlanService.getRecommendations(smartMeterId, limit);
    return ResponseEntity.ok(recommendations);
  }
}
