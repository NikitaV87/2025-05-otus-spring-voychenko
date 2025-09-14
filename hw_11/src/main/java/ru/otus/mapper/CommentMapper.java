package ru.otus.mapper;

import ru.otus.domain.Comment;
import ru.otus.dto.CommentDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface CommentMapper {
    CommentDto toDto(Comment comment);

    List<CommentDto> toDto(List<Comment> comment);
}
