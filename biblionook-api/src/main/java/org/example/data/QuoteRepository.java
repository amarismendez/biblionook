package org.example.data;

import org.example.models.Quote;

import java.util.List;

public interface QuoteRepository {
    List<Quote> findAll();

    List<Quote> findByLibraryItemId(int libraryItemId);

    Quote create(Quote quote);

    boolean update(Quote quote);

    boolean deleteById(int quoteId);
}
