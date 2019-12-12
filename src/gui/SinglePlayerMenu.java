package gui;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import logic.Feld;
import logic.GameSaver;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Menü in welchem der Spieler entscheiden kann ob er ein neues Spiel startet oder ein Altes laden möchte.
 */
public class SinglePlayerMenu extends Menu {
    private double windowWidth;
    private double windowHeight;
    private MainMenu mainMenu;

    private Scene currentScene;
    private Scene lastScene; // Necessary for the back action

    private Pane root;
    private VBox menuBox;
    private Line line;

    // New Game Menu
    private NewGameMenu ngMenu;
    private Scene ngMenuScene;

    /**
     * Menü-Punkte bestehend aus einem String, welcher den Name des Menü-Punktes darstellt und einem Runnable, in welchem
     * der Code steht, der ausgeführt werden soll, nachdem auf ein Menü-Punkt geklickt wurde. Basierend auf dieser Liste
     * werden die MenuItems erstellt. Somit wurden hier personalisierte Buttons erstellt.
     */
    private List<Pair<String, Runnable>> menuData = Arrays.asList(
            new Pair<String, Runnable>("NEW GAME", () -> {
                super.startClickSound();
                ngMenu.setWindowSizes(root.getWidth(), root.getHeight());
                ngMenu.setLastScene(currentScene);
                ngMenu.setSpMenu(this); // Übergebe aktuelle Instanz, benötigt für die Fenstergröße der Back-Operation
                BattleshipsApp.getStage().setScene(ngMenuScene);
            }),
            new Pair<String, Runnable>("LOAD GAME", () -> {
                super.startClickSound();

                GameSaver gameOpener = new GameSaver();
                Feld playerField = null;
                Feld enemyField = null;

                // Load the fields that have been saved
                try {
                    Feld[] loadedFields = gameOpener.loadFile();
                    playerField = loadedFields[0];
                    enemyField = loadedFields[1];

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Create new instance of SinglePlayer and set its window sizes and the field size
                SinglePlayer sp = new SinglePlayer();
                sp.setWindowSizes(root.getWidth(), root.getHeight());
                sp.setFieldSize(playerField.size());

                // Set the player field and the game field to the ones that have been loaded
                sp.setPlayerField(playerField);
                sp.setEnemyField(enemyField);

                Scene spScene = sp.singlePlayerLayout();

                // Set music
                super.stopMenuMusic(); // Stop menu music
                super.startGameMusic(); // start game music
                boolean isPlaying = super.isPlaying();
                sp.setGameMusicPlayer(super.getGameMusicPlayer());
                if (isPlaying)
                    sp.setPlaying(true);
                else
                    sp.setPlaying(false);

                BattleshipsApp.getStage().setScene(spScene); // Switch scenes
            }),
            new Pair<String, Runnable>("BACK", () -> {
                super.startClickSound();
                mainMenu.setWindowSizes(root.getWidth(), root.getHeight());
                BattleshipsApp.getStage().setScene(lastScene);
            })
    );

    SinglePlayerMenu() {
        root = super.root;
        menuBox = new VBox();

        ngMenu = new NewGameMenu();
        ngMenuScene = ngMenu.newGameMenuLayout();
    }

    /**
     * Erzeugt die Szene, die der Nutzer sieht, wenn er im Hauptmenü auf den Reiter "Single Player" klickt.
     *
     * @return Die erzeugt Szene.
     */
    Scene singlePlayerMenuLayout() {
        // Create a menu item for every pair in the list
        menuData.forEach(data -> {
            MenuItem item = new MenuItem(data.getKey());
            item.setOnAction(data.getValue());
            item.setTranslateX(-300);

            Rectangle clip = new Rectangle(300, 30);
            clip.translateXProperty().bind(item.translateXProperty().negate());

            item.setClip(clip);

            menuBox.getChildren().addAll(item);
        });

        // Add Background
        super.addBackground("MenuBackground.png");

        // Add Title
        super.addTitle("SINGLE PLAYER", 38);

        // Add line
        line = super.addLine(root.getWidth() / 2 - 100, root.getHeight() / 3 + 50);

        // Add menu
        super.addMenu(menuBox);

        // Start animation
        super.startAnimation(menuBox, line);

        currentScene = new Scene(root);
        return currentScene;
    }

    void setLastScene(Scene s) {
        lastScene = s;
    }

    void setWindowSizes(double width, double height) {
        windowWidth = width;
        windowHeight = height;
        root.setPrefSize(windowWidth, windowHeight);
    }

    void setMainMenu(MainMenu m) {
        this.mainMenu = m;
    }
}
