package org.example.data;

import org.example.data.mapper.CommentMapper;
import org.example.models.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CommentJdbcTemplateRepository implements CommentRepository {
    private final JdbcTemplate jdbcTemplate;
    public CommentJdbcTemplateRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    @Override
    public List<Comment> findAll() {
        List<Comment> comments = findAllIncludingChildren();

        List<Comment> mainComments = comments.stream()
                .filter(c -> c.getParentCommentId() == 0)
                .collect(Collectors.toList());

        return mainComments;
    }

    @Override
    public List<Comment> findByIsbn(String isbn) {
        List<Comment> commentsByIsbn = findAll();
        return commentsByIsbn.stream()
                .filter(c -> c.getIsbn().equalsIgnoreCase(isbn))
                .collect(Collectors.toList());
    }

    @Override
    public Comment create(Comment comment) {
        final String sql = "insert into comment (`text`, createdAt, parentCommentId, isbn, appUserId) " +
                "values (?, ?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, comment.getText());
            ps.setDate(2, Date.valueOf(comment.getCreatedAt()));
            if (comment.getParentCommentId() != 0) { // TODO: NOTE - this is how you set an int as null in the db. useful for rating (lib item) and page # (quote)
                ps.setInt(3, comment.getParentCommentId());
            } else {
                ps.setString(3, null);
            }
            ps.setString(4, comment.getIsbn());
            ps.setInt(5, comment.getAppUserId());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        comment.setCommentId(keyHolder.getKey().intValue());
        return comment;
    }

    @Override
    public boolean update(Comment comment) {
        final String sql = "update comment set "
                + "`text` = ?, "
                + "likes = ?, "
                + "likeIds = ?, "
                + "createdAt = ?, "
                + "appUserId = ? "
                + "where commentId = ?;";

        String delimitedLikeIds = null;

        if(comment.getLikeIds() != null){
            delimitedLikeIds = comment.getLikeIds().stream()
                    .map(String::valueOf).collect(Collectors.joining(","));
        }

        return jdbcTemplate.update(sql,
                comment.getText(),
                comment.getLikes(),
                delimitedLikeIds,
                comment.getCreatedAt(),
                comment.getAppUserId(),
                comment.getCommentId()) > 0;
    }

    @Override
    @Transactional
    public boolean deleteById(int commentId) {
        // delete any child comments first
        Comment originalComment = findAllIncludingChildren().stream().filter(c -> c.getCommentId() == commentId).findFirst().orElse(null);
        if (originalComment == null) {
            return false;
        }

        List<Comment> allCommentsToDelete = new ArrayList<>();
        List<Comment> listToFindChildrenFor = originalComment.getChildComments();
        allCommentsToDelete.addAll(listToFindChildrenFor);
        boolean reachedLowestLevel = false;

        do {
            // start this iteration with an empty list of the current level children
            List<Comment> currentLevelChildren = new ArrayList<>();

            // assign the current level of children
            for (Comment c : listToFindChildrenFor) {
                currentLevelChildren.addAll(c.getChildComments());
            }

            if (currentLevelChildren.isEmpty()) {
                // we stop the loop if there are no children at this level
                reachedLowestLevel = true;

            } else {
                // if there are children at this level, add them to all comments to delete list
                allCommentsToDelete.addAll(currentLevelChildren);
                // the list to find children for in the next loop will be the current children at this level
                listToFindChildrenFor = currentLevelChildren;
            }
            
        } while (!reachedLowestLevel);

        // now do the actual repo delete for all children and grandchildren
        if (allCommentsToDelete.size() > 0) {
            List<Integer> idsToDelete = allCommentsToDelete.stream().map(Comment::getCommentId).collect(Collectors.toList());
            Collections.reverse(idsToDelete);
            for (int childId : idsToDelete) {
                jdbcTemplate.update("delete from comment where commentId = ?;", childId);
            }
        }

        // now delete the top level comment
        return jdbcTemplate.update("delete from comment where commentId = ?;", commentId) > 0;
    }



    private List<Comment> findAllIncludingChildren() {
        final String sql = "select * from comment;";
        List<Comment> comments = jdbcTemplate.query(sql, new CommentMapper());

        for (Comment mc : comments) {
            List<Comment> childComments = comments.stream()
                    .filter(c -> c.getParentCommentId() == mc.getCommentId())
                    .collect(Collectors.toList());
            mc.setChildComments(childComments);
        }

        return comments;
    }

}
