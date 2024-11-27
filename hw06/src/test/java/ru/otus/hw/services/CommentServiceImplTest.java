package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.repositories.JpaBookRepositoryImpl;
import ru.otus.hw.repositories.JpaCommentRepositoryImpl;
import ru.otus.hw.repositories.entities.Book;
import ru.otus.hw.repositories.entities.Comment;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({CommentServiceImpl.class, JpaCommentRepositoryImpl.class, JpaBookRepositoryImpl.class})
class CommentServiceImplTest {

    private static final long FIRST_ELEMENT_OF_DB = 1L;

    @Autowired
    private CommentService service;

    @Autowired
    private TestEntityManager testEm;

    @Test
    void findById_ShouldNotThrowLazyInitializationException_WhenGetAccessToLoadedCommentField() {
        Comment loadedComment = service.findById(1).orElseGet(null);

        Assertions.assertNotNull(loadedComment);
        Assertions.assertDoesNotThrow(loadedComment::getId);
        Assertions.assertDoesNotThrow(loadedComment::getText);
        Assertions.assertDoesNotThrow(loadedComment::getSource);
        Book commentSource = loadedComment.getSource();
        Assertions.assertNotNull(commentSource);
        Assertions.assertDoesNotThrow(commentSource::getId);
        Assertions.assertDoesNotThrow(commentSource::getTitle);
        Assertions.assertDoesNotThrow(commentSource.getAuthor()::getId);
        Assertions.assertDoesNotThrow(commentSource.getAuthor()::getFullName);
        Assertions.assertDoesNotThrow(commentSource.getGenre()::getId);
        Assertions.assertDoesNotThrow(commentSource.getGenre()::getName);
    }

