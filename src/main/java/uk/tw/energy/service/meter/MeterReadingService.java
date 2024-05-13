package uk.tw.energy.service.meter;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.infrastructure.error.ErrorCode;
import uk.tw.energy.infrastructure.error.types.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class MeterReadingService {

    private final Map<String, List<ElectricityReading>> meterAssociatedReadings;

    public MeterReadingService(Map<String, List<ElectricityReading>> meterAssociatedReadings) {
        this.meterAssociatedReadings = meterAssociatedReadings;
    }

    public List<ElectricityReading> getReadings(String smartMeterId) {
        final List<ElectricityReading> electricityReadings = meterAssociatedReadings.get(smartMeterId);

        if (Objects.isNull(electricityReadings) || electricityReadings.isEmpty()) {
            throw new NotFoundException(
                    ErrorCode.ERR006, "No meter readings found for given smart meter id");
        }

        return electricityReadings;
    }

    public void storeReadings(MeterReadings meterReadings) {
        final MeterReadingsValidator validator = new MeterReadingsValidator();
        validator.validateMeterReadings(meterReadings);

        final String smartMeterId = meterReadings.smartMeterId();
        final List<ElectricityReading> electricityReadings = meterReadings.electricityReadings();

        final List<ElectricityReading> updatedReadings =
                meterAssociatedReadings.computeIfAbsent(smartMeterId, s -> new ArrayList<>());
        updatedReadings.addAll(electricityReadings);
    }
}
