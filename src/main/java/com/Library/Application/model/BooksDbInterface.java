package com.Library.Application.model;

import java.util.List;

/**
 * This interface declares methods for querying a Books database.
 * Different implementations of this interface handles the connection and
 * queries to a specific DBMS and database, for example a MySQL or a MongoDB
 * database.
 *
 * NB! The methods in the implementation must catch the SQL/MongoDBExceptions thrown
 * by the underlying driver, wrap in a BooksDbException and then re-throw the latter
 * exception. This way the interface is the same for both implementations, because the
 * exception type in the method signatures is the same. More info in BooksDbException.java.
 *
 * @author anderslm@kth.se
 */
public interface BooksDbInterface {

    /**
     * Connect to the database.
     * @param database
     * @return true on successful connection.
     */
    public boolean connect(String database) throws BooksDbException;

    public void disconnect() throws BooksDbException;

    public List<Book> searchBooksByTitle(String title) throws BooksDbException;

    List<Book> searchBooksByISBN(String isbn) throws BooksDbException;

    List<Book> searchBooksByAuthor(String author) throws BooksDbException;
    int addBook(Book book) throws BooksDbException;


    void addAuthor(Author author) throws BooksDbException;

    List<Book> searchBooksByRating(int rating) throws BooksDbException;

    List<Book> searchBooksByGenre(String genre) throws BooksDbException;
    public void addBookAuthor(BookAuthor bookAuthor) throws BooksDbException;

    public void addBookWithAuthors(Book book, List<Author> authors) throws BooksDbException;
    List<Author> getAllAuthors() throws BooksDbException;

}