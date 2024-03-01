package com.Library.Application.view;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.Library.Application.model.Author;
import com.Library.Application.model.Book;
import com.Library.Application.model.BooksImplements;
import com.Library.Application.model.SearchMode;
import com.example.SQLbibliotek.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.Pair;


/**
 * The main pane for the view, extending VBox and including the menus. An
 * internal BorderPane holds the TableView for books and a search utility.
 *
 * @author anderslm@kth.se
 */
public class BooksPane extends VBox {

    private TableView<Book> booksTable;
    private ObservableList<Book> booksInTable; // the data backing the table view

    private Controller controller;

    private ComboBox<SearchMode> searchModeBox;
    private TextField searchField;
    private Button searchButton;

    private MenuBar menuBar;


    public BooksPane(BooksImplements booksDb) {
        this.controller = new Controller(booksDb, this);
        this.init(controller);
    }

    /**
     * Display a new set of books, e.g. from a database select, in the
     * booksTable table view.
     *
     * @param books the books to display
     */
    public void displayBooks(List<Book> books) {
        booksInTable.clear();
        booksInTable.addAll(books);
        booksTable.refresh(); // Force the TableView to update

    }

    /**
     * Notify user on input error or exceptions.
     *
     * @param msg  the message
     * @param type types: INFORMATION, WARNING etc.
     */
    protected void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void init(Controller controller) {

        booksInTable = FXCollections.observableArrayList();

        // init views and event handlers
        initBooksTable();
        initSearchView(controller);
        initMenus();

        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(searchModeBox, searchField, searchButton);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    private void initBooksTable() {
        booksTable = new TableView<>();
        booksTable.setEditable(false);
        // Definiera kolumner
        TableColumn<Book, Integer> bookIdCol = new TableColumn<>("book Id");

        TableColumn<Book, String> titleCol = new TableColumn<>("title");
        TableColumn<Book, String> isbnCol = new TableColumn<>("isbn");
        TableColumn<Book, Date> publishedCol = new TableColumn<>("published");
        TableColumn<Book, String> genreCol = new TableColumn<>("genre");
        TableColumn<Book, Integer> ratingCol = new TableColumn<>("rating");


        titleCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.5));
        // Ställ in hur varje cell ska fyllas
        bookIdCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        publishedCol.setCellValueFactory(new PropertyValueFactory<>("published"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));

        // Lägg till kolumnerna i TableView
        booksTable.getColumns().addAll(bookIdCol, titleCol, isbnCol, publishedCol, genreCol, ratingCol);

