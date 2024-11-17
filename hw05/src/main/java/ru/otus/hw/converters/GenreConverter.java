package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.entities.Genre;

@Component
public class GenreConverter {
    public String genreToString(Genre genre) {
        return "Id: %d, Name: %s".formatted(genre.getId(), genre.getName());
    }
}
