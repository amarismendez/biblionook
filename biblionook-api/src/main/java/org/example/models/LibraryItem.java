package org.example.models;

import java.util.Objects;

public class LibraryItem {
    // FIELDS
    private int libraryItemId;
    private String isbn;
    private int rating;
    private String description;
    private BookStatus status;
    private int appUserId;

    // CONSTRUCTOR
    public LibraryItem(int libraryItemId, String isbn, int rating, String description, BookStatus status, int appUserId) {
        this.libraryItemId = libraryItemId;
        this.isbn = isbn;
        this.rating = rating;
        this.description = description;
        this.status = status;
        this.appUserId = appUserId;
    }

    public LibraryItem() {
    }

    // GETTERS & SETTERS
    public int getLibraryItemId() {
        return libraryItemId;
    }

    public void setLibraryItemId(int libraryItemId) {
        this.libraryItemId = libraryItemId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public int getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(int appUserId) {
        this.appUserId = appUserId;
    }

    // EQUALS & HASHCODE OVERRIDE
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraryItem that = (LibraryItem) o;
        return libraryItemId == that.libraryItemId && rating == that.rating && appUserId == that.appUserId && Objects.equals(isbn, that.isbn) && Objects.equals(description, that.description) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(libraryItemId, isbn, rating, description, status, appUserId);
    }

}
