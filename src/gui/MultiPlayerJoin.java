package gui;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import logic.Feld;
import logic.GameSaver;
import network.Client;
import sun.nio.ch.Net;

import java.io.IOException;

/**
 * Menü, in welchem der Nutzer die Möglichkeit hat, einem Online-Spiel beizutreten.
 */
public class MultiPlayerJoin extends Menu {
    private double windowWidth;
    private double windowHeight;
    private MultiPlayerMenu mpMenu;
    /**
     * @Client Client für das Netzwerkspiel.
     */
    private Client NetPlayer;
    /**
     * @connect der Thread connect wird für die Ausführung des Tasks in einem speraten Thread bentigt
     * Dies verhindert das Einfrieren der GUI.
     */
    private Thread connect;
    /**
     * @loadGame Wird zur Fallunterscheidung benbötigt (Neues Spiel oder Spiel laden)
     */
    String loadGame;
    private Scene currentScene;
    private Scene lastScene;
    private int counter;

    private MultiPlayer mp; // Instance of SinglePlayer
    private Scene mpScene; // Scene which is created if you start the single player game

    /**
     * Um zu verhindern, dass die GUI einfriert während auf eine Nachricht gewartet wird, wird hier ein Task verwendet.
     * Es wird der Client Socket per create Socket erstellt. Für die Möglichkeit, dass das Spiel geladen wid, wird hier
     * der Wert von LoadGame des Clients in einem String gespeichert.
     */
    private void createSocketTask() {


        Task Socket = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                NetPlayer.createSocket();
                loadGame = NetPlayer.getLoadGame();

                return null;
            }

