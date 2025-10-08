package ru.otus.mapper;

import org.mapstruct.Mapping;
import ru.otus.domain.Comment;
import ru.otus.dto.CommentCreateDto;
import ru.otus.dto.CommentDto;
import org.mapstruct.Mapper;
import ru.otus.dto.CommentUpdateDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface CommentMapper {

    CommentDto toDto(Comment comment);

    List<CommentDto> toDto(List<Comment> comment);

    @Mapping(target = "book.id", source = "bookId")
    Comment fromUpdDto(CommentUpdateDto commentUpdateDto);

    @Mapping(target = "book.id", source = "bookId")
    @Mapping(target = "id", ignore = true)
    Comment fromCreateDto(CommentCreateDto commentUpdateDto);
}
