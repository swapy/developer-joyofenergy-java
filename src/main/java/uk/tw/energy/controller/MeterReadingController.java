package uk.tw.energy.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.meter.MeterReadingService;

@RestController
@RequestMapping("/readings")
public class MeterReadingController {

  private final MeterReadingService meterReadingService;

  public MeterReadingController(MeterReadingService meterReadingService) {
    this.meterReadingService = meterReadingService;
  }

  @PostMapping("/store")
  public ResponseEntity<String> storeReadings(@RequestBody MeterReadings meterReadings) {
    meterReadingService.storeReadings(meterReadings);
    return ResponseEntity.ok("Success");
  }

  @GetMapping("/read/{smartMeterId}")
  public ResponseEntity<List<ElectricityReading>> readReadings(@PathVariable String smartMeterId) {
    final List<ElectricityReading> readings = meterReadingService.getReadings(smartMeterId);
    return ResponseEntity.ok(readings);
  }
}