            @Override
            /**
             * Wurde der Task erfolgreich beendet wird @clientLoadGame aufgerufen, falls ein Spiel geladen wird. Ansonsten
             * wird für die Multiplayer Szene der hier verwendete Netplayer sowie das Layout gesetzt und anschließend die
             * Szene gewechselt.
             */
            protected void succeeded() {
                super.succeeded();
                if (loadGame.contains("LOAD"))
                    clientLoadGame(loadGame);
                mp.setNetPlayer(NetPlayer);
                mpScene = mp.multiPlayerLayout();
                BattleshipsApp.getStage().setScene(mpScene);
            }
        };
        connect = new Thread(Socket);
        connect.start();
    }
    /**
     * Initalisiert Attribute und Instanzen die am Anfang gebraucht werden.
     */
    MultiPlayerJoin() {
        root = super.root;
        mp = new MultiPlayer("CLIENT");
    }

    /**
     * Erzeugt die Szene, die der Nutzer sieht, wenn er einem Online-Spiel beitreten möchte. Diese besteht aus Textfeldern
     * für IP-Adresse und Port des Hosts so wie einem Connect Button der versucht eine Verbindung zum Host aufzubauen.
     *
     * @return Die erzeugte Szene.
     */
    Scene multiPlayerJoinLayout() {
        // Set Background
        super.addBackground("MenuBackground.png");

        // Set Title
        super.addTitle("JOIN GAME", 38);

        // Create labels and a text field for entering the ip address of the host
        Text ipLbl = new Text("HOST IP:");
        ipLbl.setFont(Font.font("Times New Roman", 20));
        ipLbl.setFill(Color.WHITE);
        ipLbl.setEffect(new DropShadow(20, Color.BLACK));

        TextField ipField = new TextField();
        ipField.setPrefHeight(27);

        Text statusLbl = new Text("STATUS:");
        statusLbl.setFont(Font.font("Times New Roman", 20));
        statusLbl.setFill(Color.WHITE);
        statusLbl.setEffect(new DropShadow(20, Color.BLACK));

        Text status = new Text("NOT CONNECTED!"); // connection failed!, connected!
        status.setFont(Font.font("Times New Roman", 20));
        status.setFill(Color.WHITE);
        status.setEffect(new DropShadow(20, Color.BLACK));

        Text portLbl = new Text("PORT:");
        portLbl.setFont(Font.font("Times New Roman", 20));
        portLbl.setFill(Color.WHITE);
        portLbl.setEffect(new DropShadow(20, Color.BLACK));

        TextField portField = new TextField();
        portField.setPrefHeight(27);

        // Create connect button
        Button connectButton = new Button("CONNECT");
        connectButton.setStyle("-fx-font-family: 'Times New Roman';" +
                "-fx-font-size: 14px;" +
                "-fx-border-radius: 1px;" +
                "-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);"
        );

        /**
         * Beim drücken des Connect Buttons wird ein neuer Client erstellt und der Task @Socket dem in @connect neu
         * erstellten Thread zugewiesen. Dieser wird anschließend gestartet.
         */
        connectButton.setOnAction(e -> {

            NetPlayer = new Client(Integer.parseInt(portField.getCharacters().toString()), ipField.getCharacters().toString());
            createSocketTask();
            mp.setGameMusicPlayer(super.getGameMusicPlayer());
            mp.setWindowSizes(root.getWidth(), root.getHeight());
            counter++;

        });

        // Create back button
        Button backButton = new Button("BACK");
        backButton.setTranslateX(windowWidth / 2 - 25);
        backButton.setTranslateY(windowHeight / 3 + 200);
        backButton.setStyle("-fx-font-family: 'Times New Roman';" +
                "-fx-font-size: 16px;" +
                "-fx-border-radius: 1px;" +
                "-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);"
        );
        root.getChildren().add(backButton);
        backButton.setOnAction(e -> {
            super.startClickSound();
            mpMenu.setWindowSizes(root.getWidth(), root.getHeight());
            BattleshipsApp.getStage().setScene(lastScene);
        });
        // Add listener for the horizontal placement
        root.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double width = (double) newWidth;
            backButton.setTranslateX((width / 2) - 25);
        });
        // Add listener for the vertical placement
        root.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            double height = (double) newHeight;
            backButton.setTranslateY(height / 3 + 300);
        });

        GridPane lblPane = new GridPane();
        lblPane.setHgap(40);
        lblPane.setVgap(15);
        lblPane.add(ipLbl, 0, 0);
        lblPane.add(ipField, 1, 0);
        lblPane.add(connectButton, 2, 0);
        lblPane.add(portLbl, 0, 1);
        lblPane.add(portField, 1, 1);
        lblPane.add(statusLbl, 0, 3);
        lblPane.add(status, 1, 3);

        // Create VBox for ipBox and statusBox
        lblPane.setTranslateX(windowWidth / 2 - 211);
        lblPane.setTranslateY(windowHeight / 3 + 80);
        root.getChildren().add(lblPane);

        // Add listener for the horizontal placement
        root.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double width = (double) newWidth;
            lblPane.setTranslateX((width / 2) - 170);
        });
        // Add listener for the vertical placement
        root.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            double height = (double) newHeight;
            lblPane.setTranslateY(height / 3 + 80);
        });

        currentScene = new Scene(root);
        return currentScene;
    }

    void setWindowSizes(double width, double height) {
        windowWidth = width;
        windowHeight = height;
        root.setPrefSize(windowWidth, windowHeight);
    }

    void setLastScene(Scene s) {
        lastScene = s;
    }

    void setMpMenu(MultiPlayerMenu mp) {
        this.mpMenu = mp;
    }

    /**
     * Funktion wird nur im Spezialfall von Spiel Laden aufgerufen.
     * @param loadGame in diesm Parameter befindet sich der Name der Datei, die geöffnet werden soll.
     *
     * Zu Beginn wird der Name aus dem String per Split isoliert. Um dann die richtige Datei öffnen zu können wird
     * ein neues GameSaver Objekt erstellt und sowie Gegner- als auch Spielerfeld initalisert.
     * Anschließend wird versucht die ausgewählte Datei zu öffnen und die gespeicherten Felder auszulesen und wieder
     * zuzuweisen.
     * Letztendlich wird dann die das size Attribut des Clients gesetzt, da diese ansonten leer bleibt und an
     * anderer Stelle zu Problemen führt.
     */
    void clientLoadGame(String loadGame){
        String[] sArray = loadGame.split(" ");
        GameSaver gameOpener = new GameSaver();
        Feld playerField = null;
        Feld enemyField = null;

        // Load the fields that have been saved
        try {
            Feld[] loadedFields = gameOpener.loadFile(sArray[1]);
            playerField = loadedFields[0];
            enemyField = loadedFields[1];

            mp.setEnemyField(enemyField);
            mp.setPlayerField(playerField);
            NetPlayer.setSize("SIZE " + playerField.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
