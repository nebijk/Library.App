package com.Library.Application.model;


import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a book.
 *
 * @author anderslm@kth.se
 */
public class Book {



    private int bookId;
    private String isbn;
    private String title;
    private Date published;

    private String genre;


    private int rating;


    private ArrayList<Author> authors;

    public Book(int bookId, String isbn, String title, Date published, String genre,int rating) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.published = published;
        this.genre = genre;
        this.rating=rating;
    }
    public Book(String isbn, String title, Date published, String genre,int rating) {
        this(-1, isbn, title, published, genre,rating);
    }
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }


    public int getBookId() { return bookId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public Date getPublished() { return published; }

    public void addAuthor(Author author) {
        authors.add(author);
    }
    public int getRating() {
        return rating;
    }

    public List<Author> getAuthors() {
        return  new ArrayList<>(authors);
    }
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }


    @Override
    public String toString() {
        return title + ", " + isbn + ", " + published.toString()+ genre + ", "+rating;
    }
}