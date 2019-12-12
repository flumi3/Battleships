package gui;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;
import logic.Feld;
import logic.GameSaver;
import network.*;
import sun.nio.ch.Net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * Menü in welchem der Nutzer ein Online-Spiel hosten kann.
 */
public class MultiPlayerHost extends Menu {
    private double windowWidth;
    private double windowHeight;

    /**
     * Dient zur Überprüfung ob bereits auf Host Game gedrückt wurde oder nicht, um zu vermeiden dass mehrere Server
     * erzeugt werden und Fehler entstehen.
     */
    private int counter;

    /**
     * Feldgröße eines geladenen Spielstands.
     */
    private int loadedSize;

    private MultiPlayerMenu mpMenu;
    private Scene currentScene;

    /**
     * Die Szene die vor dieser angezeigt wurde.
     */
    private Scene lastScene;

    private Thread connect;
    private Server NetPlayer;
    private volatile boolean stopThread;

    private Pane root;
    private VBox menuBox;
    private Line line;
    private ComboBox comboBox;
    private Text ipAddress;

    /**
     * Status des aktuellen Stands beim Hosten. Gibt beispielsweise an, dass nach Spielern gesucht wird oder noch
     * Spielsettings vorgenommen werden müssen.
     */
    private Text status;

    private MultiPlayer mp; // Instance of SinglePlayer
    private Scene mpScene; // Scene which is created if you start the single player game

    private static int fieldSize; // Actual field size after selecting

    /**
     * Gibt an ob die Spielfeldgröße in der ComboBox ausgewählt wurde.
     */
    private boolean fieldSizeSelected;

