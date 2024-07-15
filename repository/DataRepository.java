package ro.uvt.info.dw.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import ro.uvt.info.dw.model.Data;

import java.util.List;

@Repository
public interface DataRepository extends CassandraRepository<Data, Data.DataKey> {

    @Query("SELECT * FROM main.data WHERE assetId = ?0")
    List<Data> findByAssetId(int assetId);

    @Query("SELECT * FROM main.data WHERE assetId = ?0 AND sourceId = ?1")
    List<Data> findByAssetIdAndSourceId(int assetId, int sourceId);

    @Query("SELECT * FROM main.data WHERE assetId = ?0 AND sourceId = ?1 AND businessDate > ?2 AND businessDate <= ?3")
    List<Data> findTimeSeriesData(int assetId, int sourceId, String startBusinessDate, String endBusinessDate);
}


