package uk.tw.energy.service.pricing;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.infrastructure.error.ErrorCode;
import uk.tw.energy.infrastructure.error.types.NotFoundException;
import uk.tw.energy.service.meter.MeterReadingService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PricePlanService {

  private final List<PricePlan> pricePlans;
  private final MeterReadingService meterReadingService;

  public PricePlanService(List<PricePlan> pricePlans, MeterReadingService meterReadingService) {
    this.pricePlans = pricePlans;
    this.meterReadingService = meterReadingService;
  }

  public Map<String, BigDecimal> getConsumptionCostOfElectricityReadingsForEachPricePlan(
      String smartMeterId) {

    final List<ElectricityReading> electricityReadings =
        meterReadingService.getReadings(smartMeterId);

    final CostCalculator costCalculator = new CostCalculator();
    final Map<String, BigDecimal> consumptionsForPricePlans =
        pricePlans.stream()
            .collect(
                Collectors.toMap(
                    PricePlan::getPlanName,
                    t -> costCalculator.calculateCost(electricityReadings, t)));

    if (consumptionsForPricePlans.isEmpty()) {
      throw new NotFoundException(
          ErrorCode.ERR007, "No consumption costs found for given smart meter");
    }

    return consumptionsForPricePlans;
  }

  public List<Map.Entry<String, BigDecimal>> getRecommendations(
      String smartMeterId, Integer limit) {
    final Map<String, BigDecimal> consumptionsForPricePlans =
        this.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);
    final RecommendationService recommendationService = new RecommendationService();
    return recommendationService.recommend(consumptionsForPricePlans, limit);
  }
}
