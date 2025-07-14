package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.ArrayList;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        Book book = namedParameterJdbcTemplate.query("""
                  select b.id as book_id, b.title as book_title, b.author_id, a.full_name as author_full_name, 
                  bg.genre_id, g.name as genre_name\s
                  from\s
                    books b\s
                    left join books_genres bg on b.id = bg.book_id\s
                    left join genres g on g.id = bg.genre_id
                    left join authors a on a.id = b.author_id\s
                  where\s
                    b.id = :id""", params, new BookResultSetExtractor());

        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
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
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        namedParameterJdbcTemplate.update("delete from books b where b.id = :id", params);
    }

    private List<Book> getAllBooksWithoutGenres() {
        List<Book> result = namedParameterJdbcTemplate.query("""
                select b.id as book_id, b.title as book_title, b.author_id, a.full_name as author_full_name\s
                from\s
                    books b\s
                    left join authors a on a.id = b.author_id
                """, new BookRowMapper());
        return result;

    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return namedParameterJdbcTemplate.query("select bg.book_id, bg.genre_id " +
                        "from books_genres bg", new BookGenreRelationRowMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Genre> genreCash = genres.stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity()));
        Map<Long, Book> bookCash = booksWithoutGenres.stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));
        Map<Long, Set<Long>> groupBookGenres = relations.stream()
                .collect(Collectors.groupingBy(BookGenreRelation::bookId,
                        Collectors.mapping(BookGenreRelation::genreId, Collectors.toSet())));

        groupBookGenres.forEach((bookId, genreSet) -> {
            List<Genre> listGenre = genreSet.stream().map(genreCash::get).toList();

            bookCash.get(bookId).setGenres(listGenre);
        });
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());

        namedParameterJdbcTemplate.update("insert into books(title, author_id) values(:title, :author_id)",
                params, keyHolder);

        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", book.getId());
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());

        int isUpdate = namedParameterJdbcTemplate.update("""
                                                update books b set\s
                                                  b.title = :title,\s
                                                  b.author_id = :author_id\s
                                                where\s
                                                  b.id = :id\s
                                              """, params);

        if (isUpdate == 0) {
            throw new EntityNotFoundException("Errors on update: data not found");
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        List<Long> genreIdList = book.getGenres().stream().map(Genre::getId).toList();
        MapSqlParameterSource[] params = new MapSqlParameterSource[genreIdList.size()];

        for (int i = 0; i < genreIdList.size(); i++) {
            params[i] = new MapSqlParameterSource()
                    .addValue("book_id", book.getId())
                    .addValue("genre_id", genreIdList.get(i));
        }

        namedParameterJdbcTemplate.batchUpdate(
                "insert into books_genres (book_id, genre_id) values (:book_id, :genre_id)",
                params
        );
    }

    private void removeGenresRelationsFor(Book book) {
        SqlParameterSource params = new MapSqlParameterSource("book_id", book.getId());

        namedParameterJdbcTemplate.update("delete from books_genres bg where bg.book_id = :book_id", params);
    }

    private static class BookGenreRelationRowMapper implements RowMapper<BookGenreRelation> {
        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id"));
        }
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long idBook = rs.getLong("book_id");
            Optional<String> titleBook = Optional.ofNullable(rs.getString("book_title"));

            long idAuthor = rs.getLong("author_id");
            Optional<String> fullNameAutor = Optional.ofNullable(rs.getString("author_full_name"));

            Author author = new Author(
                    idAuthor,
                    fullNameAutor.orElse("")
            );

            return new Book(
                    idBook,
                    titleBook.orElse(""),
                    author,
                    Collections.emptyList()
            );
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<Genre> genreList = new ArrayList<>();
            Optional<Long> idAuthor = Optional.empty();
            Optional<String> fullNameAuthor = Optional.empty();
            Optional<Long> idBook = Optional.empty();
            Optional<String> titleBook = Optional.empty();
            Author author = null;

            while (rs.next()) {
                idBook = Optional.of(rs.getObject("book_id", Long.class));
                titleBook = Optional.ofNullable(rs.getString("book_title"));
                idAuthor = Optional.ofNullable(rs.getObject("author_id", Long.class));
                fullNameAuthor = Optional.ofNullable(rs.getString("author_full_name"));
                Optional<Long> idGenre = Optional.ofNullable(rs.getObject("genre_id", Long.class));
                Optional<String> nameGenre = Optional.ofNullable(rs.getString("genre_name"));
                idGenre.ifPresent(aLong -> genreList.add(new Genre(aLong, nameGenre.orElse(""))));
            }
            if (idBook.isEmpty()) {
                return null;
            }
            if (idAuthor.isPresent()) {
                author = new Author(idAuthor.get(), fullNameAuthor.orElse(""));
            }
            return new Book(idBook.get(), titleBook.orElse(""), author,  genreList);
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {

    }
}
