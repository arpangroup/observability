package com.observability.__metrics_with_actuator_and_micrometer.service;

import com.observability.__metrics_with_actuator_and_micrometer.model.Book;

import java.util.List;

public interface BookService {
    List<Book> getAllBooks();

    /**
     * This method simulates a search feature over some repository (database/Elasticsearch/whatever) for a books matching given title.
     * Depending on a title, this thread will sleep for some predefined period of time.
     * For 'Fundamental Algorithms' sleeping time is the longest, so that in Grafana it should be visible that searching for this title
     * takes the most time.
     *
     * @param title book's title to search by
     * @return a list of books matching the given title
     */
    List<Book> findByTitle(String title) throws Exception;

    Long countBooks();
}
