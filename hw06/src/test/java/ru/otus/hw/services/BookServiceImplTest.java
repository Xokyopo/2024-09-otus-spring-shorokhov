package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.repositories.JpaAuthorRepositoryImpl;
import ru.otus.hw.repositories.JpaBookRepositoryImpl;
import ru.otus.hw.repositories.JpaGenreRepositoryImpl;
import ru.otus.hw.repositories.entities.Author;
import ru.otus.hw.repositories.entities.Book;
import ru.otus.hw.repositories.entities.Genre;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({BookServiceImpl.class, JpaAuthorRepositoryImpl.class, JpaBookRepositoryImpl.class, JpaGenreRepositoryImpl.class})
class BookServiceImplTest {

    private static final long FIRST_ELEMENT_OF_DB = 1L;

    @Autowired
    private BookService service;

    @Autowired
    private TestEntityManager testEm;

    @Test
    void findById_ShouldNotThrowLazyInitializationException_WhenGetAccessToLoadedBockField() {
        Book loadedBook = service.findById(1).orElseGet(null);

        Assertions.assertNotNull(loadedBook);
        Assertions.assertDoesNotThrow(loadedBook::getId);
        Assertions.assertDoesNotThrow(loadedBook::getTitle);
        Assertions.assertDoesNotThrow(loadedBook.getAuthor()::getId);
        Assertions.assertDoesNotThrow(loadedBook.getAuthor()::getFullName);
        Assertions.assertDoesNotThrow(loadedBook.getGenre()::getId);
        Assertions.assertDoesNotThrow(loadedBook.getGenre()::getName);
    }

