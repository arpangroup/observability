package com.observability.__metrics_with_actuator_and_micrometer.service;

import com.observability.__metrics_with_actuator_and_micrometer.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultBooksService implements BookService {
    private final Logger log = LoggerFactory.getLogger(DefaultBooksService.class);

    private static final List<Book> BOOKS = List.of(
            new Book("8eacc5d3-5f01-48e0-9aae-52cd2388d10d", "Fundamental Algorithms"),
            new Book("ceedf23f-6199-4cd7-b108-6d0e92d7c8a3", "Domain Driven Design"),
            new Book("0f75c02a-b640-4304-9bc7-6548bc875fef", "Analysis Patterns")
    );

    @Override
    public List<Book> getAllBooks() {
        return BOOKS;
    }

    @Override
    public List<Book> findByTitle(String title) throws Exception{
        if (!StringUtils.hasText(title)) {
            return BOOKS;
        } else {
            return BOOKS.stream().filter(book -> book.title().toLowerCase().contains(title.toLowerCase())).collect(Collectors.toList());
        }
    }

    @Override
    public Long countBooks() {
        log.info("BooksRepository#countBooks counting books");
        return (long) BOOKS.size();
    }


}
