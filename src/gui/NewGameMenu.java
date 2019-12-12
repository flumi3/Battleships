package gui;

import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * Hier legt der Nutzer die Einstellungen für das neue Spiel fest. Möchte er ein neues Spiel erzeugen wählt er hier
 * dann die Feldgröße aus und kann das Spiel anschließend starten.
 */
public class NewGameMenu extends Menu {
    private double windowWidth;
    private double windowHeight;
    private SinglePlayerMenu spMenu;
    private MainMenu mainMenu;
    private Scene mmScene;

    private Scene currentScene;
    private Scene lastScene;

    private Pane root;
    private VBox menuBox;
    private Line line;
    private ComboBox comboBox;

    private SinglePlayer sp; // Instance of SinglePlayer
    private Scene spScene; // Scene which is created if you start the single player game

    private static int fieldSize; // Actual field size after selecting
    private boolean fieldSizeSelected; // Tells if the field size has been chosen or not

    /**
     * Menü-Punkte bestehend aus einem String, welcher den Name des Menü-Punktes darstellt und einem Runnable, in welchem
     * der Code steht, der ausgeführt werden soll, nachdem auf ein Menü-Punkt geklickt wurde. Basierend auf dieser Liste
     * werden die MenuItems erstellt. Somit wurden hier personalisierte Buttons erstellt.
     */
    private List<Pair<String, Runnable>> menuData = Arrays.asList(
            new Pair<String, Runnable>("START GAME", () -> {
                checkSelection((String) comboBox.getValue());
                if (fieldSizeSelected) {
                    sp.setWindowSizes(root.getWidth(), root.getHeight());

                    if (spScene == null)
                        spScene = sp.singlePlayerLayout(); // Initalisiert die Szene erst hier, da nun field size bekannt
                    super.stopMenuMusic(); // Stop menu music
                    super.startGameMusic(); // start game music

                    boolean isPlaying = super.isPlaying();
                    sp.setGameMusicPlayer(super.getGameMusicPlayer());
                    if (isPlaying)
                        sp.setPlaying(true);
                    else
                        sp.setPlaying(false);

                    BattleshipsApp.getStage().setScene(spScene); // Switch scenes
                }}),
            new Pair<String, Runnable>("BACK", () -> {
                spMenu.setWindowSizes(root.getWidth(), root.getHeight());
                BattleshipsApp.getStage().setScene(lastScene);
            })
    );

    /**
     * Initalisiert wichtige Attribute und Instanzen.
     */
    NewGameMenu() {
        root = super.root;
        menuBox = new VBox();

        fieldSizeSelected = false;
        sp = new SinglePlayer();
    }

    /**
     * Erzeugt Szene, welche der Nutzer sieht, wenn er ein neues Spiel erzeugen möchte. Diese besteht aus Menü Punkten
     * zum Starten des Spiels oder um zur letzen Szene zurück zu kehren. Außerdem enthält sie eine ComboBox in welcher
     * der Spieler die Feldgröße für das Spiel festlegt.
     *
     * @return Die erzeugte Szene.
     */
    Scene newGameMenuLayout() {
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

        // Set Background
        super.addBackground("MenuBackground.png");

        // Set Title
        super.addTitle("SINGLE PLAYER", 38);

        // Create boxes in which you can select the field size
        comboBox = new ComboBox();
        comboBox.getItems().addAll(
                "5x5", "6x6", "7x7", "8x8", "9x9", "10x10", "11x11", "12x12", "13x13", "14x14", "15x15", "16x16",
                "17x17", "18x18", "19x19", "20x20", "21x21", "22x22", "23x23",
                "24x24", "25x25", "26x26", "27x27", "28x28", "29x29", "30x30"
        );
        comboBox.setStyle("-fx-border-with: 0.5px;" +
                "-fx-border-radius: 5px;" +
                "-fx-font-size: 14px;" +
                "-fx-font-family: 'Times New Roman';" +
                "-fx-background-color: #7bb2f0;" +
                "-fx-prompt-text-fill: #ffffff;"
        );
        comboBox.setPrefSize(200, 30);
        comboBox.setPromptText("FIELD SIZE");
        menuBox.getChildren().add(comboBox);

        // Add line
        line = super.addLine(root.getWidth() / 2 - 100, root.getHeight() / 3 + 50);

        // Add menu
        super.addMenu(menuBox);

        // Start animation
        super.startAnimation(menuBox, line);

        // Create new Scene
        currentScene = new Scene(root);
        return currentScene;
    }

    /**
     * Überprüft ob die Feldgröße ausgewählt wurde und weißt diese dem Parameter fieldSize zu.
     *
     * @param s Die in der CheckBox ausgewählte Größe des Spielfeldes.
     */
    private void checkSelection(String s) {
        // If field size and difficulty have been selected, the scene switches to the field scene
        if (s != null) {
            fieldSizeSelected = true;
            switch (s) {
                case "5x5":
                    fieldSize = 5;
                    break;
                case "6x6":
                    fieldSize = 6;
                    break;
                case "7x7":
                    fieldSize = 7;
                    break;
                case "8x8":
                    fieldSize = 8;
                    break;
                case "9x9":
                    fieldSize = 9;
                    break;
                case "10x10":
                    fieldSize = 10;
                    break;
                case "11x11":
                    fieldSize = 11;
                    break;
                case "12x12":
                    fieldSize = 12;
                    break;
                case "13x13":
                    fieldSize = 13;
                    break;
                case "14x14":
                    fieldSize = 14;
                    break;
                case "15x15":
                    fieldSize = 15;
                    break;
                case "16x16":
                    fieldSize = 16;
                    break;
                case "17x17":
                    fieldSize = 17;
                    break;
                case "18x18":
                    fieldSize = 18;
                    break;
                case "19x19":
                    fieldSize = 19;
                    break;
                case "20x20":
                    fieldSize = 20;
                    break;
                case "21x21":
                    fieldSize = 21;
                    break;
                case "22x22":
                    fieldSize = 22;
                    break;
                case "23x23":
                    fieldSize = 23;
                    break;
                case "24x24":
                    fieldSize = 24;
                    break;
                case "25x25":
                    fieldSize = 25;
                    break;
                case "26x26":
                    fieldSize = 26;
                    break;
                case "27x27":
                    fieldSize = 27;
                    break;
                case "28x28":
                    fieldSize = 28;
                    break;
                case "29x29":
                    fieldSize = 29;
                    break;
                case "30x30":
                    fieldSize = 30;
                    break;
                default:
                    fieldSize = -1;
            }
        } else {
            fieldSizeSelected = false;
        }
    }

    void setLastScene(Scene s) {
        lastScene = s;
    }

    void setWindowSizes(double width, double height) {
        windowWidth = width;
        windowHeight = height;
        root.setPrefSize(windowWidth, windowHeight);
    }

    void setSpMenu(SinglePlayerMenu sp) {
        this.spMenu = sp;
    }

    static int getFieldSize() {
        return fieldSize;
    }

    void setMainMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }
}