    @Test
    @Transactional
    void findById_ShouldLoadBookFormDataBase() {
        Book expected = testEm.find(Book.class, FIRST_ELEMENT_OF_DB);
        Optional<Book> actual = service.findById(FIRST_ELEMENT_OF_DB);

        assertThat(actual)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(Author::getId), Author.class)
                .withComparatorForType(Comparator.comparing(Genre::getId), Genre.class)
                .isEqualTo(expected);

    }

    @Test
    void findAll_ShouldNotThrowLazyInitializationException_WhenGetAccessToLoadedBockField() {
        List<Book> loadedBooks = service.findAll();

        Assertions.assertFalse(loadedBooks.isEmpty());
        for (Book loadedBook : loadedBooks) {
            Assertions.assertNotNull(loadedBook);
            Assertions.assertDoesNotThrow(loadedBook::getId);
            Assertions.assertDoesNotThrow(loadedBook::getTitle);
            Assertions.assertDoesNotThrow(loadedBook.getAuthor()::getId);
            Assertions.assertDoesNotThrow(loadedBook.getAuthor()::getFullName);
            Assertions.assertDoesNotThrow(loadedBook.getGenre()::getId);
            Assertions.assertDoesNotThrow(loadedBook.getGenre()::getName);
        }
    }

    @Test
    @Transactional
    void findAll_ShouldLoadAllBookFormDataBase() {
        String query = "FROM Book";
        EntityManager em = testEm.getEntityManager();
        TypedQuery<Book> typedQuery = em.createQuery(query, Book.class);

        List<Book> expected = typedQuery.getResultList();
        List<Book> actual = service.findAll();

        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator(
                        RecursiveComparisonConfiguration.builder()
                                .withComparatorForType(Comparator.comparing(Author::getId), Author.class)
                                .withComparatorForType(Comparator.comparing(Genre::getId), Genre.class)
                                .build()
                )
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void insert_ShouldNotThrowLazyInitializationException_WhenGetAccessToInsertedBockField() {
        Book unInsertBook = new Book(0, "BookTitle_10500", new Author(1, "null"), new Genre(1, "null"));
        List<Book> beforeInsertAllBook = new LinkedList<>(service.findAll());
        Assertions.assertFalse(beforeInsertAllBook.removeIf(book -> unInsertBook.getTitle().equals(book.getTitle())));

        Book insertedBook = service.insert(unInsertBook.getTitle(), unInsertBook.getAuthor().getId(), unInsertBook.getGenre().getId());
        Assertions.assertNotNull(insertedBook);
        Assertions.assertNotEquals(0, insertedBook.getId());
        Assertions.assertDoesNotThrow(insertedBook::getId);
        Assertions.assertDoesNotThrow(insertedBook::getTitle);
        Assertions.assertDoesNotThrow(insertedBook.getAuthor()::getId);
        Assertions.assertDoesNotThrow(insertedBook.getAuthor()::getFullName);
        Assertions.assertDoesNotThrow(insertedBook.getGenre()::getId);
        Assertions.assertDoesNotThrow(insertedBook.getGenre()::getName);
    }

    @Test
    @Transactional
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void insert_ShouldSaveBookFormDataBase() {
        Book unInsertBook = new Book(0, "BookTitle_10500", new Author(1, "null"), new Genre(1, "null"));
        List<Book> beforeInsertAllBook = new LinkedList<>(service.findAll());
        Assertions.assertFalse(beforeInsertAllBook.removeIf(book -> unInsertBook.getTitle().equals(book.getTitle())));

        Book actual = service.insert(unInsertBook.getTitle(), unInsertBook.getAuthor().getId(), unInsertBook.getGenre().getId());
        Book expected = testEm.find(Book.class, actual.getId());

        assertThat(actual)
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(Author::getId), Author.class)
                .withComparatorForType(Comparator.comparing(Genre::getId), Genre.class)
                .isEqualTo(expected);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void update_ShouldNotThrowLazyInitializationException_WhenGetAccessToInsertedBockField() {
        Book unUpdatedBook = new Book(FIRST_ELEMENT_OF_DB, "BookTitle_10500", new Author(2, "null"), new Genre(3, "null"));
        Book persistedBook = service.findById(FIRST_ELEMENT_OF_DB).orElseGet(null);

        Assertions.assertNotNull(persistedBook);
        assertThat(unUpdatedBook)
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(Author::getId), Author.class)
                .withComparatorForType(Comparator.comparing(Genre::getId), Genre.class)
                .isNotEqualTo(persistedBook);

        Book updatedBook = service.update(
                unUpdatedBook.getId(),
                unUpdatedBook.getTitle(),
                unUpdatedBook.getAuthor().getId(),
                unUpdatedBook.getGenre().getId()
        );
        Assertions.assertNotNull(updatedBook);
        Assertions.assertDoesNotThrow(updatedBook::getId);
        Assertions.assertDoesNotThrow(updatedBook::getTitle);
        Assertions.assertDoesNotThrow(updatedBook.getAuthor()::getId);
        Assertions.assertDoesNotThrow(updatedBook.getAuthor()::getFullName);
        Assertions.assertDoesNotThrow(updatedBook.getGenre()::getId);
        Assertions.assertDoesNotThrow(updatedBook.getGenre()::getName);
    }

    @Test
    @Transactional
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void update_ShouldSaveBookFormDataBase() {
        Book unUpdatedBook = new Book(FIRST_ELEMENT_OF_DB, "BookTitle_10500", new Author(2, "null"), new Genre(3, "null"));
        Book persistedBook = service.findById(FIRST_ELEMENT_OF_DB).orElseGet(null);

        Assertions.assertNotNull(persistedBook);
        assertThat(unUpdatedBook)
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(Author::getId), Author.class)
                .withComparatorForType(Comparator.comparing(Genre::getId), Genre.class)
                .isNotEqualTo(persistedBook);

        Book updatedBook = service.update(
                unUpdatedBook.getId(),
                unUpdatedBook.getTitle(),
                unUpdatedBook.getAuthor().getId(),
                unUpdatedBook.getGenre().getId()
        );
        Book expected = testEm.find(Book.class, updatedBook.getId());

        assertThat(updatedBook)
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(Author::getId), Author.class)
                .withComparatorForType(Comparator.comparing(Genre::getId), Genre.class)
                .isEqualTo(expected);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteById() {
        assertThat(service.findById(FIRST_ELEMENT_OF_DB)).isPresent();
        service.deleteById(FIRST_ELEMENT_OF_DB);
        assertThat(service.findById(FIRST_ELEMENT_OF_DB)).isEmpty();
    }
}
