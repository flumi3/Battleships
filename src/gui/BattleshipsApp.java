package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main des ganzen Projekts. Hier wird die Applikation gestartet und das Fenster sichtbar gemacht.
 */
public class BattleshipsApp extends Application {
    private static Stage window; // Stage which is used
    private static Scene mainMenuScene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;

        // Initialize Main Menu
        MainMenu mainMenu = new MainMenu();
        mainMenuScene = mainMenu.mainMenuLayout();

        // Make the Application visible
        window.setTitle("Battleships");
        window.setScene(mainMenuScene);
        window.show();
    }

    /**
     * Getter f�r die Stage. Wird ben�tigt um die Szene in anderen Klassen zu wechseln.
     *
     * @return Die Stage.
     */
    static Stage getStage() {
        return window;
    }

    /**
     * Getter f�r das Main Men�. Wird ben�tigt um immer in das gleiche Hauptmen� zu gelangen, ohne ein neues erzeugen zu m�ssen.
     *
     * @return Die Szene des Hauptmen�s
     */
    static Scene getMainMenu() {
        return mainMenuScene;
    }
}
