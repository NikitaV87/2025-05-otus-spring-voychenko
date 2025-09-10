package ru.otus.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.dto.CommentCreateDto;
import ru.otus.dto.CommentDto;
import ru.otus.dto.CommentUpdateDto;
import ru.otus.services.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentService commentService;

    @GetMapping("/api/comment/{id}")
    public CommentDto getCommentsById(@PathVariable Long id) {
        return commentService.findById(id);
    }

    @GetMapping("/api/comment/book/{id}")
    public List<CommentDto> getCommentsByBookId(@PathVariable Long id) {
        return commentService.findByBookId(id);
    }

    @PatchMapping("/api/comment")
    public CommentDto patchComment(@Valid @RequestBody CommentUpdateDto commentUpdateDto) {
        return commentService.update(commentUpdateDto);
    }

    @PostMapping("/api/comment")
    public CommentDto postComment(@Valid @RequestBody CommentCreateDto commentCreateDto) {
        return commentService.insert(commentCreateDto);
    }

    @DeleteMapping("/api/comment/{id}")
    public void deleteCommentById(@PathVariable Long id) {
        commentService.deleteById(id);
    }
}
