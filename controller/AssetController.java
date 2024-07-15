package ro.uvt.info.dw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uvt.info.dw.model.Asset;
import ro.uvt.info.dw.model.Data;
import ro.uvt.info.dw.repository.AssetRepository;
import ro.uvt.info.dw.repository.DataRepository;
import ro.uvt.info.dw.repository.DataSourceRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/assets")
public class AssetController {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private DataSourceRepository dataSourceRepository;
//
//    @GetMapping
//    public ResponseEntity<List<String>> getAssets(
//            @RequestParam(defaultValue = "0") int offset,
//            @RequestParam(defaultValue = "20") int limit) {
//        List<Asset> allAssets = assetRepository.findAllAssets();
//
//        // Debug statement to verify asset data
//        System.out.println("All Assets: " + allAssets);
//
//        List<String> combinedNames = allAssets.stream()
//                .flatMap(asset -> {
//                    List<Data> dataList = dataRepository.findByAssetIdAndSourceId(asset.getId(), asset.getId());
//
//                    // Debug statement to verify dataList
//                    System.out.println("Data for Asset " + asset.getId() + ": " + dataList);
//
//                    return dataList.stream().map(data -> {
//                        String dataSourceName = dataSourceRepository.findById(data.getKey().getSourceId()).get().getName();
//                        return dataSourceName + "/" + asset.getName();
//                    });
//                })
//                .distinct()
//                .sorted()
//                .collect(Collectors.toList());
//
//        List<String> paginatedNames = combinedNames.stream()
//                .skip(offset)
//                .limit(limit)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(paginatedNames);
//    }

    @GetMapping
    public ResponseEntity<List<String>> getAssets(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        List<Asset> allAssets = assetRepository.findAllAssets();

        List<String> assetNames = allAssets.stream()
                .map(Asset::getName)
                .distinct()
                .sorted()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        return ResponseEntity.ok(assetNames);
    }
    @GetMapping("/{assetName}")
    public ResponseEntity<List<Asset>> getAssetByName(@PathVariable String assetName) {
        List<Asset> assets = assetRepository.findByNameCustom(assetName);
        return ResponseEntity.ok(assets);
    }
}
