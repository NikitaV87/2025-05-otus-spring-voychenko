package ru.otus.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.models.Author;
import ru.otus.models.Book;
import ru.otus.models.Comment;
import ru.otus.models.Genre;
import ru.otus.repositories.AuthorRepository;
import ru.otus.repositories.BookRepository;
import ru.otus.repositories.CommentRepository;
import ru.otus.repositories.GenreRepository;

import java.util.List;

@ChangeLog
public class DatabaseChangelog {
    @ChangeSet(order = "001", id = "dropDb", author = "nvvoychenko", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertAuthors", author = "nvvoychenko")
    public void insertAuthors(AuthorRepository repository) {
        repository.save(new Author("1", "Author_1"));
        repository.save(new Author("2", "Author_2"));
        repository.save(new Author("3", "Author_3"));
    }

    @ChangeSet(order = "003", id = "insertGenres", author = "nvvoychenko")
    public void insertGenres(GenreRepository repository) {
        repository.save(new Genre("1", "Genre_1"));
        repository.save(new Genre("2", "Genre_2"));
        repository.save(new Genre("3", "Genre_3"));
        repository.save(new Genre("4", "Genre_4"));
        repository.save(new Genre("5", "Genre_5"));
        repository.save(new Genre("6", "Genre_6"));
    }

    @ChangeSet(order = "004", id = "insertBooks", author = "nvvoychenko")
    public void insertBooks(BookRepository repository) {
        repository.save(Book.builder()
                .id("1")
                .title("BookTitle_1")
                .author(Author.builder().id("1").build())
                .genres(List.of(Genre.builder().id("1").build(), Genre.builder().id("2").build()))
                .build()
        );
        repository.save(Book.builder()
                .id("2")
                .title("BookTitle_2")
                .author(Author.builder().id("2").build())
                .genres(List.of(Genre.builder().id("3").build(), Genre.builder().id("4").build()))
                .build()
        );
        repository.save(Book.builder()
                .id("3")
                .title("BookTitle_3")
                .author(Author.builder().id("3").build())
                .genres(List.of(Genre.builder().id("5").build(), Genre.builder().id("6").build()))
                .build()
        );
    }

    @ChangeSet(order = "005", id = "insertComment", author = "nvvoychenko")
    public void insertComment(CommentRepository repository) {
        repository.save(Comment.builder().id("1").book(Book.builder().id("1").build()).text("text_1").build());
        repository.save(Comment.builder().id("2").book(Book.builder().id("1").build()).text("text_2").build());
        repository.save(Comment.builder().id("3").book(Book.builder().id("1").build()).text("text_3").build());
        repository.save(Comment.builder().id("4").book(Book.builder().id("2").build()).text("text_4").build());
    }
}
