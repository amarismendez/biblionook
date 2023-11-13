package org.example.data;

import org.example.models.Book;

import java.util.List;

public interface BookRepository {
    List<Book> findAll();

    Book create(Book book);

    boolean update(Book book);

    boolean deleteById(int bookId);
}
