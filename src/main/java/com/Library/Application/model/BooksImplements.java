package com.Library.Application.model;
import java.sql.SQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BooksImplements implements BooksDbInterface {

    private Connection c;

    @Override
    public boolean connect(String database) throws BooksDbException {
        try {
            String url = "jdbc:mysql://localhost:3306/" + database;
            String user = "root";
            String password = "Coola145!";
            c = DriverManager.getConnection(url, user, password);
            return true;
        } catch (SQLException e) {
            throw new BooksDbException("Failed to connect to the database", e);
        }
    }

    @Override
    public void disconnect() throws BooksDbException {
        try {
            if (c != null && !c.isClosed()) {
                c.close();

            }
        } catch (SQLException e) {
            throw new BooksDbException("Failed to disconnect from the database", e);
        }
    }
    /**
     * Retrieves books from the database matching  given title
     *
     * @throws BooksDbException on query failure.
     */
    @Override
    public List<Book> searchBooksByTitle(String title) throws BooksDbException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE title LIKE ?";
        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, "%" + title + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int bookId = rs.getInt("bookId");
                    String isbn = rs.getString("isbn");
                    Date published = rs.getDate("published");
                    String genre = rs.getString("genre");
                    int rating = rs.getInt("rating");

                    Book book = new Book(bookId, isbn, title, published, genre, rating);
                    books.add(book);
                }
            }
            return books;
        } catch (SQLException e) {
            throw new BooksDbException("Failed to search books by title", e);
        }
    }
    /**
     * Retrieves a list of books from the database based on a specific ISBN.
     * Executes a query to find all books in the 'book' table with the given ISBN.
     *
     * @param isbn The International Standard Book Number used to search for books.
     * @return A list of {@link Book} objects that have the specified ISBN.
     * @throws BooksDbException If a database error occurs during the search.
     */
    @Override
    public List<Book> searchBooksByISBN(String isbn) throws BooksDbException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE isbn = ?";
        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int bookId = rs.getInt("bookId");
                    String title = rs.getString("title");
                    String isbnResult = rs.getString("isbn");
                    Date published = rs.getDate("published");
                    String genre = rs.getString("genre");
                    int rating = rs.getInt("rating");
                    Book book = new Book(bookId, isbnResult, title, published, genre, rating);
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Failed to search books by ISBN", e);
        }
        return books;
    }


    /**
     * Searches and retrieves a list of books by a specific author from the database.
     * This method performs a join between the book, book_authors, and authors tables
     * to find books associated with an author whose name matches the provided pattern.
     *
     * @param authorName The name of the author to search for (supports partial matching).
     * @return A list of {@link Book} objects written by the specified author.
     * @throws BooksDbException If there's an error executing the query.
     */

    @Override
    public List<Book> searchBooksByAuthor(String authorName) throws BooksDbException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.* FROM book b " +
                "INNER JOIN book_authors ba ON b.bookId = ba.bookId " +
                "INNER JOIN authors a ON ba.authorId = a.authorId " +
                "WHERE a.name LIKE ?";

        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, "%" + authorName + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int bookId = rs.getInt("bookId");
                    String title = rs.getString("title");
                    String isbn = rs.getString("isbn");
                    Date published = rs.getDate("published");
                    String genre = rs.getString("genre");
                    int rating = rs.getInt("rating");

                    Book book = new Book(bookId, title, isbn, published, genre, rating);
                    books.add(book);
                }
            }
            return books;
        } catch (SQLException e) {
            throw new BooksDbException("Failed to search books by author", e);
        }
    }

    @Override
    public int addBook(Book book) throws BooksDbException {
        String sql = "INSERT INTO book (title, isbn, published, genre, rating) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getIsbn());
            stmt.setDate(3, book.getPublished());
            stmt.setString(4, book.getGenre());
            stmt.setInt(5, book.getRating());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new BooksDbException("Creating book failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new BooksDbException("Creating book failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error adding book to the database", e);
        }
    }
    /**
     * Adds a new author to the database.
     * This method inserts an author record into the 'authors' table.
     *
     * @param author The {@link Author} object representing the author to be added.
     * @throws BooksDbException If there's a SQL error during the insert operation.
     */
    @Override
    public void addAuthor(Author author) throws BooksDbException {
        String sql = "INSERT INTO authors (name, birthdate) VALUES (?, ?)";
        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, author.getName());
            stmt.setDate(2, author.getBirthdate());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error adding author to the database", e);
        }
    }
    /**
     * Retrieves a list of books from the database that match a specific genre.
     *
     * @param genre The genre to use as a filter in the search.
     * @return A list of {@link Book} objects belonging to the specified genre.
     * @throws BooksDbException If there's a SQL error during the query execution.
     */

    public List<Book> searchBooksByGenre(String genre) throws BooksDbException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE genre = ?";
        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, genre);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int bookId = rs.getInt("bookId");
                    String title = rs.getString("title");
                    String isbn = rs.getString("isbn");
                    Date published = rs.getDate("published");
                    String genreResult = rs.getString("genre");
                    int rating = rs.getInt("rating");

                    Book book = new Book(bookId, isbn, title, published, genreResult, rating);
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Failed to search books by genre", e);
        }
        return books;
    }
    /**
     * Searches for and retrieves all books with a specified rating from the database.
     *
     * @param rating The rating to filter the books by.
     * @return A list of {@link Book} objects matching the specified rating.
     * @throws BooksDbException If there's a SQL error during the query execution.
     */
    public List<Book> searchBooksByRating(int rating) throws BooksDbException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE rating = ?";
        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, rating);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int bookId = rs.getInt("bookId");
                    String title = rs.getString("title");
                    String isbn = rs.getString("isbn");
                    Date published = rs.getDate("published");
                    String genreResult = rs.getString("genre");
                    rating = rs.getInt("rating");
                    Book book = new Book(bookId, isbn, title, published, genreResult, rating);
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Failed to search books by rating", e);
        }
        return books;
    }
    /**
     * Retrieves all authors from the database.
     * This method queries the 'authors' table and constructs a list of Author objects.
     *
     * @return A list of {@link Author} objects.
     * @throws BooksDbException If there's a SQL error during the database query.
     */
    public List<Author> getAllAuthors() throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM authors";
        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int authorId = rs.getInt("authorId");
                    String name = rs.getString("name");
                    Date birthdate = rs.getDate("birthdate");
                    Author author = new Author(authorId, name, birthdate);
                    authors.add(author);
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Failed to retrieve authors", e);
        }
        return authors;
    }

    public void addBookAuthor(BookAuthor bookAuthor) throws BooksDbException {
        String sql = "INSERT INTO book_authors (bookId, authorId) VALUES (?, ?)";
        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, bookAuthor.getBookId());
            stmt.setInt(2, bookAuthor.getAuthorId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error adding book author relation to the database", e);
        }
    }


    /**
     * Adds a book with its associated authors to the database, handling the process transactionally.
     * @param book The book to be added.
     * @param authors The list of authors associated with the book.
     * @throws BooksDbException If a database error occurs or the transaction fails.
     */
    public void addBookWithAuthors(Book book, List<Author> authors) throws BooksDbException {
        try {
            if (c == null || c.isClosed()) {
                throw new SQLException("Database connection is not established.");
            }
            c.setAutoCommit(false);
            int bookId = addBook(book);
            for (Author author : authors) {
                addBookAuthor(new BookAuthor(bookId, author.getAuthorId()));
            }
            c.commit();
        } catch (SQLException e) {
            try {
                if (c != null) {
                    c.rollback();
                }
            } catch (SQLException ex) {
                throw new BooksDbException("Failed to rollback transaction after error", ex);
            }
            throw new BooksDbException("Error adding book with authors", e);
        } finally {
            try {
                if (c != null) {
                    c.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                throw new BooksDbException("Error resetting auto-commit", ex);
            }
        }
    }

}