    @Test
    @Transactional
    void findById_ShouldLoadCommentFromDatabase() {
        Comment expected = testEm.find(Comment.class, FIRST_ELEMENT_OF_DB);
        Optional<Comment> actual = service.findById(FIRST_ELEMENT_OF_DB);

        assertThat(actual)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(Book::getId), Book.class)
                .isEqualTo(expected);
    }

    @Test
    void findAllByBookId_ShouldNotThrowLazyInitializationException_WhenGetAccessToLoadedCommentField() {
        Class<LazyInitializationException> expectedException = LazyInitializationException.class;
        List<Comment> loadedComments = service.findAllByBookId(FIRST_ELEMENT_OF_DB);

        Assertions.assertFalse(loadedComments.isEmpty());
        for (Comment loadedComment : loadedComments) {
            Assertions.assertNotNull(loadedComment);
            Assertions.assertDoesNotThrow(loadedComment::getId);
            Assertions.assertDoesNotThrow(loadedComment::getText);
            Assertions.assertDoesNotThrow(loadedComment::getSource);
            Book commentSource = loadedComment.getSource();
            Assertions.assertNotNull(commentSource);
            Assertions.assertDoesNotThrow(commentSource::getId);
            Assertions.assertDoesNotThrow(commentSource::getTitle);
            Assertions.assertDoesNotThrow(commentSource.getAuthor()::getId);
            Assertions.assertThrows(expectedException, commentSource.getAuthor()::getFullName);
            Assertions.assertDoesNotThrow(commentSource.getGenre()::getId);
            Assertions.assertThrows(expectedException, commentSource.getGenre()::getName);
        }
    }

    @Test
    @Transactional
    void findAllByBookId_ShouldLoadAllCommentFromDatabase() {
        String query = "FROM Comment WHERE source.id = :bookId";
        EntityManager em = testEm.getEntityManager();
        TypedQuery<Comment> typedQuery = em.createQuery(query, Comment.class);
        typedQuery.setParameter("bookId", FIRST_ELEMENT_OF_DB);

        List<Comment> expected = typedQuery.getResultList();
        List<Comment> actual = service.findAllByBookId(FIRST_ELEMENT_OF_DB);

        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator(
                        RecursiveComparisonConfiguration.builder()
                                .withComparatorForType(Comparator.comparing(Book::getId), Book.class)
                                .build()
                )
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void insert_ShouldNotThrowLazyInitializationException_WhenGetAccessToInsertedCommentField() {
        String unInsertCommentText = "fake Comment";
        long unInsertCommentSourceId = FIRST_ELEMENT_OF_DB;

        List<Comment> beforeInsertAllBook = new LinkedList<>(service.findAllByBookId(unInsertCommentSourceId));
        Assertions.assertFalse(beforeInsertAllBook.removeIf(comment -> unInsertCommentText.equals(comment.getText())));

        Comment insertedComment = service.insert(unInsertCommentText, unInsertCommentSourceId);

        Assertions.assertNotNull(insertedComment);
        Assertions.assertDoesNotThrow(insertedComment::getId);
        Assertions.assertDoesNotThrow(insertedComment::getText);
        Assertions.assertDoesNotThrow(insertedComment::getSource);
        Book commentSource = insertedComment.getSource();
        Assertions.assertNotNull(commentSource);
        Assertions.assertDoesNotThrow(commentSource::getId);
        Assertions.assertDoesNotThrow(commentSource::getTitle);
        Assertions.assertDoesNotThrow(commentSource.getAuthor()::getId);
        Assertions.assertDoesNotThrow(commentSource.getAuthor()::getFullName);
        Assertions.assertDoesNotThrow(commentSource.getGenre()::getId);
        Assertions.assertDoesNotThrow(commentSource.getGenre()::getName);
    }

    @Test
    @Transactional
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void insert_ShouldSaveBookFormDataBase() {
        String unInsertCommentText = "fake Comment";
        long unInsertCommentSourceId = FIRST_ELEMENT_OF_DB;

        List<Comment> beforeInsertAllBook = new LinkedList<>(service.findAllByBookId(unInsertCommentSourceId));
        Assertions.assertFalse(beforeInsertAllBook.removeIf(comment -> unInsertCommentText.equals(comment.getText())));

        Comment actual = service.insert(unInsertCommentText, unInsertCommentSourceId);
        Comment expected = testEm.find(Comment.class, actual.getId());

        assertThat(actual)
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(Book::getId), Book.class)
                .isEqualTo(expected);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void update_ShouldNotThrowLazyInitializationException_WhenGetAccessToUpdatedCommentField() {
        Book book = new Book(FIRST_ELEMENT_OF_DB, null, null, null);
        Comment unUpdatedComment = new Comment(FIRST_ELEMENT_OF_DB, book, "fake Comment_324234");
        Comment persistedComment = service.findById(unUpdatedComment.getId()).orElseGet(null);

        Assertions.assertNotNull(persistedComment);
        assertThat(unUpdatedComment)
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(Book::getId), Book.class)
                .isNotEqualTo(persistedComment);

        Comment updatedinsertedComment = service.update(
                unUpdatedComment.getId(),
                unUpdatedComment.getText()
        );
        Assertions.assertNotNull(updatedinsertedComment);
        Assertions.assertDoesNotThrow(updatedinsertedComment::getId);
        Assertions.assertDoesNotThrow(updatedinsertedComment::getText);
        Assertions.assertDoesNotThrow(updatedinsertedComment::getSource);
        Book commentSource = updatedinsertedComment.getSource();
        Assertions.assertNotNull(commentSource);
        Assertions.assertDoesNotThrow(commentSource::getId);
        Assertions.assertDoesNotThrow(commentSource::getTitle);
        Assertions.assertDoesNotThrow(commentSource.getAuthor()::getId);
        Assertions.assertDoesNotThrow(commentSource.getAuthor()::getFullName);
        Assertions.assertDoesNotThrow(commentSource.getGenre()::getId);
        Assertions.assertDoesNotThrow(commentSource.getGenre()::getName);
    }

    @Test
    @Transactional
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void update_ShouldSaveBookFormDataBase() {
        Book book = new Book(FIRST_ELEMENT_OF_DB, null, null, null);
        Comment unUpdatedComment = new Comment(FIRST_ELEMENT_OF_DB, book, "fake Comment_324234");
        Comment persistedComment = service.findById(unUpdatedComment.getId()).orElseGet(null);

        Assertions.assertNotNull(persistedComment);
        assertThat(unUpdatedComment)
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(Book::getId), Book.class)
                .isNotEqualTo(persistedComment);

        Comment actual = service.update(
                unUpdatedComment.getId(),
                unUpdatedComment.getText()
        );
        Comment expected = testEm.find(Comment.class, actual.getId());

        assertThat(actual)
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(Book::getId), Book.class)
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
