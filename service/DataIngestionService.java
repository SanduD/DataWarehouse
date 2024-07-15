package ro.uvt.info.dw.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.uvt.info.dw.exception.SymbolAlreadyExistsException;
import ro.uvt.info.dw.model.Asset;
import ro.uvt.info.dw.model.Data;
import ro.uvt.info.dw.model.Data.DataKey;
import ro.uvt.info.dw.model.DataSource;
import ro.uvt.info.dw.repository.AssetRepository;
import ro.uvt.info.dw.repository.DataRepository;
import ro.uvt.info.dw.repository.DataSourceRepository;

import java.io.IOException;
import java.util.*;

@Service
public class DataIngestionService {

    @Autowired
    private NasdaqDataService nasdaqDataService;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    private synchronized int getNextAssetId() {
        Integer maxId = assetRepository.findMaxId();
        return (maxId != null ? maxId : 0) + 1;
    }

    private synchronized int getNextDataSourceId() {
        Integer maxId = dataSourceRepository.findMaxId();
        return (maxId != null ? maxId : 0) + 1;
    }

    public Map<String, String> ingestData(String dataset, String ticker) {
        try {
            JsonNode dataNode = nasdaqDataService.getFinancialData(dataset, ticker);
            if (dataNode.has("quandl_error")) {
                String errorMessage = dataNode.get("quandl_error").get("message").asText();
                throw new SymbolAlreadyExistsException(errorMessage);
            }
            JsonNode datasetNode = dataNode.get("dataset");

            String date = datasetNode.get("end_date").asText();
            JsonNode columnNamesNode = datasetNode.get("column_names");
            JsonNode dataEntriesNode = datasetNode.get("data");

            // Debug statement to check dataEntriesNode
//            System.out.println("dataEntriesNode: " + dataEntriesNode);

//            if (!dataEntriesNode.isArray()) {
//                throw new IllegalArgumentException("Data entries node is not an array");
//            }

            List<JsonNode> dataEntries = new ArrayList<>();
            dataEntriesNode.forEach(dataEntries::add);

            // Debug statement to check the size of dataEntries
//            System.out.println("Number of data entries: " + dataEntries.size());

            // Check if Asset exists
            Optional<Asset> existingAsset = assetRepository.findByName(ticker);
            if (existingAsset.isPresent()) {
                throw new SymbolAlreadyExistsException("Symbol already exists");
            }

            int newAssetId = getNextAssetId();

            Asset asset = new Asset();
            asset.setId(newAssetId);
            asset.setName(ticker);
            asset.setDescription(datasetNode.get("description").asText());
            asset.setSystemDate(new Date());
            asset.setAdditionalAttributes(Collections.singletonMap("Name", datasetNode.get("name").asText()));
            assetRepository.save(asset);

            // Check if DataSource exists
            String sourceName = dataset;
            Optional<DataSource> existingDataSource = dataSourceRepository.findByName(sourceName);
            DataSource dataSource;
            if (existingDataSource.isPresent()) {
                dataSource = existingDataSource.get();
            } else {
                int newDataSourceId = getNextDataSourceId();

                dataSource = new DataSource();
                dataSource.setId(newDataSourceId);
                dataSource.setName(sourceName);
                dataSource.setDescription("DESCRIPTION FOR " + sourceName);
                dataSource.setSystemDate(new Date());

                Set<String> attributes = new HashSet<>();
                columnNamesNode.forEach(node -> attributes.add(node.asText()));
                dataSource.setAttributes(attributes);
                dataSourceRepository.save(dataSource);
            }

            int dateIndex = -1;
            for (int i = 0; i < columnNamesNode.size(); i++) {
                if (columnNamesNode.get(i).asText().equalsIgnoreCase("Date")) {
                    dateIndex = i;
                    break;
                }
            }

            if (dateIndex == -1) {
                throw new IllegalArgumentException("Date column not found in column names");
            }

            // Iterate over the first three data entries and create Data entries
            for (int entryIndex = 0; entryIndex < Math.min(dataEntries.size(), 3); entryIndex++) {
                JsonNode firstDataEntry = dataEntries.get(entryIndex);
                String businessDate = firstDataEntry.get(dateIndex).asText();


                // Create Data entry
                DataKey dataKey = new DataKey();
                dataKey.setAssetId(asset.getId());
                dataKey.setSourceId(dataSource.getId());
                dataKey.setSystemDate(new Date());
                dataKey.setBusinessDate(businessDate);

                Data newData = new Data();
                newData.setKey(dataKey);

                // Populate values based on column names
                Map<String, Double> valuesDouble = new HashMap<>();
                Map<String, Integer> valuesInt = new HashMap<>();
                Map<String, String> valuesText = new HashMap<>();
                for (int i = 1; i < columnNamesNode.size(); i++) {
                    String columnName = columnNamesNode.get(i).asText();
                    JsonNode valueNode = firstDataEntry.get(i);
                    if (valueNode.isDouble()) {
                        valuesDouble.put(columnName, valueNode.asDouble());
                    } else if (valueNode.isInt()) {
                        valuesInt.put(columnName, valueNode.asInt());
                    } else if (valueNode.isTextual()) {
                        valuesText.put(columnName, valueNode.asText());
                    }
                }
                newData.setValuesDouble(valuesDouble);
                newData.setValuesInt(valuesInt);
                newData.setValuesText(valuesText);

                dataRepository.save(newData);
            }

            // Return a success message
            Map<String, String> response = new HashMap<>();
            response.put("message", dataset + "/" + ticker + " added to database");
            return response;

        } catch (IOException e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to ingest data");
            return errorResponse;
        }
    }
}
