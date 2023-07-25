package org.example.domain;

import org.example.data.CommentJdbcTemplateRepository;
import org.example.models.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class CommentServiceTest {
    @Autowired
    CommentService service;

    @MockBean
    CommentJdbcTemplateRepository repository;

    @Test
    void findByUserExperienceId() {
        List<Comment> existing = List.of(makeComment(), makeSecondComment());
        when(repository.findByIsbn("1234567891234")).thenReturn(existing);

        List<Comment> comments = service.findByIsbn("1234567891234");

        assertEquals(existing, comments);
        assertEquals(2, comments.size());
    }

    @Test
    void shouldNotFindMissing() {
        when(repository.findByIsbn("1")).thenReturn(new ArrayList<>());

        List<Comment> comments = service.findByIsbn("1");

        assertEquals(0, comments.size());
    }

    @Test
    void shouldCreate() {
        Comment comment = makeComment();
        when(repository.create(any())).thenReturn(comment);

        Result<Comment> result = service.create(comment);

        assertTrue(result.isSuccess());
        assertNotNull(result.getPayload());
    }

    @Test
    void shouldNotCreateInvalid() {
        Comment comment = makeInvalidComment();
        Result<Comment> result = service.create(comment);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Comment text is required."));
        assertTrue(result.getMessages().contains("Likes cannot be negative."));
    }

    @Test
    void shouldNotCreateDuplicate() {
        Comment comment = makeComment();
        when(repository.findAll()).thenReturn(List.of(comment));

        Result<Comment> result = service.create(comment);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("This comment already exists"));
    }

    @Test
    void shouldUpdate() {
        Comment comment = makeComment();
        when(repository.update(comment)).thenReturn(true);

        Result<Comment> result = service.update(comment);

        assertEquals(ResultType.SUCCESS, result.getType());
        assertEquals(comment, result.getPayload());
    }

    @Test
    void shouldNotUpdateMissing() {
        Comment comment = makeComment();
        when(repository.update(comment)).thenReturn(false);

        Result<Comment> result = service.update(comment);

        assertEquals(ResultType.NOT_FOUND, result.getType());
        assertNull(result.getPayload());
        assertTrue(result.getMessages().contains("There is no comment by 4 ID"));
    }

    @Test
    void shouldNotUpdateInvalid() {
        Comment comment = makeInvalidComment();
        Result<Comment> result = service.update(comment);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Comment text is required."));
        assertTrue(result.getMessages().contains("Likes cannot be negative."));
    }

    @Test
    void shouldNotUpdateDuplicate() {
        Comment comment = makeComment();
        Comment duplicate = makeComment();
        duplicate.setCommentId(8);

        when(repository.findAll()).thenReturn(List.of(comment));
        Result<Comment> result = service.update(duplicate);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("This comment already exists"));
    }

    @Test
    void shouldDeleteById() {
        when(repository.deleteById(2)).thenReturn(true);
        assertTrue(service.deleteById(2));
    }

    @Test
    void shouldNotDeleteByMissingId() {
        when(repository.deleteById(2)).thenReturn(false);
        assertFalse(service.deleteById(2));
    }



    private Comment makeComment() {
        Comment comment = new Comment();
        comment.setCommentId(4);
        comment.setText("LMAO");
        comment.setCreatedAt(LocalDate.now());
        comment.setParentCommentId(0);
        comment.setIsbn("1234567891234");
        comment.setAppUserId(1);

        return comment;
    }

    private Comment makeSecondComment() {
        Comment comment = new Comment();
        comment.setCommentId(5);
        comment.setText("LMAO me too");
        comment.setCreatedAt(LocalDate.now());
        comment.setParentCommentId(0);
        comment.setIsbn("1234567891234");
        comment.setAppUserId(1);

        return comment;
    }

    private Comment makeChildComment(int parentCommentId) {
        Comment comment = new Comment();
        comment.setCommentId(4);
        comment.setText("LMAO");
        comment.setCreatedAt(LocalDate.now());
        comment.setParentCommentId(parentCommentId);
        comment.setIsbn("1234567891234");
        comment.setAppUserId(1);

        return comment;
    }

    private Comment makeInvalidComment() {
        Comment comment = new Comment();
        comment.setCommentId(4);
        comment.setText("    ");
        comment.setLikes(-1);
        comment.setLikeIds(new ArrayList<>());
        comment.setCreatedAt(LocalDate.of(2024,9,28));
        comment.setParentCommentId(0);
        comment.setIsbn(null);
        comment.setAppUserId(1);

        return comment;
    }
}