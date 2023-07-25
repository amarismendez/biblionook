package org.example.data;

import org.example.data.mapper.LibraryItemMapper;
import org.example.models.LibraryItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class LibraryItemJdbcTemplateRepository implements LibraryItemRepository {
    private final JdbcTemplate jdbcTemplate;

    public LibraryItemJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<LibraryItem> findAll() {
        final String sql = "select * from library_item;";
        List<LibraryItem> libraryItems = jdbcTemplate.query(sql, new LibraryItemMapper());

        return libraryItems;
    }

    @Override
    public List<LibraryItem> findByAppUserId(int appUserId) {
        List<LibraryItem> libraryItemsForUser = findAll().stream().filter(l -> l.getAppUserId() == appUserId).toList();

        return libraryItemsForUser;
    }

    @Override
    public LibraryItem create(LibraryItem libraryItem) {
        final String sql = "insert into library_item (isbn, rating, `description`, `status`, appUserId) " +
                "values (?, ?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, libraryItem.getIsbn());
            ps.setInt(2, libraryItem.getRating());
            ps.setString(3, libraryItem.getDescription());
            ps.setString(4, libraryItem.getStatus().toString());
            ps.setInt(5, libraryItem.getAppUserId());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        libraryItem.setLibraryItemId(keyHolder.getKey().intValue());
        return libraryItem;
    }

    @Override
    public boolean update(LibraryItem libraryItem) {
        final String sql = "update library_item set " // note: isbn cannot be updated.
                + "rating = ?, "
                + "`description` = ?, "
                + "`status` = ?, "
                + "appUserId = ? "
                + "where libraryItemId = ?;";

        return jdbcTemplate.update(sql,
                libraryItem.getRating(),
                libraryItem.getDescription(),
                libraryItem.getStatus().toString(),
                libraryItem.getAppUserId(),
                libraryItem.getLibraryItemId()) > 0;
    }

    @Override
    public boolean deleteById(int libraryItemId) {
        return jdbcTemplate.update("delete from library_item where libraryItemId = ?;", libraryItemId) > 0;
    }

}
