package ru.otus.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.converters.CommentConvert;
import ru.otus.models.Comment;
import ru.otus.services.CommentService;

import java.util.Optional;

@RequiredArgsConstructor
@ShellComponent
@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class CommentCommands {
    private final CommentService commentService;

    private final CommentConvert commentConvert;

    @ShellMethod(value = "Find comment by id", key = "cbid")
    public String findCommentById(long id) {
        Optional<Comment> comment = commentService.findById(id);
        if (comment.isEmpty()) {
            return "Comment with id %d not found".formatted(id);
        }

        return commentConvert.commentToString(comment.get());
    }

    @ShellMethod(value = "Find comments by book id", key = "cbbid")
    public String findCommentsByBookId(long id) {
        return commentConvert.commentsToString(commentService.findByBookIdWithBook(id));
    }

    @ShellMethod(value = "Insert comment to book", key = "cins")
    public String insertComment(String text, long bookId) {
        return commentConvert.commentToString(commentService.insert(text, bookId));
    }

    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateComment(long id, String text) {
        return commentConvert.commentToString(commentService.update(id, text));
    }

    @ShellMethod(value = "Delete comment by id", key = "cdel")
    public void deleteComment(long id) {
        commentService.deleteById(id);
    }
}
