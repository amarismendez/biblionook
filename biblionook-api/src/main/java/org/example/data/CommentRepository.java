package org.example.data;

import org.example.models.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> findAll();

    List<Comment> findByIsbn(String isbn);

    Comment create(Comment comment);

    boolean update(Comment comment);

    boolean deleteById(int commentId);
}
