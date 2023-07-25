package org.example.domain;

import org.example.data.QuoteJdbcTemplateRepository;
import org.example.models.Quote;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class QuoteServiceTest {
    @Autowired
    QuoteService service;

    @MockBean
    QuoteJdbcTemplateRepository repository;

    @Test
    void findByLibraryItemId() {
        List<Quote> existing = List.of(makeQuote());
        when(repository.findByLibraryItemId(1)).thenReturn(existing);

        List<Quote> quotes = service.findByLibraryItemId(1);

        assertEquals(existing, quotes);
        assertEquals(1, quotes.size());
    }

    @Test
    void shouldNotFindMissing() {
        when(repository.findByLibraryItemId(50)).thenReturn(new ArrayList<>());

        List<Quote> quotes = service.findByLibraryItemId(50);

        assertEquals(0, quotes.size());
    }

    @Test
    void shouldCreate() {
        Quote quote = makeQuote();
        when(repository.create(any())).thenReturn(quote);

        Result<Quote> result = service.create(quote);

        assertTrue(result.isSuccess());
        assertNotNull(result.getPayload());
    }

    @Test
    void shouldNotCreateInvalid() {
        Quote quote = makeInvalidQuote();
        Result<Quote> result = service.create(quote);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Text is required."));
        assertTrue(result.getMessages().contains("Library Item Id is required and cannot be 0."));
    }

    @Test
    void shouldNotCreateDuplicate() {
        Quote quote = makeQuote();
        quote.setQuoteId(15);
        when(repository.findByLibraryItemId(1)).thenReturn(List.of(quote));

        Result<Quote> result = service.create(makeQuote());

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("You have already saved this quote."));
    }

    @Test
    void shouldUpdate() {
        Quote quote = makeQuote();
        when(repository.update(quote)).thenReturn(true);

        Result<Quote> result = service.update(quote);

        assertEquals(ResultType.SUCCESS, result.getType());
        assertEquals(quote, result.getPayload());
    }

    @Test
    void shouldNotUpdateMissing() {
        Quote quote = makeQuote();
        quote.setQuoteId(50);
        when(repository.update(quote)).thenReturn(false);

        Result<Quote> result = service.update(quote);

        assertEquals(ResultType.NOT_FOUND, result.getType());
        assertNull(result.getPayload());
        assertTrue(result.getMessages().contains("There is no quote with ID #50"));
    }

    @Test
    void shouldNotUpdateInvalid() {
        Quote quote = makeInvalidQuote();
        Result<Quote> result = service.update(quote);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Text is required."));
        assertTrue(result.getMessages().contains("Library Item Id is required and cannot be 0."));
    }

    @Test
    void shouldNotUpdateDuplicate() {
        Quote quote = makeQuote();
        quote.setQuoteId(15);
        when(repository.findByLibraryItemId(1)).thenReturn(List.of(quote));

        Result<Quote> result = service.update(makeQuote());

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("You have already saved this quote."));
    }

    @Test
    void shouldDeleteById() {
        when(repository.deleteById(1)).thenReturn(true);
        assertTrue(service.deleteById(1));
    }

    @Test
    void shouldNotDeleteByMissingId() {
        when(repository.deleteById(50)).thenReturn(false);
        assertFalse(service.deleteById(50));
    }


    private Quote makeQuote() {
        Quote quote = new Quote();

        quote.setText("I am a deep quote...");
        quote.setPageNumber(165);
        quote.setLibraryItemId(1);

        return quote;
    }

    private Quote makeInvalidQuote() {
        Quote quote = new Quote();

        quote.setText("    ");
        quote.setPageNumber(0);
        quote.setLibraryItemId(0);

        return quote;
    }
}