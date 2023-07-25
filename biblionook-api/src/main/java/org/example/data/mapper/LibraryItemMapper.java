package org.example.data.mapper;

import org.example.models.BookStatus;
import org.example.models.LibraryItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LibraryItemMapper implements RowMapper<LibraryItem> {
    @Override
    public LibraryItem mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        LibraryItem libraryItem = new LibraryItem();

        libraryItem.setLibraryItemId(resultSet.getInt("libraryItemId"));
        libraryItem.setIsbn(resultSet.getString("isbn"));
        libraryItem.setRating(resultSet.getInt("rating"));
        libraryItem.setDescription(resultSet.getString("description"));
        libraryItem.setStatus(BookStatus.valueOf(resultSet.getString("status")));
        libraryItem.setAppUserId(resultSet.getInt("appUserId"));

        return libraryItem;
    }
}
