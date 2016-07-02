package com.example.android.bookslisting;

/**
 * Created by Navendu Agarwal on 30-Jun-16.
 */
public class Book {
    private String title;
    private String author;
    private String bookImageURL;

    public Book(String title, String author, String bookImageURL) {
        this.title = title;
        this.author = author;
        this.bookImageURL = bookImageURL;
    }


    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getBookImageURL() {
        return bookImageURL;
    }
}
