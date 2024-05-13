package uk.tw.energy.service.pricing;

import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CostCalculatorTest {

  CostCalculator costCalculator = new CostCalculator();

  @Test
  void shouldCalculatePriceCorrectly() {
    ElectricityReading electricityReading =
        new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(15.0));
    ElectricityReading otherReading =
        new ElectricityReading(Instant.now(), BigDecimal.valueOf(5.0));

    PricePlan pricePlan = new PricePlan("test", "test", BigDecimal.ONE, List.of());
    BigDecimal actual =
        costCalculator.calculateCost(List.of(electricityReading, otherReading), pricePlan);

    assertThat(actual).isEqualTo(new BigDecimal("10.0"));
  }
}
