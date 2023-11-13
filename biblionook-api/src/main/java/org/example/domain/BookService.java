package org.example.domain;

import org.example.data.BookRepository;
import org.example.models.Book;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BookService {
    private final BookRepository repository;

    public BookService(BookRepository repository) { this.repository = repository; }

    public List<Book> findAll() { return repository.findAll(); }

    public Result<Book> create(Book book) {
        Result<Book> result = validate(book);

        if (!result.isSuccess()) {
            return result;
        }

        book = repository.create(book);
        result.setPayload(book);

        return result;
    }

    public Result<Book> update(Book book) {
        Result<Book> result = validate(book);

        if (!result.isSuccess()) {
            return result;
        }

        if (!repository.update(book)) {
            result.addMessage(ResultType.NOT_FOUND,
                    String.format("There is no book with ID #%s", book.getBookId()));
            return result;
        }

        result.setPayload(book);
        return result;
    }

    public boolean deleteById(int bookId) { return repository.deleteById(bookId); }


    private Result<Book> validate(Book book) {
        Result<Book> result = new Result<>();

        // isbn is required
        if (book.getIsbn() == null || book.getIsbn().isBlank()) {
            result.addMessage(ResultType.INVALID, "Isbn is required.");
        }

        // title is required
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            result.addMessage(ResultType.INVALID, "Title is required.");
        }

        // author is required
        if (book.getAuthor() == null || book.getAuthor().isBlank()) {
            result.addMessage(ResultType.INVALID, "Author is required.");
        }

        // image link is required
        if (book.getImageLink() == null || book.getImageLink().isBlank()) {
            result.addMessage(ResultType.INVALID, "Image link is required.");
        }

        // uniqueness check. user cannot save same book twice in database
        for (Book b : repository.findAll()) {
            if (b.getBookId() != book.getBookId() &&
                    Objects.equals(b.getIsbn(), book.getIsbn())) {
                result.addMessage(ResultType.INVALID, "You have already saved this book.");
            }
        }

        return result;
    }
}