    /**
     * Um zu verhindern, dass die GUI einfriert während auf eine Nachricht gewartet wird, wird auch hier ein Task verwendet.
     * Es wird der Server Socket per create Socket erstellt. Wenn dieser erfolgreich war wird die Szene gewechselt.
     */
    private void createSocketTask() {


         Task Socket = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                NetPlayer.createSocket();
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if(stopThread)
                    return;
                else {
                    if (NetPlayer != null)
                        if (NetPlayer.getSs() != null)
                            BattleshipsApp.getStage().setScene(mpScene); // Switch scenes
                }
            }
        };
        connect = new Thread(Socket);
        connect.start();
    }

    /**
     * Menü-Punkte bestehend aus einem String, welcher den Name des Menü-Punktes darstellt und einem Runnable, in welchem
     * der Code steht, der ausgeführt werden soll, nachdem auf ein Menü-Punkt geklickt wurde. Basierend auf dieser Liste
     * werden die MenuItems erstellt. Somit wurden hier personalisierte Buttons erstellt.
     *
     * Beim drücken des Start Buttons/Load Game wird ein neuer Server erstellt und der Task @Socket dem in @connect neu
     * erstellten Thread zugewiesen. Dieser wird anschließend gestartet. Soll ein Spiel geladen werden, werden hier die
     * Spielstände geholt und ihren Attributen zugewiesen.
     */
    private List<Pair<String, Runnable>> menuData = Arrays.asList(
            new Pair<String, Runnable>("START GAME", () -> {
                checkSelection((String) comboBox.getValue());

                if (fieldSizeSelected && counter == 0) { // Only execute this code once to avoid errors (counter)
                    status.setText("SEARCHING FOR PLAYERS...");
                    stopThread = false;
                    this.NetPlayer = new Server(5000, "localhost", "SIZE " + fieldSize);
                    if(NetPlayer.getSocket() == null)
                        createSocketTask();
                    mp.setNetPlayer(this.NetPlayer);
                    mp.setWindowSizes(root.getWidth(), root.getHeight());
                    if (mpScene == null)
                        mpScene = mp.multiPlayerLayout(); // Initalisiert die Szene erst hier, da nun field size bekannt

                    super.stopMenuMusic(); // Stop menu music
                    mp.setGameMusicPlayer(super.getGameMusicPlayer());
                    super.startGameMusic(); // start game music
                    counter++;
                }}),
            new Pair<String, Runnable>("LOAD GAME", () -> {

                if (counter == 0 ) { // Only load game if the user hasnt tried hosting a game before (counter)
                    super.startClickSound();
                    GameSaver gameOpener = new GameSaver();
                    Feld enemyField = null;
                    Feld playerField = null;


                    // Load the fields that have been saved
                    try {
                        Feld[] loadedFields = gameOpener.loadFile();
                        playerField = loadedFields[0];
                        enemyField = loadedFields[1];
                        loadedSize = playerField.size();
                        mp.setPlayerField(playerField);
                        mp.setEnemyField(enemyField);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    /**
                     * Spezialfall: Laden eines Spielstands. Es wird der überladene Konstruktor des Servers aufgerufen.
                     * Anschließend, wie bei Start Game auch der Task in einem neuen Thread gestartet.
                     * Da der Server erst hier existiert, wird er der Szene MultiPlayer zugewiesen. Diese benötigt den
                     * Server später selbst zur Kommunikation.
                     */
                    status.setText("SEARCHING FOR PLAYERS...");
                    NetPlayer = new Server(5000, "localhost", "SIZE " + playerField.size(), "LOAD " + gameOpener.getLoadedName());
                    mp.setNetPlayer(this.NetPlayer);
                    createSocketTask();
                    mp.setWindowSizes(windowWidth, windowHeight);
                    if (mpScene == null)
                        mpScene = mp.multiPlayerLayout(); // Initalisiert die Szene erst hier, da nun field size bekannt
                    super.stopMenuMusic(); // Stop menu music
                    mp.setGameMusicPlayer(super.getGameMusicPlayer());
                    super.startGameMusic(); // start game music
                    counter++;
                }
            }),

            new Pair<String, Runnable>("BACK", () -> {

                    super.startClickSound();
                    // Reset the game settings
                    status.setText("SET UP GAME SETTINGS");
                    fieldSizeSelected = false;
                    // Switch scenes
                    counter = 0;
                    mpMenu.setWindowSizes(root.getWidth(), root.getHeight());
                    BattleshipsApp.getStage().setScene(lastScene);

            })
    );

    /**
     * Initalisiert wichtige Attribute und Instanzen die am Anfang gebraucht werden.
     */
    MultiPlayerHost() {
        root = super.root;
        menuBox = new VBox();
        fieldSizeSelected = false;
        mp = new MultiPlayer("HOST");
    }

    /**
     * Erzeugt die Szene, die der Nutzer sieht, wenn er ein Spiel hosten möchte. Diese besteht aus den drei Menü Reiter
     * "Start Game", "Load Game" und "Back" so wie einer ComboBox in der die Feldgröße festgelegt wird. Außerdem werden
     * hier die IP-Adresse und Port angezeigt, welche der Client zum Aufbauen der Verbindung benötigt. Durch ein Statusfeld
     * wird dem Host mitgeteilt ob noch Spieleinstellungen wie die Feldgröße auswählen muss oder ob bereits andere
     * Spieler beitreten können.
     *
     * @return Die erzeugte Szene.
     */
    Scene MultiPlayerHostLayout() {
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
        super.addTitle("HOST GAME", 38);

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

        // Create label for ip address and game status
        Text ipLbl = new Text("HOST IP:");
        ipLbl.setFont(Font.font("Times New Roman", 20));
        ipLbl.setFill(Color.WHITE);
        ipLbl.setEffect(new DropShadow(20, Color.BLACK));

        Text portLbl = new Text("HOST PORT:");
        portLbl.setFont(Font.font("Times New Roman", 20));
        portLbl.setFill(Color.WHITE);
        portLbl.setEffect(new DropShadow(20, Color.BLACK));

        Text port = new Text("5000");
        port.setFont(Font.font("Times New Roman", 20));
        port.setFill(Color.WHITE);
        port.setEffect(new DropShadow(20, Color.BLACK));

        ipAddress = new Text();
        ipAddress.setFont(Font.font("Times New Roman", 20));
        ipAddress.setFill(Color.WHITE);
        ipAddress.setEffect(new DropShadow(20, Color.BLACK));
        try {
            ipAddress.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Text statusLbl = new Text("STATUS:");
        statusLbl.setFont(Font.font("Times New Roman", 20));
        statusLbl.setFill(Color.WHITE);
        statusLbl.setEffect(new DropShadow(20, Color.BLACK));

        status = new Text("SET UP GAME SETTINGS");
        status.setFont(Font.font("Times New Roman", 20));
        status.setFill(Color.WHITE);
        status.setEffect(new DropShadow(20, Color.BLACK));

        // Create HBox for the labels
        HBox addressBox = new HBox(20);
        HBox portBox = new HBox(20);
        HBox statusBox = new HBox(20);
        addressBox.getChildren().addAll(ipLbl, ipAddress);
        portBox.getChildren().addAll(portLbl, port);
        statusBox.getChildren().addAll(statusLbl, status);

        // Create VBox for the HBoxes
        VBox statusHolder = new VBox(20);
        statusHolder.getChildren().addAll(addressBox, portBox, statusBox);
        statusHolder.setTranslateX(windowWidth / 2 - 95);
        statusHolder.setTranslateY(windowHeight / 3 + 260);
        root.getChildren().addAll(statusHolder);

        // Add listener for the horizontal placement
        root.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double width = (double) newWidth;
            statusHolder.setTranslateX(width / 2 - 95);
        });
        // Add listener for the vertical placement
        root.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            double height = (double) newHeight;
            statusHolder.setTranslateY(height / 3 + 260);
        });

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

    void setMpMenu(MultiPlayerMenu mp) {
        this.mpMenu = mp;
    }

    void setWindowSizes(double width, double height) {
        windowWidth = width;
        windowHeight = height;
        root.setPrefSize(windowWidth, windowHeight);
    }

    static int getFieldSize() {
        return fieldSize;
    }

}
