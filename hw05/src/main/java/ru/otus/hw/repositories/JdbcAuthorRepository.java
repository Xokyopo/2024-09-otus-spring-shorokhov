package ru.otus.hw.repositories;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Author> findAll() {
        return new ArrayList<>();
    }

    @Override
    public Optional<Author> findById(long id) {
        return Optional.empty();
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            return new Author(rs.getLong("id"), rs.getString("full_name"));
        }
    }
}
