package com.Library.Application;
import com.Library.Application.model.BooksDbInterface;
import com.Library.Application.model.BooksImplements;
import com.Library.Application.view.BooksPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BookApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        BooksDbInterface booksDb = new BooksImplements(); // Använd din JDBC-implementation här istället för mock-implementationen
        BooksPane booksPane = new BooksPane((BooksImplements) booksDb);

        Scene scene = new Scene(booksPane, 800, 600);
        primaryStage.setTitle("Book Library");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Startar JavaFX-applikationen
    }
}
