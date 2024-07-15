package ro.uvt.info.dw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uvt.info.dw.exception.SymbolAlreadyExistsException;
import ro.uvt.info.dw.model.Data;
import ro.uvt.info.dw.service.DataIngestionService;
import ro.uvt.info.dw.service.TimeSeriesService;

import java.util.Map;

@RestController
@RequestMapping("/data")
public class DataController {

    @Autowired
    private DataIngestionService dataIngestionService;

    @Autowired
    private TimeSeriesService timeSeriesService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getTimeSeriesData(
            @RequestParam int assetId,
            @RequestParam int dataSourceId,
            @RequestParam String startBusinessDate,
            @RequestParam String endBusinessDate,
            @RequestParam boolean includeAttributes) {

        Map<String, Object> response = timeSeriesService.getTimeSeriesData(assetId, dataSourceId, startBusinessDate, endBusinessDate, includeAttributes);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ingest/{dataset}/{ticker}")
    public ResponseEntity<?> ingestData(@PathVariable String dataset, @PathVariable String ticker) {
        try {
            Map<String, String> response = dataIngestionService.ingestData(dataset, ticker);
            if (response.containsKey("error")) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        } catch (SymbolAlreadyExistsException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.CONFLICT);
        }
    }
}
