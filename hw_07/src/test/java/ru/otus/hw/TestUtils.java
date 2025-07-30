package ru.otus.hw;


import org.junit.jupiter.api.Assertions;
import ru.otus.models.Author;
import ru.otus.models.Book;
import ru.otus.models.Comment;
import ru.otus.models.Genre;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtils {
    public static void equalAuthor(Author a1, Author a2) {
        Assertions.assertEquals(a1.getId(), a2.getId(), "Идентификаторы авторов должны быть одинаковыми");
        Assertions.assertEquals(a1.getFullName(), a2.getFullName(), "Имена авторов должны быть одинаковыми");
    }

    public static void equalAuthors(List<Author> authorList1, List<Author> authorList2) {
        assertThat(authorList1).containsExactlyInAnyOrderElementsOf(authorList2);

        Map<Long, Author> authorMap2 = authorList2.stream().collect(Collectors.toMap(
                Author::getId, Function.identity()));

        for (Author author : authorList1) {
            Long idAuthor =  author.getId();
            equalAuthor(author, authorMap2.get(idAuthor));
        }
    }

    public static void equalGenre(Genre g1, Genre g2) {
        Assertions.assertEquals(g1.getId(), g2.getId(), "Идентификаторы жанров должны быть одинаковыми");
        Assertions.assertEquals(g1.getName(), g2.getName(), "Наименование жанров должны быть одинаковыми");
    }

    public static void equalGenres(List<Genre> genre1List, List<Genre> genre2List) {
        assertThat(genre1List).containsExactlyInAnyOrderElementsOf(genre2List);

        Map<Long, Genre> genreMap2 = genre2List.stream().collect(Collectors.toMap(
                Genre::getId, Function.identity()));

        for (Genre genre : genre1List) {
            Long idGenre =  genre.getId();
            equalGenre(genre, genreMap2.get(idGenre));
        }
    }

    public static void equalComment(Comment b1, Comment b2) {
        Assertions.assertEquals(b1.getId(), b2.getId(), "Идентификаторы Комментариев должны совпадать");
        Assertions.assertEquals(b1.getText(), b2.getText(), "Тексты комментариев должны совпадать");
        Assertions.assertEquals(b1.getBook(), b2.getBook(), "Идентификаторы книг должны совпадать");
    }

    public static void equalComments(List<Comment> comment1List, List<Comment> comment2List) {
        assertThat(comment1List).containsExactlyInAnyOrderElementsOf(comment2List);

        Map<Long, Comment> genreMap2 = comment2List.stream().collect(Collectors.toMap(
                Comment::getId, Function.identity()));

        for (Comment comment : comment1List) {
            Long idGenre =  comment.getId();
            equalComment(comment, genreMap2.get(idGenre));
        }
    }

    public static void equalBook(Book b1, Book b2) {
        Assertions.assertEquals(b1.getId(), b2.getId(), "Идентификаторы книг должны совпадать");
        Assertions.assertEquals(b1.getTitle(), b2.getTitle(), "Название книг должны совпадать");

        equalAuthor(b1.getAuthor(), b2.getAuthor());
        equalGenres(b1.getGenres(), b2.getGenres());
    }

    public static void equalBooks(List<Book> book1List, List<Book> book2List) {
        assertThat(book1List).containsExactlyInAnyOrderElementsOf(book2List);

        Map<Long, Book> bookMap2 = book2List.stream().collect(Collectors.toMap(
                Book::getId, Function.identity()));

        for (Book book : book1List) {
            Long idBook =  book.getId();
            equalBook(book, bookMap2.get(idBook));
        }
    }
}
