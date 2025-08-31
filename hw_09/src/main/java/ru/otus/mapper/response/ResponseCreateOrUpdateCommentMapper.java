package ru.otus.mapper.response;

import ru.otus.dto.CommentDto;
import ru.otus.dto.response.ResponseCreateOrUpdateComment;

public class ResponseCreateOrUpdateCommentMapper {
    public static ResponseCreateOrUpdateComment toResponse(CommentDto comment) {
        return ResponseCreateOrUpdateComment.builder().id(comment.getId()).text(comment.getText())
                .book(comment.getBook().getId()).build();
    }
}
