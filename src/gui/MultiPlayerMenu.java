package gui;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * Menü, in welchem der Nutzer entscheidet, ob er ein Online-Spiel hosten, oder ob er einem beitreten möchte.
 */
public class MultiPlayerMenu extends Menu {
    private double windowWidth;
    private double windowHeight;
    private MainMenu mainMenu;

    private Scene currentScene;
    private Scene lastScene; // Necessary for the back action

    private MultiPlayerHost mpHost;
    private Scene mpHostScene;
    private MultiPlayerJoin mpJoin;
    private Scene mpJoinScene;

    Pane root;
    VBox menuBox;
    Line line;

    /**
     * Menü-Punkte bestehend aus einem String, welcher den Name des Menü-Punktes darstellt und einem Runnable, in welchem
     * der Code steht, der ausgeführt werden soll, nachdem auf ein Menü-Punkt geklickt wurde. Basierend auf dieser Liste
     * werden die MenuItems erstellt. Somit wurden hier personalisierte Buttons erstellt.
     */
    private List<Pair<String, Runnable>> menuData = Arrays.asList(
            new Pair<String, Runnable>("HOST GAME", () -> {
                super.startClickSound();
                mpHost.setWindowSizes(root.getWidth(), root.getHeight());
                mpHost.setLastScene(currentScene);
                mpHost.setMpMenu(this); // Übergebe aktuelle Instanz, benötigt für die Fenstergröße der Back-Operation
                BattleshipsApp.getStage().setScene(mpHostScene);
            }),
            new Pair<String, Runnable>("JOIN GAME", () -> {
                super.startClickSound();
                mpJoin.setWindowSizes(root.getWidth(), root.getHeight());
                mpJoin.setLastScene(currentScene);
                mpJoin.setMpMenu(this); // Übergebe aktuelle Instanz, benötigt für die Fenstergröße der Back-Operation
                BattleshipsApp.getStage().setScene(mpJoinScene);
            }),
            new Pair<String, Runnable>("BACK", () -> {
                super.startClickSound();
                mainMenu.setWindowSizes(root.getWidth(), root.getHeight());
                BattleshipsApp.getStage().setScene(lastScene);
            })
    );

    /**
     * Initialisiert wichtige Attribute und Instanzen die am Anfang benötigt werden und setzt die Fenstergrößen für
     * das Join- und Host-Menü.
     */
    MultiPlayerMenu() {
        root = super.root;
        menuBox = new VBox();

        mpHost = new MultiPlayerHost();
        mpHost.setWindowSizes(1280, 720);
        mpHostScene = mpHost.MultiPlayerHostLayout();

        mpJoin = new MultiPlayerJoin();
        mpJoin.setWindowSizes(1280, 720);
        mpJoinScene = mpJoin.multiPlayerJoinLayout();
    }

    /**
     * Erzeugt die Szene, welche der Nutzer sieht, wenn er im Hauptmenü auf den Reiter "Multiplayer" klickt. Bestehend
     * aus den drei Menü Punkten "Host Game", "Join Game" und "Back".
     *
     * @return Die erzeugte Szene.
     */
    public Scene multiPlayerMenuLayout() {
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

        // Add background
        super.addBackground("MenuBackground.png");

        // Add title
        super.addTitle("MULTIPLAYER", 38);

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
