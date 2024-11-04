package ru.otus.hw.repositories;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {

    private static final RowMapper<Author> ROW_MAPPER = new AuthorRowMapper();

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Author> findAll() {
        return jdbcTemplate.query("SELECT id, full_name FROM authors", ROW_MAPPER);
    }

    @Override
    public Optional<Author> findById(long id) {
        var queryParameters = Map.of("id", id);
        String queryString = "SELECT id, full_name FROM authors WHERE id = :id";

        Author author = jdbcTemplate.queryForObject(queryString, queryParameters, ROW_MAPPER);

        return author != null ? Optional.of(author) : Optional.empty();
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            return new Author(rs.getLong("id"), rs.getString("full_name"));
        }
    }
}
