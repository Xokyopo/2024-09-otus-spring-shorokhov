package ru.otus.hw.repositories;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.repositories.entities.Author;
import ru.otus.hw.repositories.entities.Book;
import ru.otus.hw.repositories.entities.Comment;
import ru.otus.hw.repositories.entities.Genre;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Import({JpaCommentRepositoryImpl.class})
class JpaCommentRepositoryImplTest {

    @Autowired
    private CommentRepository repository;


    @ParameterizedTest
    @MethodSource("createDbComments")
    void findById_ShouldReturnCorrectComment_WhenHaveOriginal(Comment expected) {
        Optional<Comment> actual = repository.findById(expected.getId());

        assertThat(actual)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(Book::getId), Book.class)
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getAllCommentedBooks")
    void findAllByBookId_ShouldReturnCorrectCommentsList_WhenHaveOriginalList(Book book) {
        List<Comment> expected = createDbComments().stream()
                .filter(comment -> book.equals(comment.getSource()))
                .toList();
        List<Comment> actual = repository.findAllByBookId(book.getId());

        assertThat(actual)
                .isNotNull()
                .usingRecursiveFieldByFieldElementComparator(
                        RecursiveComparisonConfiguration.builder()
                                .withIgnoredFields("id")
                                .withComparatorForType(Comparator.comparing(Book::getId), Book.class)
                                .build())
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void save_ShouldUpdateComment_WhenAddNewComment() {
        Comment unsavedComment = new Comment(1L, createDbBook(3), "Comment_13");

        assertThat(repository.findById(unsavedComment.getId()))
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(Book::getId), Book.class)
                .isNotEqualTo(unsavedComment);

        var updatedComment = repository.save(unsavedComment);
        assertThat(updatedComment)
                .isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .withComparatorForType(Comparator.comparing(Book::getId), Book.class)
                .isEqualTo(unsavedComment);

        assertThat(repository.findById(updatedComment.getId()))
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(Book::getId), Book.class)
                .isEqualTo(updatedComment);
    }

    @Test
    void deleteById_ShouldDeleteTargetComment() {
        assertThat(repository.findById(1L)).isPresent();
        repository.deleteById(1L);
        assertThat(repository.findById(1L)).isEmpty();
    }

    private static Book createDbBook(int id) {
        Author author = new Author(id, "Author_%s".formatted(id));
        Genre genre = new Genre(id, "Genre_%s".formatted(id));
        return new Book(id, "BookTitle_%s".formatted(id), author, genre);
    }

    private static Comment createDbComment(int id, int bookId) {
        return new Comment(id, createDbBook(bookId), "Comment_%s%s".formatted(id, bookId));
    }

    private static List<Comment> createDbComments() {
        return IntStream.range(1, 7).boxed()
                .map(commentId -> createDbComment(commentId, (int) commentId / 2 + commentId % 2))
                .toList();
    }

    private static List<Book> getAllCommentedBooks() {
        return createDbComments().stream()
                .map(Comment::getSource)
                .distinct()
                .toList();
    }
}
