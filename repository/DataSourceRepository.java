package ro.uvt.info.dw.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import ro.uvt.info.dw.model.DataSource;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataSourceRepository extends CassandraRepository<DataSource, Integer> {
    Optional<DataSource> findByName(String name);

    @Query("SELECT * FROM main.data_source WHERE name = ?0")
    Optional<DataSource> findByNameCustom(String name);

    @Query("SELECT MAX(id) FROM main.data_source")
    Integer findMaxId();

    @Query("SELECT * FROM main.data_source")
    List<DataSource> findAll();
}