        // Associate the table view with the data
        booksInTable = FXCollections.observableArrayList();
        booksTable.setItems(booksInTable);
    }

    private void initSearchView(Controller controller) {
        searchField = new TextField();
        searchField.setPromptText("Search for...");
        searchModeBox = new ComboBox<>();
        searchModeBox.getItems().addAll(SearchMode.values());
        searchModeBox.setValue(SearchMode.Title);
        searchButton = new Button("Search");

        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String searchFor = searchField.getText();
                SearchMode mode = searchModeBox.getValue();
                controller.onSearchSelected(searchFor, mode);
            }
        });
    }

    private void showAddBookDialog() {
        Dialog<Pair<Book, List<Author>>> dialog = new Dialog<>();
        dialog.setTitle("Add/Edit Book");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));


        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        DatePicker publishedPicker = new DatePicker();
        TextField genreField = new TextField();
        genreField.setPromptText("Genre");
        TextField ratingField = new TextField();
        ratingField.setPromptText("Rating");
        ListView<Author> authorListView = new ListView<>();
        authorListView.setItems(FXCollections.observableArrayList(controller.fetchAllAuthors()));
        authorListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        authorListView.setPrefHeight(100); // Justera höjden efter behov


        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("ISBN:"), 0, 1);
        grid.add(isbnField, 1, 1);
        grid.add(new Label("Published:"), 0, 2);
        grid.add(publishedPicker, 1, 2);
        grid.add(new Label("Genre:"), 0, 3);
        grid.add(genreField, 1, 3);
        grid.add(new Label("Rating:"), 0, 4);
        grid.add(ratingField, 1, 4);
        grid.add(new Label("Author:"), 0, 5);
        grid.add(authorListView, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);



        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Book book = new Book(
                        titleField.getText(),
                        isbnField.getText(),
                        Date.valueOf(publishedPicker.getValue()),
                        genreField.getText(),
                        Integer.parseInt(ratingField.getText())
                );
                List<Author> selectedAuthors = new ArrayList<>(authorListView.getSelectionModel().getSelectedItems());
                return new Pair<>(book,selectedAuthors );
            }
            return null;
        });

        Optional<Pair<Book, List<Author>>> result = dialog.showAndWait();

        result.ifPresent(bookAuthorPair -> {
            Book book = bookAuthorPair.getKey();
            List<Author> authors = bookAuthorPair.getValue();
            controller.handleAddBookWithAuthors(book,authors);
        });
    }

    private void initMenus() {

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");

        MenuItem connectItem = new MenuItem("Connect to Db");
        connectItem.setOnAction(event -> controller.connectToDatabase());


        MenuItem disconnectItem = new MenuItem("Disconnect");
        disconnectItem.setOnAction(event -> controller.disconnectFromDatabase());

        fileMenu.getItems().addAll(exitItem, connectItem, disconnectItem);

        Menu searchMenu = new Menu("Search");
        MenuItem titleItem = new MenuItem("Title");

        MenuItem isbnItem = new MenuItem("ISBN");
        MenuItem authorItem = new MenuItem("Author");
        MenuItem genreItem = new MenuItem("genre");
        MenuItem ratingItem = new MenuItem("rating");

        titleItem.setOnAction(event -> {
            controller.onSearchSelected(searchField.getText(), SearchMode.Title);
        });

        isbnItem.setOnAction(event -> {
            controller.onSearchSelected(searchField.getText(), SearchMode.ISBN);
        });
        authorItem.setOnAction(event -> {
            controller.onSearchSelected(searchField.getText(), SearchMode.Author);
        });
        genreItem.setOnAction(event -> {
            controller.onSearchSelected(searchField.getText(), SearchMode.genre);
        });
        ratingItem.setOnAction(event -> {
            controller.onSearchSelected(searchField.getText(), SearchMode.rating);
        });
        searchMenu.getItems().addAll(titleItem, isbnItem, authorItem, genreItem, ratingItem);

        Menu manageMenu = new Menu("Manage");
        MenuItem addbookItem = new MenuItem("Add book");
        addbookItem.setOnAction(e -> showAddBookDialog());

        MenuItem addauthorItem = new MenuItem("add author");
        addauthorItem.setOnAction(e->showAddAuthorDialog());
        MenuItem updateItem = new MenuItem("Update");
        manageMenu.getItems().addAll(addbookItem, addauthorItem, updateItem);
        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, searchMenu, manageMenu);


    }
    private void showAddAuthorDialog() {
        // Skapa dialogrutan
        Dialog<Author> dialog = new Dialog<>();
        dialog.setTitle("Add New Author");

        // Skapa och konfigurera GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Skapa namnfält och födelsedagsväljare
        TextField nameField = new TextField();
        nameField.setPromptText("Author's Name");
        DatePicker birthDatePicker = new DatePicker();
        birthDatePicker.setPromptText("Birthdate");

        // Lägg till komponenterna i GridPane
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Birthdate:"), 0, 1);
        grid.add(birthDatePicker, 1, 1);

        // Typ av knappar i dialogrutan
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Sätt innehållet i dialogrutan
        dialog.getDialogPane().setContent(grid);

        // Konvertera resultatet till en författare när OK-knappen klickas
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Author(nameField.getText(), Date.valueOf(birthDatePicker.getValue()));
            }
            return null;
        });

        // Visa dialogrutan och vänta på respons
        Optional<Author> result = dialog.showAndWait();

        result.ifPresent(author -> {
         controller.addAuthor(author);
            System.out.println("New Author: " + author.getName() + ", Birthdate: " + author.getBirthdate());
        });
    }
}

