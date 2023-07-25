package org.example.data;

import org.example.models.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentJdbcTemplateRepositoryTest {
    @Autowired
    private CommentJdbcTemplateRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    static boolean hasSetup = false;

    @BeforeEach
    void setup() {
        if (!hasSetup) {
            hasSetup = true;
            jdbcTemplate.update("call set_known_good_state();");
        }
    }

    @Test
    void shouldFindAll() {
        List<Comment> result = repository.findAll();
        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertTrue(result.get(0).getChildComments().size() > 0);
    }

    @Test
    void shouldFindByIsbn() {
        List<Comment> result = repository.findByIsbn("9781451696196");
        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertTrue(result.get(0).getChildComments().size() > 0);
    }

    @Test
    void shouldCreate() {
        Comment comment = makeComment();

        Comment result = repository.create(comment);

        assertNotNull(result);
        assertEquals(3, result.getCommentId());

    }

    @Test
    void shouldUpdate() {
        Comment comment = makeComment();
        comment = repository.create(comment);
        comment.setText("LMFAOOO");
        int id = comment.getCommentId();

        assertTrue(repository.update(comment));
        assertTrue(comment.getText().equals(repository.findAll().stream().filter(c -> c.getCommentId() == id).findFirst().orElse(null).getText()));
    }

    @Test
    void shouldDeleteById() {
        Comment comment = repository.create(makeComment());
        assertTrue(repository.deleteById(comment.getCommentId()));
    }

    @Test
    void shouldDeleteByIdWithChildComments() {
        Comment comment = repository.create(makeComment());
        Comment childComment = repository.create(makeChildComment(comment.getCommentId()));
        Comment grandchildComment = repository.create(makeGrandchildComment(childComment.getCommentId()));
        assertTrue(repository.deleteById(comment.getCommentId()));
    }

    private Comment makeComment() {
        Comment comment = new Comment();
        comment.setCommentId(4);
        comment.setText("LMAO");
        comment.setCreatedAt(LocalDate.now());
        comment.setParentCommentId(0);
        comment.setIsbn("1234567891234");
        comment.setAppUserId(1);
        comment.setLikes(0);
        comment.setLikeIds(new ArrayList<>());

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
        comment.setLikes(0);
        comment.setLikeIds(new ArrayList<>());

        return comment;
    }

    private Comment makeGrandchildComment(int parentCommentId) {
        Comment comment = new Comment();
        comment.setCommentId(5);
        comment.setText("LMAO0000");
        comment.setCreatedAt(LocalDate.now());
        comment.setParentCommentId(parentCommentId);
        comment.setIsbn("1234567891234");
        comment.setAppUserId(1);
        comment.setLikes(0);
        comment.setLikeIds(new ArrayList<>());

        return comment;
    }
}