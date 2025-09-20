package ru.otus.mapper;

import org.mapstruct.Mapper;
import ru.otus.dto.CommentDto;
import ru.otus.models.Comment;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface CommentMapper {
    CommentDto toDto(Comment comment);

    List<CommentDto> toDto(List<Comment> comment);
}
