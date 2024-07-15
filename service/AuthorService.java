package ro.uvt.info.dw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.uvt.info.dw.model.Author;
import ro.uvt.info.dw.repository.AuthorsRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorService {

    @Autowired
    private AuthorsRepository authorsRepository;

    public Author createAuthor(Author author) {
        author.setId(UUID.randomUUID());
        return authorsRepository.save(author);
    }

    public Optional<Author> getAuthorById(UUID id) {
        return authorsRepository.findById(id);
    }

    public List<Author> getAllAuthors() {
        return authorsRepository.findAll();
    }

    public void deleteAuthor(UUID id) {
        authorsRepository.deleteById(id);
    }
}
