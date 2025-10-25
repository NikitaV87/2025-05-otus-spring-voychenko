package ru.otus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.otus.domain.Book;
import ru.otus.dto.BookDto;

@Service
@RequiredArgsConstructor
public class BookTemplateOperations {
    private final ReactiveMongoTemplate template;

    public Flux<BookDto> findAll() {
        TypedAggregation<Book> aggregation = Aggregation.newAggregation(
                Book.class,
                Aggregation.lookup("author", "author", "_id", "author"),
                Aggregation.lookup("genre", "genres", "_id", "genres"),
                Aggregation.unwind("author", true),
                Aggregation.unwind("genre", true)
        );

        return template.aggregate(aggregation, BookDto.class);
    }
}
