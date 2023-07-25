package org.example.models;

import java.util.Objects;

public class Quote {
    // FIELDS
    private int quoteId;
    private String text;
    private int pageNumber;
    private int libraryItemId;


    // CONSTRUCTOR
    public Quote(int quoteId, String text, int pageNumber, int libraryItemId) {
        this.quoteId = quoteId;
        this.text = text;
        this.pageNumber = pageNumber;
        this.libraryItemId = libraryItemId;
    }

    public Quote() {
    }


    // GETTER & SETTER
    public int getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(int quoteId) {
        this.quoteId = quoteId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getLibraryItemId() {
        return libraryItemId;
    }

    public void setLibraryItemId(int libraryItemId) {
        this.libraryItemId = libraryItemId;
    }


    // EQUALS & HASHCODE
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quote quote = (Quote) o;
        return quoteId == quote.quoteId && pageNumber == quote.pageNumber && libraryItemId == quote.libraryItemId && Objects.equals(text, quote.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quoteId, text, pageNumber, libraryItemId);
    }
}
