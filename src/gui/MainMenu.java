package gui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * Hauptmenü, in welchem der Nutzer auswählt was er tun möchte. Dort kann er entscheiden ob er im Einzelspieler- oder
 * Mehrspielermodus spielen möchte. Außerdem kann er von dort aus in die Spieloptionen wechseln.
 */
public class MainMenu extends Menu {
    private Pane root;
    private VBox menuBox;
    private Line line;

    private double windowWidth;
    private double windowHeight;

    private Scene currentScene; // Scene of the main menu

    // Single Player Menu
    private SinglePlayerMenu spMenu;
    private Scene spMenuScene;

    // Multi Player Menu
    private MultiPlayerMenu mpMenu;
    private Scene mpMenuScene;

    // Options Menu
    private OptionsMenu opMenu;
    private Scene opMenuScene;


    /**
     * Menü-Punkte bestehend aus einem String, welcher den Name des Menü-Punktes darstellt und einem Runnable, in welchem
     * der Code steht, der ausgeführt werden soll, nachdem auf ein Menü-Punkt geklickt wurde. Basierend auf dieser Liste
     * werden die MenuItems erstellt. Somit wurden hier personalisierte Buttons erstellt.
     */
    private List<Pair<String, Runnable>> menuData = Arrays.asList(
            new Pair<String, Runnable>("SINGLE PLAYER", () -> {
                super.startClickSound();
                spMenu.setWindowSizes(root.getWidth(), root.getHeight()); // Update window size
                spMenu.setLastScene(currentScene);
                spMenu.setMainMenu(this); // Übergebe aktuelle Instanz, benötigt für die Fenstergröße der Back-Operation
                BattleshipsApp.getStage().setScene(spMenuScene); // Switch Scene
                }),

            new Pair<String, Runnable>("MULTIPLAYER", () -> {
                super.startClickSound();
                mpMenu.setWindowSizes(root.getWidth(), root.getHeight()); // Update window size
                mpMenu.setLastScene(currentScene);
                mpMenu.setMainMenu(this); // Übergebe aktuelle Instanz, benötigt für die Fenstergröße der Back-Operation
                BattleshipsApp.getStage().setScene(mpMenuScene); // Switch Scene
            }),

            new Pair<String, Runnable>("GAME OPTIONS", () -> {
                super.bound = true;
                super.startClickSound();
                opMenu.setWindowSizes(root.getWidth(), root.getHeight()); // Update window size
                opMenu.setLastScene(currentScene);
                opMenu.setMainMenu(this); // Übergebe aktuelle Instanz, benötigt für die Fenstergröße der Back-Operation
                if (opMenuScene == null) // Wird nur initialisiert wenn es nicht schon getan wurde
                    opMenuScene = opMenu.optionsMenuLayout();
                BattleshipsApp.getStage().setScene(opMenuScene); // Switch Scene
            }),
            new Pair<String, Runnable>("EXIT TO DESKTOP", Platform::exit)
    );

    /**
     * Initalisiert den Container so wie Instanzen und Attribute die schon am Anfang gebraucht werden.
     */
    MainMenu() {
        root = super.root;
        menuBox = new VBox();

        // Initialize Single Player Menu
        spMenu = new SinglePlayerMenu();
        spMenuScene = spMenu.singlePlayerMenuLayout();

        mpMenu = new MultiPlayerMenu();
        mpMenuScene = mpMenu.multiPlayerMenuLayout();

        opMenu = new OptionsMenu();

        super.startMenuMusic();
    }

    /**
     * Erzeugt die Szene, welcher der Nutzer später als Hauptmenü sieht.
     * @return Die erzeugte Szene.
     */
    Scene mainMenuLayout() {
        // Erzeuge ein MenuItem für jedes Paar das in der Liste ist
        menuData.forEach(data -> {
            MenuItem item = new MenuItem(data.getKey());
            item.setOnAction(data.getValue());
            item.setTranslateX(-300);

            Rectangle clip = new Rectangle(300, 30);
            clip.translateXProperty().bind(item.translateXProperty().negate());

            item.setClip(clip);

            menuBox.getChildren().addAll(item);
        });

        // Add background
        super.addBackground("MenuBackground.png");

        // Add title
        super.addTitle("BATTLESHIPS", 52);

        // Add line
        line = super.addLine(root.getWidth() / 2 - 100, root.getHeight() / 3 + 50);

        // Add menu
        super.addMenu(menuBox);

        // Start animation
        super.startAnimation(menuBox, line);


        currentScene = new Scene(root);
        return currentScene;
    }

    /**
     * Legt die Fenstergröße fest, welche die Oberfläche haben soll.
     *
     * @param width Gibt die Breite an.
     * @param height Gibt die Höhe an.
     */
    void setWindowSizes(double width, double height) {
        windowWidth = width;
        windowHeight = height;
        root.setPrefSize(windowWidth, windowHeight);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
}
