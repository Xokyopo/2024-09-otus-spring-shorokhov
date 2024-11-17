package ru.otus.hw.repositories;

import ru.otus.hw.repositories.entities.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository {
    List<Author> findAll();

    Optional<Author> findById(long id);
}
