package org.example.data;

import org.example.data.mapper.BookMapper;
import org.example.models.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class BookJdbcTemplateRepository implements BookRepository {
    private final JdbcTemplate jdbcTemplate;

    public BookJdbcTemplateRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    @Override
    public List<Book> findAll() {
        final String sql = "select * from book;";
        List<Book> books = jdbcTemplate.query(sql, new BookMapper());

        return books;
    }

    @Override
    public Book create(Book book) {
        final String sql = "insert into book (isbn, title, author, imageLink) " +
                "values (?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getImageLink());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        book.setBookId(keyHolder.getKey().intValue());
        return book;
    }

    @Override
    public boolean update(Book book) {
        final String sql = "update book set "
                + "isbn = ?, "
                + "title = ?, "
                + "author = ?, "
                + "imageLink = ? "
                + "where bookId = ?;";

        return jdbcTemplate.update(sql,
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getImageLink(),
                book.getBookId()) > 0;
    }

    @Override
    public boolean deleteById(int bookId) {
        return jdbcTemplate.update("delete from book where bookId = ?;", bookId) > 0;
    }

}
