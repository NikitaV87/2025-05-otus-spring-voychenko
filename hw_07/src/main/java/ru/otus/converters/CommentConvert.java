package ru.otus.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.models.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class CommentConvert {
    public String commentToString(Comment comment) {
        return "Id: %d, text: %s, book id: %d".formatted(
                comment.getId(),
                comment.getText(),
                comment.getBook().getId());
    }

    public String commentsToString(List<Comment> comments) {
        Map<Long, List<Comment>> bookComments =  comments.stream()
                .collect(Collectors.groupingBy(c -> c.getBook().getId()));
        List<String> commentsGroupByBook = new ArrayList<>();
        for (Long bookId : bookComments.keySet()) {
               var commentsString = bookComments.get(bookId).stream()
                    .map(c -> "{Id: %d, text: %s}".formatted(c.getId(), c.getText()))
                    .collect(Collectors.joining(", "));
            commentsGroupByBook.add("Book id: %s, Comments: [%s]".formatted(Long.toString(bookId), commentsString));
        }

        return String.join("\n", commentsGroupByBook);
    }
}
