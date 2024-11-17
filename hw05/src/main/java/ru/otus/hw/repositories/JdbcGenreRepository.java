package ru.otus.hw.repositories;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.hw.entities.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JdbcGenreRepository implements GenreRepository {

    private static final RowMapper<Genre> ROW_MAPPER = new GenreRowMapper();

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT id, name FROM genres", ROW_MAPPER);
    }

    @Override
    public Optional<Genre> findById(long id) {
        var queryParameters = Map.of("id", id);
        String queryString = "SELECT id, name FROM genres WHERE id = :id";

        List<Genre> genres = jdbcTemplate.query(queryString, queryParameters, ROW_MAPPER);

        return genres.stream().findAny();
    }

    private static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            return new Genre(rs.getLong("id"), rs.getString("name"));
        }
    }
}
