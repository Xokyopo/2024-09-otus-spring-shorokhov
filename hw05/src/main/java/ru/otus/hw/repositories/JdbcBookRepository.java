package ru.otus.hw.repositories;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private static final RowMapper<Book> ROW_MAPPER = new BookRowMapper();

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<Book> findById(long id) {
        var queryParameters = Map.of("id", id);
        String queryString = """
                SELECT
                    books.id,
                    books.title,
                    books.author_id,
                    books.genre_id,
                    authors.full_name as `authors_full_name`,
                    genres.name as `genres_name`
                FROM books
                    LEFT JOIN authors on authors.id = books.author_id
                    LEFT JOIN genres on genres.id = books.genre_id
                WHERE books.id = :id
                """.replaceAll("\\s+", " ");

        Book book = jdbcTemplate.queryForObject(queryString, queryParameters, ROW_MAPPER);

        return book != null ? Optional.of(book) : Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        String queryString = """
                SELECT
                    books.id,
                    books.title,
                    books.author_id,
                    books.genre_id,
                    authors.full_name as `authors_full_name`,
                    genres.name as `genres_name`
                FROM books
                    LEFT JOIN authors on authors.id = books.author_id
                    LEFT JOIN genres on genres.id = books.genre_id
                """.replaceAll("\\s+", " ");

        return jdbcTemplate.query(queryString, ROW_MAPPER);
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        var queryParameters = Map.of("id", id);

        jdbcTemplate.update("DELETE FROM books WHERE id = :id", queryParameters);
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        //...

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        return book;
    }

    private Book update(Book book) {
        //...
        // Выбросить EntityNotFoundException если не обновлено ни одной записи в БД
        return book;
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Author author = rs.getLong("author_id") > 0
                    ? new Author(rs.getLong("author_id"), rs.getString("authors_full_name"))
                    : null;
            Genre genre = rs.getLong("genre_id") > 0
                    ? new Genre(rs.getLong("genre_id"), rs.getString("genres_name"))
                    : null;
            return new Book(rs.getLong("id"), rs.getString("title"), author, genre);
        }
    }
}
