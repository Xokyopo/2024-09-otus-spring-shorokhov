package ru.otus.hw.services;

import ru.otus.hw.repositories.entities.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> findAll();
}
