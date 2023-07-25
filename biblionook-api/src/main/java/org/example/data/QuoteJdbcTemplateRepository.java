package org.example.data;

import org.example.data.mapper.QuoteMapper;
import org.example.models.Quote;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class QuoteJdbcTemplateRepository implements QuoteRepository {
    private final JdbcTemplate jdbcTemplate;

    public QuoteJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Quote> findAll() {
        final String sql = "select * from quote;";
        List<Quote> quotes = jdbcTemplate.query(sql, new QuoteMapper());

        return quotes;
    }

    @Override
    public List<Quote> findByLibraryItemId(int libraryItemId) {
        List<Quote> quotesForLibraryItem = findAll().stream().filter(q -> q.getLibraryItemId() == libraryItemId).toList();

        return quotesForLibraryItem;
    }

    @Override
    public Quote create(Quote quote) {
        final String sql = "insert into quote (`text`, pageNumber, libraryItemId) " +
                "values (?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, quote.getText());
            ps.setInt(2, quote.getPageNumber());
            ps.setInt(3, quote.getLibraryItemId());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        quote.setQuoteId(keyHolder.getKey().intValue());
        return quote;
    }

    @Override
    public boolean update(Quote quote) {
        final String sql = "update quote set "
                + "`text` = ?, "
                + "pageNumber = ? "
                + "where quoteId = ?;";

        return jdbcTemplate.update(sql,
                quote.getText(),
                quote.getPageNumber(),
                quote.getQuoteId()) > 0;
    }

    @Override
    public boolean deleteById(int quoteId) {
        return jdbcTemplate.update("delete from quote where quoteId = ?;", quoteId) > 0;
    }

}
