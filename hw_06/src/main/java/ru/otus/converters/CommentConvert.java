package ru.otus.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.models.Book;
import ru.otus.models.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class CommentConvert {
    private final BookConverter bookConverter;


    public String commentToString(Comment comment) {
        var bookString = bookConverter.bookToString(comment.getBook());

        return "Id: %d, text: %s, book: {%s}".formatted(
                comment.getId(),
                comment.getText(),
                bookString);
    }

    public String commentsToString(List<Comment> comments) {
        Map<Book, List<Comment>> bookComments =  comments.stream()
                .collect(Collectors.groupingBy(Comment::getBook));
        List<String> commentsGroupByBook = new ArrayList<>();
        for (Book book : bookComments.keySet()) {
            var bookString = bookConverter.bookToString(book);
            var commentsString = bookComments.get(book).stream()
                    .map(c -> "{Id: %d, text: %s}".formatted(c.getId(), c.getText()))
                    .collect(Collectors.joining(", "));
            commentsGroupByBook.add("Book: {%s},\nComments:[%s]".formatted(bookString, commentsString));
        }

        return String.join("\n", commentsGroupByBook);
    }
}
