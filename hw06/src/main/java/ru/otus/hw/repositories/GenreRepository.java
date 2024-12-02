package ru.otus.hw.repositories;

import ru.otus.hw.repositories.entities.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {
    List<Genre> findAll();

    Optional<Genre> findById(long id);
}
