package org.example.domain;

import org.example.data.CommentRepository;
import org.example.models.Comment;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository repository;
    public CommentService(CommentRepository repository) {
        this.repository = repository;
    }

    public List<Comment> findAll() {
        return repository.findAll();
    }

    public List<Comment> findByIsbn(String isbn) {
        return repository.findByIsbn(isbn);
    }

    public Result<Comment> create(Comment comment) {
        comment.setCreatedAt(LocalDate.now());
        Result<Comment> result = validate(comment);

        for (Comment c : repository.findAll()) {
            checkUniqueness(c, comment, result);
            if (c.getChildComments() != null) {
                for (Comment child : c.getChildComments()) {
                    checkUniqueness(child, comment, result);
                }
            }
        }

        if (!result.isSuccess()) {
            return result;
        }

        comment = repository.create(comment);
        result.setPayload(comment);

        return result;
    }

    public Result<Comment> update(Comment comment) {
        comment.setCreatedAt(LocalDate.now());
        Result<Comment> result = validate(comment);

        for (Comment c : repository.findAll()) {
            checkUniquenessForUpdate(c, comment, result);
            if (c.getChildComments() != null) {
                for (Comment child : c.getChildComments()) {
                    checkUniquenessForUpdate(child, comment, result);
                }
            }
        }

        if (!result.isSuccess()) {
            return result;
        }

        if (!repository.update(comment)) {
            result.addMessage(ResultType.NOT_FOUND,
                    String.format("There is no comment by %s ID", comment.getCommentId()));
            return result;
        }

        result.setPayload(comment);
        return result;
    }

    public boolean deleteById(int commentId) {
        return repository.deleteById(commentId);
    }

    private Result<Comment> validate(Comment comment) {
        Result<Comment> result = new Result<>();

        if (comment.getText() == null || comment.getText().isBlank()) {
            result.addMessage(ResultType.INVALID, "Comment text is required.");
        }

        if (comment.getLikes() < 0) {
            result.addMessage(ResultType.INVALID, "Likes cannot be negative.");
        }

        if (comment.getCreatedAt() == null || comment.getCreatedAt().isAfter(LocalDate.now())) {
            result.addMessage(ResultType.INVALID, "Date is required and cannot be in the future.");
        }

        return result;
    }

    private void checkUniqueness(Comment c, Comment comment, Result<Comment> result) {
        if (c.getText().equalsIgnoreCase(comment.getText()) &&
                c.getAppUserId() == comment.getAppUserId() &&
                c.getParentCommentId() == comment.getParentCommentId() &&
                c.getIsbn().equalsIgnoreCase(comment.getIsbn())) {
            result.addMessage(ResultType.INVALID, "This comment already exists");
        }
    }

    private void checkUniquenessForUpdate(Comment c, Comment comment, Result<Comment> result) {
        if (c.getCommentId() != comment.getCommentId() &&
                c.getText().equalsIgnoreCase(comment.getText()) &&
                c.getAppUserId() == comment.getAppUserId() &&
                c.getParentCommentId() == comment.getParentCommentId() && // user can comment the same thing "LOL" as long as its in different threads.
                c.getIsbn().equalsIgnoreCase(comment.getIsbn())) {
            result.addMessage(ResultType.INVALID, "This comment already exists");
        }
    }
}
