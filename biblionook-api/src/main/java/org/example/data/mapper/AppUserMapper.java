package org.example.data.mapper;

import org.example.models.AppUser;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppUserMapper implements RowMapper<AppUser> {
    private final List<String> roles;

    public AppUserMapper(List<String> roles) {
        this.roles = roles;
    }
    public AppUserMapper() {this.roles = new ArrayList<>();
    }

    @Override
    public AppUser mapRow(ResultSet rs, int i) throws SQLException {
        return new AppUser(
                rs.getInt("appUserId"),
                rs.getString("username"),
                rs.getString("passwordHash"),
                rs.getBoolean("enabled"),
                roles);
    }
}
