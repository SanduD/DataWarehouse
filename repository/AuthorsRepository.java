package ro.uvt.info.dw.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import ro.uvt.info.dw.model.Author;

import java.util.UUID;

@Repository
public interface AuthorsRepository extends CassandraRepository<Author, UUID> {
}
