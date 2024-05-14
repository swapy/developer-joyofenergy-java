package uk.tw.energy.service.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

public class CostCalculator {
  public static final String DEFAULT_TIME = "0.0";

  public BigDecimal calculateCost(
      List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
    BigDecimal average = calculateAverageReading(electricityReadings);
    BigDecimal timeElapsed = calculateTimeElapsed(electricityReadings);

    BigDecimal averagedCost = average.divide(timeElapsed, RoundingMode.HALF_UP);
    return averagedCost.multiply(pricePlan.getUnitRate());
  }

  private BigDecimal calculateAverageReading(List<ElectricityReading> electricityReadings) {
    BigDecimal summedReadings =
        electricityReadings.stream()
            .map(ElectricityReading::reading)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    return summedReadings.divide(
        BigDecimal.valueOf(electricityReadings.size()), RoundingMode.HALF_UP);
  }

  private BigDecimal calculateTimeElapsed(List<ElectricityReading> electricityReadings) {
    final Optional<ElectricityReading> first =
        electricityReadings.stream().min(Comparator.comparing(ElectricityReading::time));

    if (first.isEmpty()) {
      return new BigDecimal(DEFAULT_TIME);
    }

    final Optional<ElectricityReading> last =
        electricityReadings.stream().max(Comparator.comparing(ElectricityReading::time));

    if (last.isEmpty()) {
      return new BigDecimal(DEFAULT_TIME);
    }

    return BigDecimal.valueOf(
        Duration.between(first.get().time(), last.get().time()).getSeconds() / 3600.0);
  }
}
