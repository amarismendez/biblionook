package org.example.data;

import org.example.models.Quote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuoteJdbcTemplateRepositoryTest {
    @Autowired
    private QuoteJdbcTemplateRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    static boolean hasSetup = false;

    @BeforeEach
    void setup() {
        if (!hasSetup) {
            hasSetup = true;
            jdbcTemplate.update("call set_known_good_state();");
        }
    }

    @Test
    void shouldFindAll() {
        List<Quote> result = repository.findAll();

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void shouldFindByLibraryItemId() {
        List<Quote> result = repository.findByLibraryItemId(1);

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void shouldNotFindByNonexistentLibraryItemId() {
        List<Quote> result = repository.findByLibraryItemId(50);

        assertEquals(0, result.size());
    }

    @Test
    void shouldCreate() {
        Quote quote = makeQuote();

        Quote result = repository.create(quote);

        assertNotNull(result);
        assertEquals(4, result.getQuoteId());
    }

    @Test
    void shouldUpdate() {
        Quote quote = makeQuote();
        quote = repository.create(quote); // attach quote id
        quote.setPageNumber(123);
        int id = quote.getQuoteId();

        assertTrue(repository.update(quote));
        assertTrue(quote.getPageNumber() == repository.findAll().stream().filter(q -> q.getQuoteId() == id).findFirst().orElse(null).getPageNumber());
    }

    @Test
    void shouldDeleteById() {
        Quote quote = repository.create(makeQuote());
        int startingSize = repository.findAll().size();

        assertTrue(repository.deleteById(quote.getQuoteId()));
        assertEquals(startingSize - 1, repository.findAll().size());
    }


    private Quote makeQuote() {
        Quote quote = new Quote();

        quote.setText("I am a deep quote...");
        quote.setPageNumber(165);
        quote.setLibraryItemId(1);

        return quote;
    }
}
