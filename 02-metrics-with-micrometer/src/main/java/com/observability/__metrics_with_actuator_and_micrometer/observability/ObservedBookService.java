package com.observability.__metrics_with_actuator_and_micrometer.observability;

import com.observability.__metrics_with_actuator_and_micrometer.model.Book;
import com.observability.__metrics_with_actuator_and_micrometer.service.BookService;
import com.observability.__metrics_with_actuator_and_micrometer.service.DefaultBooksService;
import io.micrometer.core.instrument.*;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Low cardinality tags will be added to metrics and traces, while high cardinality tags will only be added to traces.
 *
 *
 * Application has only one endpoint GET /api/books which returns an array of books. Optionally, it is possible to filter those books by title.
 * For such API, these are the example metrics that we may want to monitor:
 *          - how many times this API has been called
 *          - how long does it take to return a list of books
 *          - how many books are “in stock”
 */
public class ObservedBookService implements BookService {
    private final DefaultBooksService delegate;
    private final ObservationRegistry observationRegistry;
    private final MeterRegistry meterRegistry;

    public ObservedBookService(DefaultBooksService delegate, ObservationRegistry observationRegistry, MeterRegistry meterRegistry) {
        this.delegate = delegate;
        this.observationRegistry = observationRegistry;
        this.meterRegistry = meterRegistry;

        // Method booksRepository::countBooks will be executed every 15s - Prometheus scrape_interval setting
        Gauge.builder("books_service_books_in_store_count", delegate::countBooks)
                .description("A current number of books in store")
                .register(this.meterRegistry);
    }

    //  metric api_books_get will tell us how many times endpoint /api/books has been called.
    //  We will use tags concept to group them by title (if it has been provided).
    @Override
    public List<Book> getAllBooks() {
        Counter.builder("api_books_get")
                .tag("title", "getAllBooks")
                .description("a number of requests to /api/books endpoint")
                .register(meterRegistry)
                .increment();
        return delegate.getAllBooks();
    }

    @Override
    public List<Book> findByTitle(String title) throws Exception{
        Tag titleTag = Tag.of("title", StringUtils.hasText(title) ? "all" : title);
        List<Book> books;
        Timer.Sample timer = Timer.start(this.meterRegistry);

        if (StringUtils.isEmpty(title)) {
            books = delegate.getAllBooks();
        } else {
            if ("Fundamental Algorithms".equalsIgnoreCase(title)) {
                Thread.sleep(ThreadLocalRandom.current().nextInt(200, 400));
            }
            books = delegate.findByTitle(title);
        }

        timer.stop(Timer.builder("service_books_find")
                .description("books searching timer")
                .tags(List.of(titleTag))
                .register(this.meterRegistry));
        return books;

       /* Observation.createNotStarted("make.tea", observationRegistry)
                .lowCardinalityKeyValue("name", "name")
                .lowCardinalityKeyValue("size", size)
                .observe(() -> delegate.makeTea());*/
    }

    @Override
    public Long countBooks() {
        /*Observation.createNotStarted(CREATED_DEPOSITS, observationRegistry)
                .lowCardinalityKeyValue("request-uid", deposit.getRequestUid())
                .observe(() -> log.debug("Counting created deposits"));*/
        return delegate.countBooks();
    }
}
