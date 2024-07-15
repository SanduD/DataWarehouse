package ro.uvt.info.dw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.uvt.info.dw.model.Data;
import ro.uvt.info.dw.repository.DataRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimeSeriesService {

    @Autowired
    private DataRepository dataRepository;

    public Map<String, Object> getTimeSeriesData(int assetId, int dataSourceId, String startBusinessDate, String endBusinessDate, boolean includeAttributes) {
        List<Data> dataList = dataRepository.findTimeSeriesData(assetId, dataSourceId, startBusinessDate, endBusinessDate);

        // Group by businessDate and take the most recent record per date
        Map<String, Data> latestDataPerDate = dataList.stream()
                .collect(Collectors.toMap(
                        Data::getBusinessDate,
                        data -> data,
                        (existing, replacement) -> existing.getKey().getSystemDate().after(replacement.getKey().getSystemDate()) ? existing : replacement
                ));

        List<Map<String, Object>> records = latestDataPerDate.values().stream()
                .sorted(Comparator.comparing(Data::getBusinessDate).reversed())
                .map(data -> {
                    Map<String, Object> record = new HashMap<>();
                    record.put("businessDate", data.getBusinessDate());
                    Map<String, Object> values = new HashMap<>();
                    if (data.getValuesDouble() != null) {
                        values.putAll(data.getValuesDouble());
                    }
                    if (data.getValuesInt() != null) {
                        values.putAll(data.getValuesInt());
                    }
                    if (data.getValuesText() != null) {
                        values.putAll(data.getValuesText());
                    }
                    record.put("values", values);
                    return record;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> dataResponse = new HashMap<>();
        dataResponse.put("assetId", assetId);
        dataResponse.put("datasourceId", dataSourceId);
        dataResponse.put("records", records);

        response.put("data", dataResponse);

        if (includeAttributes) {
            Set<String> attributes = new HashSet<>();
            dataList.forEach(data -> {
                if (data.getValuesDouble() != null) {
                    attributes.addAll(data.getValuesDouble().keySet());
                }
                if (data.getValuesInt() != null) {
                    attributes.addAll(data.getValuesInt().keySet());
                }
                if (data.getValuesText() != null) {
                    attributes.addAll(data.getValuesText().keySet());
                }
            });
            response.put("attributes", attributes);
        }

        return response;
    }
}
