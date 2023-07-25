package org.example.domain;

import org.example.data.QuoteRepository;
import org.example.models.Quote;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuoteService {
    private final QuoteRepository repository;

    public QuoteService(QuoteRepository repository) {
        this.repository = repository;
    }

    public List<Quote> findAll() {
        return repository.findAll();
    }

    public List<Quote> findByLibraryItemId(int libraryItemId) {
        return repository.findByLibraryItemId(libraryItemId);
    }

    public Result<Quote> create(Quote quote) {
        Result<Quote> result = validate(quote);

        if (!result.isSuccess()) {
            return result;
        }

        quote = repository.create(quote);
        result.setPayload(quote);

        return result;
    }

    public Result<Quote> update(Quote quote) {
        Result<Quote> result = validate(quote);

        if (!result.isSuccess()) {
            return result;
        }

        if (!repository.update(quote)) {
            result.addMessage(ResultType.NOT_FOUND,
                    String.format("There is no quote with ID #%s", quote.getQuoteId()));
            return result;
        }

        result.setPayload(quote);
        return result;
    }

    public boolean deleteById(int quoteId) {
        return repository.deleteById(quoteId);
    }


    private Result<Quote> validate(Quote quote) {
        Result<Quote> result = new Result<>();

        // text is required
        if (quote.getText() == null || quote.getText().isBlank()) {
            result.addMessage(ResultType.INVALID, "Text is required.");
        }

        // page # is not required, & will default to 0.

        // libraryItemId is required. cannot be 0
        if (quote.getLibraryItemId() == 0) {
            result.addMessage(ResultType.INVALID, "Library Item Id is required and cannot be 0.");
        }

        // uniqueness check. user cannot save same quote twice
        for (Quote q : repository.findByLibraryItemId(quote.getLibraryItemId())) {
            if (q.getQuoteId() != quote.getQuoteId() &&
                    q.getText().equalsIgnoreCase(quote.getText())) {
                result.addMessage(ResultType.INVALID, "You have already saved this quote.");
            }
        }

        return result;
    }
}
