package com.observability.__metrics_with_actuator_and_micrometer;

import com.observability.__metrics_with_actuator_and_micrometer.model.Book;
import com.observability.__metrics_with_actuator_and_micrometer.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getBooks(@RequestParam(required = false) String title) throws Exception {
        if (StringUtils.hasText(title)) return bookService.findByTitle(title);
        return bookService.getAllBooks();
    }


}
