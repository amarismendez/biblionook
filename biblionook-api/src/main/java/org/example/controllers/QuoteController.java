package org.example.controllers;

import org.example.domain.QuoteService;
import org.example.domain.Result;
import org.example.models.Quote;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quotes")
public class QuoteController {
    private final QuoteService service;

    public QuoteController(QuoteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Quote> findAll() {
        return service.findAll();
    }

    @GetMapping("/{libraryItemId}")
    public List<Quote> findByLibraryItemId(@PathVariable int libraryItemId) {
        return service.findByLibraryItemId(libraryItemId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Quote quote) {
        Result<Quote> result = service.create(quote);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @PutMapping("/{quoteId}")
    public ResponseEntity<Object> update(@PathVariable int quoteId, @RequestBody Quote quote) {
        if (quoteId != quote.getQuoteId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<Quote> result = service.update(quote);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{quoteId}")
    public ResponseEntity<Object> deleteById(@PathVariable int quoteId) {
        if (service.deleteById(quoteId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
