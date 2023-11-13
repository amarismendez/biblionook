package org.example.controllers;

import org.example.domain.BookService;
import org.example.domain.Result;
import org.example.models.Book;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService service;

    public BookController(BookService service) { this.service = service; }

    @GetMapping
    public List<Book> findAll() { return service.findAll(); }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Book book) {
        Result<Book> result = service.create(book);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<Object> update(@PathVariable int bookId, @RequestBody Book book) {
        if (bookId != book.getBookId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<Book> result = service.update(book);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Object> deleteById(@PathVariable int bookId) {
        if (service.deleteById(bookId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
