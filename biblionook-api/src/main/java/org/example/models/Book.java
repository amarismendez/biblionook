package org.example.models;

public class Book {
    // FIELDS
    private int bookId;
    private String isbn;
    private String title;
    private String author;
    private String imageLink;


    // CONSTRUCTOR
    public Book(int bookId, String isbn, String title, String author, String imageLink) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.imageLink = imageLink;
    }

    public Book() {
    }


    // GETTER & SETTER
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }


}
