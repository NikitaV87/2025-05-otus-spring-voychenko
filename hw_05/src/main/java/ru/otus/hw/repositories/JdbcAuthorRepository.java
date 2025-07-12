package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Author> findAll() {
        return namedParameterJdbcTemplate.query("select a.id, a.full_name from authors a", new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        List<Author> resultQuery = namedParameterJdbcTemplate.query("select a.id, a.full_name from authors a " +
                "where a.id = :id", params, new AuthorRowMapper());

        Optional<Author> result;

        if (resultQuery.size() == 1) {
            result = Optional.of(resultQuery.get(0));
        } else {
            result = Optional.empty();
        }

        return result;
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("id");
            String fullName = rs.getString("full_name");

            return new Author(id, fullName);
        }
    }
}
