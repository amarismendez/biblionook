package org.example.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Comment {
    private int commentId;
    private String text;
    private int likes;
    private List<Integer> likeIds;
    private LocalDate createdAt;
    private List<Comment> childComments;
    private int parentCommentId;
    private String isbn;
    private int appUserId;

    public Comment(int commentId, String text, int parentCommentId, String isbn, int appUserId) {
        this.commentId = commentId;
        this.text = text;
        this.likes = 0;
        this.likeIds = new ArrayList<>();
        this.createdAt = LocalDate.now();
        this.childComments = new ArrayList<>();
        this.parentCommentId = parentCommentId;
        this.isbn = isbn;
        this.appUserId = appUserId;
    }

    public Comment() {

    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public List<Integer> getLikeIds() {
        return likeIds;
    }

    public void setLikeIds(List<Integer> likeIds) {
        this.likeIds = likeIds;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public List<Comment> getChildComments() {
        return childComments;
    }

    public void setChildComments(List<Comment> childComments) {
        this.childComments = childComments;
    }

    public void addChildComment(Comment childComment) {
        childComment.setParentCommentId(this.commentId);
        this.childComments.add(childComment);
    }

    public int getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(int parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(int appUserId) {
        this.appUserId = appUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return commentId == comment.commentId && likes == comment.likes && parentCommentId == comment.parentCommentId && appUserId == comment.appUserId && Objects.equals(text, comment.text) && Objects.equals(likeIds, comment.likeIds) && Objects.equals(createdAt, comment.createdAt) && Objects.equals(childComments, comment.childComments) && Objects.equals(isbn, comment.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, text, likes, likeIds, createdAt, childComments, parentCommentId, isbn, appUserId);
    }
}
