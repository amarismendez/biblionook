package org.example.data.mapper;

import org.example.models.Quote;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuoteMapper implements RowMapper<Quote> {
    @Override
    public Quote mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Quote quote = new Quote();

        quote.setQuoteId(resultSet.getInt("quoteId"));
        quote.setText(resultSet.getString("text"));
        quote.setPageNumber(resultSet.getInt("pageNumber"));
        quote.setLibraryItemId(resultSet.getInt("libraryItemId"));

        return quote;
    }
}
