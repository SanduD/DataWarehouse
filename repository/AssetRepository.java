package ro.uvt.info.dw.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import ro.uvt.info.dw.model.Asset;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends CassandraRepository<Asset, Integer> {
    Optional<Asset> findByName(String ticker);

    @Query("SELECT * FROM main.asset WHERE name = ?0")
    List<Asset> findByNameCustom(String name);

    @Query("SELECT MAX(id) FROM main.asset")
    Integer findMaxId();

    @Query("SELECT name FROM main.asset")
    List<Asset> findAllAssets();

}
