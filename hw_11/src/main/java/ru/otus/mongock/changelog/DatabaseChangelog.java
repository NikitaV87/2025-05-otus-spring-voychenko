package ru.otus.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.domain.Genre;

import java.util.ArrayList;
import java.util.List;

@ChangeLog
public class DatabaseChangelog {
    @ChangeSet(order = "001", id = "dropDb", author = "nvvoychenko", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertAuthors", author = "nvvoychenko")
    public void insertAuthors(MongockTemplate template) {
        List<Author> authors = new ArrayList<>();

        authors.add(new Author("1", "Author_1"));
        authors.add(new Author("2", "Author_2"));
        authors.add(new Author("3", "Author_3"));

        template.insertAll(authors);
    }

    @ChangeSet(order = "003", id = "insertGenres", author = "nvvoychenko")
    public void insertGenres(MongockTemplate template) {
        List<Genre> genres = new ArrayList<>();

        genres.add(new Genre("1", "Genre_1"));
        genres.add(new Genre("2", "Genre_2"));
        genres.add(new Genre("3", "Genre_3"));
        genres.add(new Genre("4", "Genre_4"));
        genres.add(new Genre("5", "Genre_5"));
        genres.add(new Genre("6", "Genre_6"));

        template.insertAll(genres);
    }

    @ChangeSet(order = "004", id = "insertBooks", author = "nvvoychenko")
    public void insertBooks(MongockTemplate template) {
        List<Book> books = new ArrayList<>();

        books.add(Book.builder().id("1").title("BookTitle_1").author(Author.builder().id("2").build()).genres(
                List.of(Genre.builder().id("1").build(), Genre.builder().id("2").build())).build()
        );
        books.add(Book.builder().id("2").title("BookTitle_2").author(Author.builder().id("2").build()).genres(
                List.of(Genre.builder().id("3").build(), Genre.builder().id("4").build())).build()
        );
        books.add(Book.builder()
                .id("3").title("BookTitle_3").author(Author.builder().id("3").build()).genres(
                        List.of(Genre.builder().id("5").build(), Genre.builder().id("6").build())).build()
        );

        template.insertAll(books);
    }

    @ChangeSet(order = "005", id = "insertComment", author = "nvvoychenko")
    public void insertComment(MongockTemplate template) {
        List<Comment> comments = new ArrayList<>();

        comments.add(Comment.builder().id("1").book(Book.builder().id("1").build()).text("text_1").build());
        comments.add(Comment.builder().id("2").book(Book.builder().id("1").build()).text("text_2").build());
        comments.add(Comment.builder().id("3").book(Book.builder().id("1").build()).text("text_3").build());
        comments.add(Comment.builder().id("4").book(Book.builder().id("2").build()).text("text_4").build());

        template.insertAll(comments);
    }
}
