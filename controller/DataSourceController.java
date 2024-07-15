package ro.uvt.info.dw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uvt.info.dw.model.DataSource;
import ro.uvt.info.dw.repository.DataSourceRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/data-sources")
public class DataSourceController {

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @GetMapping
    public ResponseEntity<List<String>> getAllDataSources(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        List<DataSource> allDataSources = dataSourceRepository.findAll();
        List<String> dataSourceNames = allDataSources.stream()
                .map(DataSource::getName)
                .distinct()
                .sorted()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dataSourceNames);
    }

    @GetMapping("/{dataSourceId}")
    public ResponseEntity<DataSource> getDataSourceById(@PathVariable int dataSourceId) {
        Optional<DataSource> dataSource = dataSourceRepository.findById(dataSourceId);
        return dataSource.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{dataSourceName}")
    public ResponseEntity<DataSource> getDataSourceByName(@PathVariable String dataSourceName) {
        Optional<DataSource> dataSource = dataSourceRepository.findByNameCustom(dataSourceName);
        return dataSource.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
