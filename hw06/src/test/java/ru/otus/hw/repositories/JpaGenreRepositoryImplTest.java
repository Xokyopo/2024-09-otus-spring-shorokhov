package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.repositories.entities.Genre;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaGenreRepositoryImpl.class})
class JpaGenreRepositoryImplTest {

    @Autowired
    private GenreRepository repository;

    @MethodSource("getDbGenres")
    @ParameterizedTest
    void findById_ShouldReturnCorrectGenre_WhenHaveOriginal(Genre expected) {
        Optional<Genre> actual = repository.findById(expected.getId());

        assertThat(actual)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void findAll_ShouldReturnCorrectGenresList_WhenHaveOriginalList() {
        List<Genre> expected = getDbGenres();
        List<Genre> actual = repository.findAll();

        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }
}
