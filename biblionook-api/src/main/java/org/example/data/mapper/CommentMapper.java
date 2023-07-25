package org.example.data.mapper;

import org.example.models.Comment;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper implements RowMapper<Comment> {

    @Override
    public Comment mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Comment comment = new Comment();

        comment.setCommentId(resultSet.getInt("commentId"));
        comment.setText(resultSet.getString("text"));
        comment.setLikes(resultSet.getInt("likes"));

        String stringOfLikeIds = resultSet.getString("likeIds");
        if(!stringOfLikeIds.isEmpty()){
            String[] separatedLikeIds = stringOfLikeIds.split(",");
            List<Integer> listOfLikeIds = Arrays.stream(separatedLikeIds).map((m) -> Integer.parseInt(m)).collect(Collectors.toList());
            comment.setLikeIds(listOfLikeIds);
        } else {
            comment.setLikeIds(new ArrayList<>());
        }

        comment.setCreatedAt(resultSet.getDate("createdAt").toLocalDate());
        comment.setParentCommentId(resultSet.getInt("parentCommentId"));
        comment.setIsbn(resultSet.getString("isbn"));
        comment.setAppUserId(resultSet.getInt("appUserId"));

        return comment;
    }
}
