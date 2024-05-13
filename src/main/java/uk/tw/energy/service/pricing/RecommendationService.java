package uk.tw.energy.service.pricing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecommendationService {

    public List<Map.Entry<String, BigDecimal>> recommend(Map<String, BigDecimal> consumptionsForPricePlans, Integer limit) {
        List<Map.Entry<String, BigDecimal>> recommendations =
                new ArrayList<>(consumptionsForPricePlans.entrySet());
        recommendations.sort(Map.Entry.comparingByValue());

        if (limit != null && limit < recommendations.size()) {
            recommendations = recommendations.subList(0, limit);
        }

        return recommendations;
    }
}
