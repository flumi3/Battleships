package gui;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import logic.*;
import network.Client;
import network.Server;


import java.io.IOException;

import static logic.FieldState.*;

/**
 * Repräsentiert das eigentliche Spiel im Multi Player Modus. Hier werden Schiffe platziert, geschossen und gegen
 * den Online-Gegnger gespielt.
 */
public class MultiPlayer extends Menu {
    private double windowWidth;
    private double windowHeight;

    private int counter;
    private int readyCounter;

    private BorderPane root;
    private Thread waitMessage;
    private RealPlayer player; // Instance of Spieler class

    // Audio for end screen
    private static Media winnerMusic;
    private static Media looserMusic;
    private static MediaPlayer winnerPlayer;
    private static MediaPlayer looserPlayer;
    private static MediaPlayer gameMusicPlayer;

    /**
     * Ist false bis der Spieler surrendered
     */
    private boolean hasSurrendered;

    /**
     * Ist false bis der Shoot-Button gedrückt wird.
     */
    private boolean shoot;

    /**
     * Ist false bis ein Schiff platziert werden soll.
     */
    private boolean place;

    /**
     * Größe des Spielfeldes.
     */
    private int fieldSize;

    /**
     * Größe des derzeitigen Schiffs, welches man platzieren möchte.
     */
    private int shipSize;

    /**
     * Zellen-Array für das Spielerfeld, um auf die einzelnen Zellen per Koordinaten zugreifen zu können.
     */
    private Cell[][] cellArrayPlayer;

    /**
     * Zellen-Array für das gegnerische Spielfeld, um auf die einzelnen Zeilen per Koordinaten zugreifen zu können.
     */
    private Cell[][] cellArrayEnemy;

    /**
     * Enthält die ipAdresse über welche die Verbindung besteht.
     */
    private String ipAddress;

    /**
     * Enthält den Port über welchen kommuniziert wird.
     */
    private int port;

    /**
     * Instanz des Servers.
     */
    private Server server;

    /**
     * Ergebniswert für einen Schuss. 0 = Wasser, 1 = Treffer, -1 = Other.
     */
    private int shotResult;

    /**
     * Ist der Gegner am Zug wird diese Variable auf true gesetzt. Danach wieder auf false.
     */
    private boolean gegnerIstDran;

    /**
     * Gibt an ob man beim Online Spiel der Host ist oder nicht.
     */
    private boolean isHost;

    /**
     * Enthält das Feld des Gegners in welchem die FieldStates eingetragen sind.
     */
    private Feld enemyField;

    /**
     * Enthält das Feld des Spielers in welchem die FieldStates eingetragen sind.
     */
    private Feld playerField;

    private KIGegner ki;

    /**
     * Box welche die Spielerzellen enthält.
     */
    private VBox playerRows;

    /**
     * Box welche die Gegnerzellen enthält.
     */
    private VBox enemyRows;

    /**
     * Gibt an wie viele Schiffe der Größe 2 noch übrig zum platzieren sind.
     */
    private Text size2LeftLbl;

    /**
     * Gibt an wie viele Schiffe der Größe 3 noch übrig zum platzieren sind.
     */
    private Text size3LeftLbl;

    /**
     * Gibt an wie viele Schiffe der Größe 4 noch übrig zum platzieren sind.
     */
    private Text size4LeftLbl;

    /**
     * Gibt an wie viele Schiffe der Größe 5 noch übrig zum platzieren sind.
     */
    private Text size5LeftLbl;

    /**
     * Button zum Schießen.
     */
    private Button shootButton;

    /**
     * Wählt ein Schiff der Größe 2 für die Platzier-Operation aus.
     */
    private Button boat2Button;

    /**
     * Wählt ein Schiff der Größe 3 für die Platzier-Operation aus.
     */
    private Button boat3Button;

    /**
     * Wählt ein Schiff der Größe 4 für die Platzier-Operation aus.
     */
    private Button boat4Button;

    /**
     * Wählt ein Schiff der Größe 5 für die Platzier-Operation aus.
     */
    private Button boat5Button;

    /**
     * Platziert die Schiffe zufällig auf dem Spielfeld des Spielers.
     */
    private Button placeRandomButton;

    /**
     * Setzt das Spielfeld des Spielers zurück, damit Schiffe nochmal neu platziert werden können.
     */
    private Button clearButton;

    /**
     * Dreht das zuletzt platzierte Schiff der Größe 2
     */
    private Button turn2Button;

    /**
     * Dreht das zuletzt platzierte Schiff der Größe 3
     */
    private Button turn3Button;

    /**
     * Dreht das zuletzt platzierte Schiff der Größe 4
     */
    private Button turn4Button;

    /**
     * Dreht das zuletzt platzierte Schiff der Größe 5
     */
    private Button turn5Button;

    /**
     * Lässt die KI für einen selbst spielen.
     */
    private Button kiPlayButton;

    /**
     * Zur Fallunterscheidung, ob ein Spiel geladen wurde, da dann die Zustände der Knöpfe und des Spielfelds
     * wiederhergestellt werden müssen
     */
    private boolean loadGameSettings = false;

    private Button saveButton;
    private Button surrenderButton;

    /**
     * Netplayer als Client, da sowohl Server als auch Client in diesem Attribut aufgrund von Vererbung gespeichert
     * werden können.
     */
    private Client NetPlayer;

    /**
     * Ein Service, der an mehrern Stellen im Programm verwendet wird, mit leicht abgeänderten Auswirkungen.
     * Prinzipiell wird er Verwendet, um auf eine Bestimmte Nachricht(OK) zu warten und anschließend eine
     * bestimmte Aktion auszuführen.
     */
    private Service<String> gameService;

    /**
     * zur Abfrage, wird gesetzt wenn der Spieler aufgegeben hat.
     */

    private volatile boolean setStopTasks = false;

    /**
     * Da man innerhalb eines Tasks keinen Zugriff auf die Cell hat, muss nach jedem drücken einer Zelle dieses Attribut
     * gesetzt werden. Sozusagen ein Zwischenspeicher der zuletzt gewählten Zelle. Wird für die Verarbeitung innerhalb
     * des playerTurnTasks benötigt.
     */
    private Cell tempCell;

    /**
     * Dieses Attribut wird von der KI zum Spielen benötigt.
     */
    private boolean versenkt;

    /**
     * Instanz der Enumeration Fieldstate.
     */
    private FieldState fieldState;
    int zeileKI;
    int spalteKI;

    /**
     * Initialisiert Attribute so wie wichtige Instanzen für den Start.
     *
     * @param role
     */
    MultiPlayer(String role) {
        winnerMusic = new Media(getClass().getResource("GameWonMusic.mp3").toExternalForm());
        looserMusic = new Media(getClass().getResource("GameLostMusic.mp3").toExternalForm());

        player = new RealPlayer();
        ki = new KIGegner();
        root = new BorderPane();

        if (role.equals("HOST"))
            isHost = true;
        else
            isHost = false;
        //server = new Server(port, ipAddress);
        initServices();
    }

    /**
     * Initalisierung des gameService. Es wird auf eine Nachricht gewartet.
     * Der Handlungsablauf bei Erfolg wird festgelegt(Sende ein "OK" zurück)
     */
    void initServices() {
        this.gameService = new Service<String>() {

            @Override
            protected Task createTask() {
                Task playing = new Task<String>() {
                    @Override
                    protected String call() throws Exception {

                        return waitForEnemyTurn();
                    }


                };
                return playing;
            }

        };


        gameService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                NetPlayer.outMessage("OK");
            }
        });


    }

    /**
     * Funktion wird aufgerufen, wenn der Spieler am Zug ist.
     * Es wird ein Task erstellt, welcher auf eine Nachricht wartet.
     * Dann wird der Handlungsablauf für den Fall des Erfolgs festgelegt.
     * Da dieser Task öfters benötigt wird und man einen Task nur ein mal verwenden kann, wird bei jedem Funktionsaufruf
     * ein neuer Task erstellt.
     * Der Spielablauf ist nach Protokoll, ähnlich wie im Singleplayer.
     * Die erhaltene Nachricht wird ausgelesen, und anhand dessen, was in dieser steht wird entsprechend gehandelt.
     * (Zelle deaktiviert, färben, entschieden wer am Zug ist, entsprechende Antwort auf die Nachricht versenden,...)
     * Dann wird ein neuer Thread erstellt, diesem wird der Task zugewiesen und gestartet.
     * Falls der Gegner am Zug ist, wird enemyTurnTask() aufgerufen. Wenn nicht darf der Spieler erneut
     * schießen und startet damit erneut die Funktion playerTurnTask().
     *
     * Das abarbeiten des Tasks in einem seperaten Thread verhindert das einfrieren der GUI.
     */
    private void playerTurnTask() {

        Task playerTurnTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                //changeRowState(true);
                return waitForEnemyTurn();
            }
        };

        playerTurnTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            public synchronized void setEnemyRowsStates(boolean able) {
                enemyRows.setDisable(able);
            }

            @Override
            public void handle(WorkerStateEvent event) {
                if(setStopTasks)
                    return;
                if(playerTurnTask.getValue() == null){
                    Boolean answer = OkBox.display("", "Your enemy has quit the game!");
                    if (answer) {
                        NetPlayer.closeSockets();
                        MultiPlayer.super.stopGameMusic(); // Stop menu music
                        MainMenu mm = new MainMenu();
                        mm.setWindowSizes(root.getWidth(), root.getHeight());
                        mm.mainMenuLayout();

                        BattleshipsApp.getStage().setScene(mm.getCurrentScene());
                        return;
                    }
                }
                String shot = playerTurnTask.getValue().toString();
                if (shot.equals("ANSWER 0")) {
                    tempCell.setFill(Color.AQUA);
                    gegnerIstDran = true;
                    tempCell.setDisable(true);
                    enemyField.setFieldstate(tempCell.getCellY(), tempCell.getCellX(), WATER_HIT);
                    NetPlayer.outMessage("OK");
                    // Schiffstreffer
                } else if (shot.equals("ANSWER 1")) {
                    enemyField.decrementCounter();
                    tempCell.setFill(Color.RED);
                    tempCell.setDisable(true);
                    enemyField.setFieldstate(tempCell.getCellY(), tempCell.getCellX(), SHIP_HIT);
                    gegnerIstDran = false;
                } else if (shot.equals("ANSWER 2")) {// Wurde das Schiff versenkt, färbe es dunkelrot
                    enemyField.decrementCounter();
                    enemyField.setFieldstate(tempCell.getCellY(), tempCell.getCellX(), SHIP_HIT);
                    tempCell.setDisable(true);
                    tempCell.setFill(Color.RED);
                    player.versenktInfo(enemyField, tempCell.getCellY(), tempCell.getCellX());
                    int[][] versenktesSchiff = player.getVersenktesSchiff(); // Koordinaten des versenkten Schiffes //xxxxxxx
                    int spalte;
                    int zeile;
                    for (int i = 0; i < versenktesSchiff.length; i++) {
                        zeile = versenktesSchiff[i][0];
                        spalte = versenktesSchiff[i][1];
                        if (zeile != -1)
                            cellArrayEnemy[zeile][spalte].setFill(Color.DARKRED);
                    }
                    gegnerIstDran = false;

                    // Check if the game is over
                    if (enemyField.gameOver())
                        gameOver("you");
                    if (playerField.gameOver())
                        gameOver("enemy");
                } else if (shot.contains("SAVE")) {
                    String[] sArray = shot.split(" ");
                    String fileName = sArray[1];
                    Feld[] fieldArray = {playerField, enemyField};

                    GameSaver gameSaver = new GameSaver();
                    gameSaver.saveFile(fieldArray, fileName);

                    Boolean answer = ConfirmBox.display("", "Resume game?");
                    if (answer)
                        Platform.exit();
                }
                saveButton.setDisable(gegnerIstDran);
                setEnemyRowsStates(gegnerIstDran);
                setFieldColoring();
            }
        });
        waitMessage = new Thread(playerTurnTask);
        waitMessage.start();
        playerTurnTask.onSucceededProperty();

    }

    /**
     * Funktion wird aufgerufen, wenn der Gegner am Zug ist.
     * Es wird ein Task erstellt, welcher auf eine Nachricht wartet.
     * Dann wird der Handlungsablauf für den Fall des Erfolgs festgelegt.
     * Der Spielablauf ist nach Protokoll.
     * Die erhaltene Nachricht wird ausgelesen, und anhand dessen, was in dieser steht wird entsprechend gehandelt.
     * (Zelle deaktiviert, färben, entschieden wer am Zug ist, entsprechende Antwort auf die Nachricht versenden,...)
     * Dann wird ein neuer Thread erstellt, diesem wird der Task zugewiesen und gestartet.
     * <p>
     * Das abarbeiten des Tasks in einem seperaten Thread verhindert das einfrieren der GUI.
     * <p>
     * Falls der Gegner am Zug ist, wird enemyTurnTask() aufgerufen. Wenn nicht darf der Spieler
     * schießen und startet damit die Funktion playerTurnTask().
     * <p>
     */
    private void enemyTurnTask() {

        Task enemyTurnTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return waitForEnemyTurn();
            }

        };
        enemyTurnTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public synchronized void setEnemyRowsStates(boolean able) {
                enemyRows.setDisable(able);
            }

            @Override

            public void handle(WorkerStateEvent event) {
                if(setStopTasks)
                    return;
                if(enemyTurnTask.getValue() == null){
                    Boolean answer = OkBox.display("", "Your enemy has quit the game");

                    if (answer) {

                        MultiPlayer.super.stopGameMusic(); // Stop menu music
                        MainMenu mm = new MainMenu();
                        mm.setWindowSizes(root.getWidth(), root.getHeight());
                        mm.mainMenuLayout();
                        BattleshipsApp.getStage().setScene(mm.getCurrentScene());
                        NetPlayer.closeSockets();
                        return;
                    }
                }
                String shot = enemyTurnTask.getValue().toString();
                setEnemyRowsStates(true);
                saveButton.setDisable(true);
                System.out.println(shot);
                if (shot.contains("SHOT")) {
                    String coordinates = NetPlayer.compute(shot);
                    String[] c = coordinates.split(" ");
                    int y = Integer.parseInt(c[0]);
                    int x = Integer.parseInt(c[1]);
                    shotResult = shoot(y, x);
                    if (shotResult == 0) {
                        NetPlayer.outMessage("ANSWER 0");
                        cellArrayPlayer[y][x].setFill(Color.AQUA);

                    }
                    if (shotResult == 1) {
                        cellArrayPlayer[y][x].setFill(Color.RED);
                        if (player.getVersenkt())
                            NetPlayer.outMessage("ANSWER 2");

                        else
                            NetPlayer.outMessage("ANSWER 1");

                        // Check if game is over
                        if (playerField.gameOver())
                            gameOver("enemy");
                        if (enemyField.gameOver())
                            gameOver("you");
                    }

                    gegnerIstDran = true;
                } else {
                    if (shot.contains("SAVE")) {
                        String[] sArray = shot.split(" ");
                        String fileName = sArray[1];
                        Feld[] fieldArray = {playerField, enemyField};

                        GameSaver gameSaver = new GameSaver();
                        gameSaver.saveFile(fieldArray, fileName);

                        // Ask the user if he wants to quit
                        Boolean answer = ConfirmBox.display("", "Resume game?");
                        if (answer)
                            Platform.exit();
                    }
                    if (shot.contains("OK")) {
                        gegnerIstDran = false;

                    } else {
                        NetPlayer.outMessage("OK");
                        gegnerIstDran = true;
                    }

                }
                setEnemyRowsStates(gegnerIstDran);
                saveButton.setDisable(gegnerIstDran);
                setFieldColoring();

                if (gegnerIstDran)
                    enemyTurnTask();

            }
        });
        waitMessage = new Thread(enemyTurnTask);
        waitMessage.start();
        enemyTurnTask.onSucceededProperty();

    }

    /**
     * Wird aufgerufen, wenn man die KI für sich spielen lässt und diese schießen darf.
     * Wie auch beim normalen Spieler wird der Task in einem seperaten Thread ausgeführt um die Interaktion mit der GUI
     * zu garantieren, während man auf den Zug des Gegners wartet.
     * Damit die KI ihren Zug nicht in Windeseile herunterrattert(vorallem wenn sie trifft) ist eine Verzögerung von
     * 500ms eingebaut.
     * Man lässt die KI ganz normal spielen, und versendet deren Aktionen an den Gegner.
     * Die Verarbeitung der Antwort unterscheidet sich nicht enorm von der des "echten" Spielers. Es wurde ledigich
     * eine Zeile (fieldstate = ...) hinzugefügt. Darf die KI noch mal, so wird die Funktion einfach wieder aufgerufen.
     * Andernfalls wird die Funktion enemyTurnTaskKI() aufgerufen.
     */
    private void kiTurnTask() {
        Task kiTurnTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                waitMessage.sleep(500);
                versenkt = false;
                fieldState = FieldState.WATER;
                ki.shoot(enemyField);
                zeileKI = ki.getShotY();
                spalteKI = ki.getShotX();
                NetPlayer.outMessage("SHOT " + zeileKI + " " + spalteKI);
                return waitForEnemyTurn();
            }


        };

        kiTurnTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent event) {
                if(setStopTasks)
                    return;
                if(kiTurnTask.getValue() == null){
                    Boolean answer = OkBox.display("", "Your enemy has quit the game!");

                    if (answer) {
                        NetPlayer.closeSockets();
                        MultiPlayer.super.stopGameMusic(); // Stop menu music

                        MainMenu mm = new MainMenu();
                        mm.setWindowSizes(root.getWidth(), root.getHeight());
                        mm.mainMenuLayout();

                        BattleshipsApp.getStage().setScene(mm.getCurrentScene());
                        return;
                    }
                }
                String shot = kiTurnTask.getValue().toString();
                boolean hit = false;
                if (shot.equals("ANSWER 0")) {
                    fieldState = FieldState.WATER;
                    cellArrayEnemy[zeileKI][spalteKI].setFill(Color.AQUA);
                    enemyField.setFieldstate(zeileKI, spalteKI, WATER_HIT);
                    gegnerIstDran = true;
                    NetPlayer.outMessage("OK");

                    // Schiffstreffer
                } else if (shot.equals("ANSWER 1")) {
                    fieldState = FieldState.SHIP;
                    cellArrayEnemy[zeileKI][spalteKI].setFill(Color.RED);
                    enemyField.setFieldstate(zeileKI, spalteKI, SHIP_HIT);
                    gegnerIstDran = false;
                } else if (shot.equals("ANSWER 2")) {// Wurde das Schiff versenkt, färbe es dunkelrot
                    fieldState = FieldState.SHIP;
                    versenkt = true;
                    System.out.println("VERSENK in Multiplayer: " + versenkt);
                    enemyField.setFieldstate(zeileKI, spalteKI, SHIP_HIT);
                    cellArrayEnemy[zeileKI][spalteKI].setFill(Color.RED);
                    gegnerIstDran = false;
                    player.versenktInfo(enemyField, zeileKI, spalteKI);
                    int[][] versenktesSchiff = player.getVersenktesSchiff(); // Koordinaten des versenkten Schiffes
                    int spalte;
                    int zeile;

                    for (int i = 0; i < versenktesSchiff.length; i++) {
                        zeile = versenktesSchiff[i][0];
                        spalte = versenktesSchiff[i][1];
                        if (zeile != -1)
                            cellArrayEnemy[zeile][spalte].setFill(Color.DARKRED);
                    }
                }

                hit = ki.schussVerarbeiten(enemyField, fieldState);

                // Check if game is over
                if (enemyField.gameOver())
                    gameOver("you");
                if (playerField.gameOver())
                    gameOver("enemy");

                if (versenkt) {
                    ki.versenktInfo(enemyField, zeileKI, spalteKI);
                }
                if (hit)
                    kiTurnTask();
                else
                    enemyTurnTaskKI();

            }
        });
        waitMessage = new Thread(kiTurnTask);
        waitMessage.start();
        kiTurnTask.onSucceededProperty();

    }

    /**
     * Auf die Nachricht des Gegners zu warten funktioniert bei der KI recht ähnlich, wie für einen "echten Spieler".
     * Wegen kleiner Unterschiede(z.b. muss, wenn die KI am Zug ist, die Funktion kiTurnTask aufgerufen werden) und
     * zur klaren Trennung wurde hierfür eine eigene Funktion mit eigenem Task erstellt.
     *
     * Auch hier wird der Task in einem eigenen Thread abgearbeitet, um die GUI responsiv zu halten.
     *
     */
    private void enemyTurnTaskKI() {

        Task enemyTurnTaskKI = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return waitForEnemyTurn();
            }

        };
        enemyTurnTaskKI.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override

            public void handle(WorkerStateEvent event) {
                if(setStopTasks)
                    return;
                if(enemyTurnTaskKI.getValue() == null){
                    Boolean answer = OkBox.display("", "Your enemy has quit the game!");

                    if (answer) {
                        NetPlayer.closeSockets();
                        MultiPlayer.super.stopGameMusic(); // Stop menu music

                        MainMenu mm = new MainMenu();
                        mm.setWindowSizes(root.getWidth(), root.getHeight());
                        mm.mainMenuLayout();

                        BattleshipsApp.getStage().setScene(mm.getCurrentScene());
                        return;
                    }
                }
                String shot = enemyTurnTaskKI.getValue().toString();
                System.out.println(shot);
                if (shot.contains("SHOT")) {
                    String coordinates = NetPlayer.compute(shot);
                    String[] c = coordinates.split(" ");
                    int y = Integer.parseInt(c[0]);
                    int x = Integer.parseInt(c[1]);
                    shotResult = shoot(y, x);
                    if (shotResult == 0) {
                        NetPlayer.outMessage("ANSWER 0");
                        cellArrayPlayer[y][x].setFill(Color.AQUA);

                    }
                    if (shotResult == 1) {
                        cellArrayPlayer[y][x].setFill(Color.RED);
                        if (player.getVersenkt())
                            NetPlayer.outMessage("ANSWER 2");

                        else
                            NetPlayer.outMessage("ANSWER 1");

                        // Check if game is over
                        if (playerField.gameOver())
                            gameOver("enemy");
                        if (enemyField.gameOver())
                            gameOver("you");
                    }

                    gegnerIstDran = true;
                } else {
                    if (shot.contains("SAVE")) {
                        String[] sArray = shot.split(" ");
                        String fileName = sArray[1];
                        Feld[] fieldArray = {playerField, enemyField};

                        GameSaver gameSaver = new GameSaver();
                        gameSaver.saveFile(fieldArray, fileName);

                        // Ask the user if he wants to quit
                        Boolean answer = ConfirmBox.display("", "Resume game?");
                        if (answer)
                            Platform.exit();
                    }
                    if (shot.contains("OK")) {
                        gegnerIstDran = false;

                    } else {
                        NetPlayer.outMessage("OK");
                        gegnerIstDran = true;
                    }
                }
                setFieldColoring();
                if (gegnerIstDran)
                    enemyTurnTaskKI();
                else {
                    kiTurnTask();
                }

            }
        });
        waitMessage = new Thread(enemyTurnTaskKI);
        waitMessage.start();
        enemyTurnTaskKI.onSucceededProperty();
    }

    /**
     * Erzeugt die Szene, welche der Nutzer sieht wenn er sich in einem Online-Spiel befindet. Ebenso werden hier Spieler-
     * und Gegnerfeld erzeugt falls sie nicht schon durch ein geladenes Spiel gesetzt wurden so wie alle ButtonEvents verarbeitet.
     *
     * @return Die erzeugte Szene.
     */
    Scene multiPlayerLayout() {
        this.fieldSize = NetPlayer.getSize();
        if (enemyField == null && playerField == null) {
            enemyField = new Feld(fieldSize); // Create Array for enemy field
            playerField = new Feld(fieldSize); // Create Array for player field

        } else
            loadGameSettings = true;
        cellArrayPlayer = new Cell[fieldSize][fieldSize];
        cellArrayEnemy = new Cell[fieldSize][fieldSize];

        // Set Background
        ImageView background = new ImageView(new Image(getClass().getResource("MenuBackground.png").toExternalForm()));
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        // Create Label for the place boat buttons
        Text placeBoatsLbl = new Text("PLACE YOUR SHIPS");
        placeBoatsLbl.setFont(Font.font("Times New Roman", 20));
        placeBoatsLbl.setFill(Color.WHITE);
        placeBoatsLbl.setEffect(new DropShadow(20, Color.BLACK));

        // Create field Labels
        Text enemyFieldLbl = new Text("ENEMY FIELD");
        enemyFieldLbl.setFont(Font.font("Times New Roman", 20));
        enemyFieldLbl.setFill(Color.WHITE);
        enemyFieldLbl.setEffect(new DropShadow(20, Color.BLACK));

        Text playerFieldLbl = new Text("PLAYER FIELD");
        playerFieldLbl.setFont(Font.font("Times New Roman", 20));
        playerFieldLbl.setFill(Color.WHITE);
        playerFieldLbl.setEffect(new DropShadow(20, Color.BLACK));

        // Create boats left labels
        size2LeftLbl = new Text();
        size2LeftLbl.setFont(Font.font("Times New Roman", 16));
        size2LeftLbl.setFill(Color.WHITE);
        size2LeftLbl.setEffect(new DropShadow(16, Color.BLACK));
        Integer a = playerField.getShips(3);
        size2LeftLbl.setText(a.toString() + " LEFT");

        size3LeftLbl = new Text();
        size3LeftLbl.setFont(Font.font("Times New Roman", 16));
        size3LeftLbl.setFill(Color.WHITE);
        size3LeftLbl.setEffect(new DropShadow(16, Color.BLACK));
        a = playerField.getShips(2);
        size3LeftLbl.setText(a.toString() + " LEFT");

        size4LeftLbl = new Text();
        size4LeftLbl.setFont(Font.font("Times New Roman", 16));
        size4LeftLbl.setFill(Color.WHITE);
        size4LeftLbl.setEffect(new DropShadow(16, Color.BLACK));
        a = playerField.getShips(1);
        size4LeftLbl.setText(a.toString() + " LEFT");

        size5LeftLbl = new Text();
        size5LeftLbl.setFont(Font.font("Times New Roman", 16));
        size5LeftLbl.setFill(Color.WHITE);
        size5LeftLbl.setEffect(new DropShadow(16, Color.BLACK));
        a = playerField.getShips(0);
        size5LeftLbl.setText(a.toString() + " LEFT");

        // Create buttons for placing ships or shooting
        Image turnImage = new Image(getClass().getResource("TurnIcon.png").toExternalForm(), 18, 18, true, true);
        Image shootImage = new Image(getClass().getResource("ShootIcon.png").toExternalForm(), 18, 18, true, true);
        this.shootButton = new Button("SHOOT", new ImageView(shootImage));
        this.shootButton.setDisable(true); // Shoot button is disabled until all ships are placed
        this.boat2Button = new Button("SIZE 2");
        this.boat3Button = new Button("SIZE 3");
        this.boat4Button = new Button("SIZE 4");
        this.boat5Button = new Button("SIZE 5");
        this.placeRandomButton = new Button("PLACE RANDOM");
        this.clearButton = new Button("RESET");
        this.turn2Button = new Button("", new ImageView(turnImage));
        this.turn3Button = new Button("", new ImageView(turnImage));
        this.turn4Button = new Button("", new ImageView(turnImage));
        this.turn5Button = new Button("", new ImageView(turnImage));
        this.kiPlayButton = new Button("KI FIGHT");
        this.saveButton = new Button("SAVE GAME");
        this.saveButton.setDisable(true);
        this.surrenderButton = new Button("SURRENDER");
        this.surrenderButton.setDisable(true);
        if(loadGameSettings){
            this.placeRandomButton.setDisable(true);
            this.shootButton.setDisable(false);
            this.kiPlayButton.setDisable(true);
            this.clearButton.setDisable(true);

        }

        // Set button styles
        setButtonStyles();

        shootButton.setOnAction(e -> {
            this.shoot = true;
            this.placeRandomButton.setDisable(true);
            this.clearButton.setDisable(true);
            boat2Button.setDisable(true);
            boat3Button.setDisable(true);
            boat4Button.setDisable(true);
            boat5Button.setDisable(true);
            turn2Button.setDisable(true);
            turn3Button.setDisable(true);
            turn4Button.setDisable(true);
            turn5Button.setDisable(true);
            this.playerRows.setDisable(true); // Disable player field because you can not shoot your own ships
            this.enemyRows.setDisable(true);
            if(loadGameSettings) {
                if (!isHost && readyCounter == 0) {
                    gameService.start();
                    gameService.onSucceededProperty();
                    readyCounter++;
                }
                if (readyCounter == 0) { // Initialize only if its not done already (ready button)
                    NetPlayer.outMessage("OK");
                }
                shootButton.setDisable(true);
                readyCounter++;
                startLoadedGame();
            }
            else {
                if (isHost && readyCounter == 0) {
                    gameService.start();
                    gameService.onSucceededProperty();
                    readyCounter++;
                }
                if (readyCounter == 0) { // Initialize only if its not done already (ready button)
                    NetPlayer.outMessage("OK");
                }
                shootButton.setDisable(true);
                readyCounter++;
                startGame();
            }
        });
        boat2Button.setOnAction(e -> {
            this.place = true;
            this.shipSize = 2;
            this.enemyRows.setDisable(true); // Disable enemy field because you can not place ships in the enemy field
        });
        boat3Button.setOnAction(e -> {
            this.place = true;
            this.shipSize = 3;
            this.enemyRows.setDisable(true);
        });
        boat4Button.setOnAction(e -> {
            this.place = true;
            this.shipSize = 4;
            this.enemyRows.setDisable(true);
        });
        boat5Button.setOnAction(e -> {
            this.place = true;
            this.shipSize = 5;
            this.enemyRows.setDisable(true);
        });
        turn2Button.setOnAction(e -> {
            int x = player.getLast2()[0];
            int y = player.getLast2()[1];
            player.placeShip(x, y, 2, playerField);
            setFieldColoring();
        });
        turn3Button.setOnAction(e -> {
            int x = player.getLast3()[0];
            int y = player.getLast3()[1];
            player.placeShip(x, y, 3, playerField);
            setFieldColoring();
        });
        turn4Button.setOnAction(e -> {
            int x = player.getLast4()[0];
            int y = player.getLast4()[1];
            player.placeShip(x, y, 4, playerField);
            setFieldColoring();
        });
        turn5Button.setOnAction(e -> {
            int x = player.getLast5()[0];
            int y = player.getLast5()[1];
            player.placeShip(x, y, 5, playerField);
            setFieldColoring();
        });
        placeRandomButton.setOnAction(e -> {
            playerField = new Feld(fieldSize);
            size2LeftLbl.setText("0 LEFT");
            size3LeftLbl.setText("0 LEFT");
            size4LeftLbl.setText("0 LEFT");
            size5LeftLbl.setText("0 LEFT");

            // Alle Cells auf Standardfarbe zurücksetzten
            for (int y = 0; y < playerField.size(); y++) {
                // durchlaufe spalten
                for (int x = 0; x < playerField.size(); x++) {
                    cellArrayPlayer[y][x].setFill(Color.LIGHTGRAY);
                }
            }

            // Schiffe random durch die KI platzieren lassen
            ki.placeShips(playerField);
            shootButton.setDisable(false);
            boat2Button.setDisable(true);
            boat3Button.setDisable(true);
            boat4Button.setDisable(true);
            boat5Button.setDisable(true);
            turn2Button.setDisable(true);
            turn3Button.setDisable(true);
            turn4Button.setDisable(true);
            turn5Button.setDisable(true);
            setFieldColoring();
        });

        clearButton.setOnAction(e -> {
            player.resetGetLast();
            playerField = new Feld(fieldSize);
            player.createShips(playerField);
            playerRows.setDisable(false);
            shootButton.setDisable(true);
            boat2Button.setDisable(false);
            boat3Button.setDisable(false);
            boat4Button.setDisable(false);
            boat5Button.setDisable(false);
            turn2Button.setDisable(false);
            turn3Button.setDisable(false);
            turn4Button.setDisable(false);
            turn5Button.setDisable(false);
            kiPlayButton.setDisable(false);
            setLeftLabel(2);
            setLeftLabel(3);
            setLeftLabel(4);
            setLeftLabel(5);
            turn2Button.setDisable(false);

            // Alle Cells auf Standardfarbe zurücksetzten
            for (int y = 0; y < playerField.size(); y++) {
                // durchlaufe spalten
                for (int x = 0; x < playerField.size(); x++) {
                    cellArrayPlayer[y][x].setFill(Color.LIGHTGRAY);
                }
            }
        });
        kiPlayButton.setOnAction(e -> {
            shootButton.setDisable(true);
            boat2Button.setDisable(true);
            boat3Button.setDisable(true);
            boat4Button.setDisable(true);
            boat5Button.setDisable(true);
            turn2Button.setDisable(true);
            turn3Button.setDisable(true);
            turn4Button.setDisable(true);
            kiPlayButton.setDisable(true);
            turn5Button.setDisable(true);
            clearButton.setDisable(true);
            placeRandomButton.setDisable(true);
            initKIGame();
        });
        saveButton.setOnAction(e -> {
            Feld[] fieldArray = {playerField, enemyField};
            GameSaver gameSaver = new GameSaver();
            gameSaver.saveFile(fieldArray);

            NetPlayer.outMessage("SAVE " + gameSaver.getLastSaved());
            System.out.println(gameSaver.getLastSaved());

            Boolean answer = ConfirmBox.display("", "Resume game?");
            if (answer)
                Platform.exit();
        });
        surrenderButton.setOnAction(e -> {
            Boolean answer = ConfirmBox.display("", "Are you sure you want to surrender?");
            if (!answer) {

                // Tell enemy player that you've quit and closes the socket
                NetPlayer.closeSockets();
                setStopTasks = true;
                super.stopGameMusic(); // Stop menu music

                MainMenu mm = new MainMenu();
                mm.setWindowSizes(root.getWidth(), root.getHeight());
                mm.mainMenuLayout();

                BattleshipsApp.getStage().setScene(mm.getCurrentScene());
            }
        });

        // Create box which contains the enemy field and the player field
        HBox fieldHolder = new HBox();
        fieldHolder.setPadding(new Insets(20, 20, 20, 20));
        fieldHolder.setSpacing(20);

        // Create boxes which hold the user field and the enemy field
        this.enemyRows = createField(this.fieldSize);
        this.playerRows = createField(this.fieldSize);



        VBox enemyRowsHolder = new VBox();
        VBox playerRowsHolder = new VBox();
        enemyRowsHolder.setAlignment(Pos.CENTER);
        playerRowsHolder.setAlignment(Pos.CENTER);
        enemyRowsHolder.getChildren().addAll(enemyFieldLbl, enemyRows);
        playerRowsHolder.getChildren().addAll(playerFieldLbl, playerRows);

        // Create HBoxes for the place and turn buttons
        HBox boat2Box = new HBox(5);
        boat2Box.getChildren().addAll(boat2Button, turn2Button);
        HBox boat3Box = new HBox(5);
        boat3Box.getChildren().addAll(boat3Button, turn3Button);
        HBox boat4Box = new HBox(5);
        boat4Box.getChildren().addAll(boat4Button, turn4Button);
        HBox boat5Box = new HBox(5);
        boat5Box.getChildren().addAll(boat5Button, turn5Button);
        HBox placeRandomBox = new HBox(10);
        placeRandomBox.getChildren().addAll(placeRandomButton, clearButton);

        // Create box which contains the label and the buttons
        GridPane buttonHolder = new GridPane();
        buttonHolder.setAlignment(Pos.CENTER);
        buttonHolder.setHgap(20);
        buttonHolder.setVgap(15);
        ColumnConstraints c = new ColumnConstraints();
        c.setPrefWidth(100);
        buttonHolder.getColumnConstraints().addAll(c);

        // Add nods to the pane
        buttonHolder.add(shootButton, 0, 0);
        buttonHolder.add(placeBoatsLbl, 0, 2);
        buttonHolder.add(placeRandomBox, 0, 3, 2, 1);
        buttonHolder.add(boat2Box, 0, 4);
        buttonHolder.add(size2LeftLbl, 1, 4);
        buttonHolder.add(boat3Box, 0, 5);
        buttonHolder.add(size3LeftLbl, 1, 5);
        buttonHolder.add(boat4Box, 0, 6);
        buttonHolder.add(size4LeftLbl, 1, 6);
        buttonHolder.add(boat5Box, 0, 7);
        buttonHolder.add(size5LeftLbl, 1, 7);
        buttonHolder.add(kiPlayButton, 0, 9, 2, 1);
        buttonHolder.add(saveButton, 0, 10, 2, 1);
        buttonHolder.add(surrenderButton, 0, 11, 2, 1);

        // Add both fields to the field holder and set scene
        fieldHolder.getChildren().addAll(enemyRowsHolder, playerRowsHolder);
        root.getChildren().add(background);
        root.setRight(fieldHolder);
        root.setLeft(buttonHolder);

        Scene s1 = new Scene(root);
        return s1;
    }

    /**
     * Erzeugt die Spielfelder, auf denen die Schiffe platziert und Schüsse abgefeuert werden und fügt sie dem Layout hinzu.
     * Außerdem befindet sich hier der EventHandler für einen Spielerzug und den Gegnerzug. Von hier aus werden dann die
     * unterschiedlichen Tasks gestartet.
     *
     * @param size Feldgröße die beim Erzeugen des Spiels ausgewählt wurde.
     * @return Das erzeugte Spielfeld.
     */
    private VBox createField(int size) {
        // Set cell size based on the chosen field size
        int cellSize = 30;
        if (size <= 10)
            cellSize = 35;
        if (size >= 20)
            cellSize = 25;

        VBox rows = new VBox(); // Box which contains the field
        rows.setAlignment(Pos.CENTER);

        // Erzeuge Reihen basierend auf der Feldgröße
        for (int y = 0; y < size; y++) {
            HBox row = new HBox(); // Single row including it's columns

            // Create Columns based on field size
            for (int x = 0; x < size; x++) {
                Cell cell = new Cell(x, y, cellSize);

                // Add cells to the enemy array first, then to the player array
                if (counter == 0)
                    cellArrayEnemy[y][x] = cell;
                else
                    cellArrayPlayer[y][x] = cell;

                EventHandler<MouseEvent> spielzugSpieler = new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                        // Schießen
                        if (shoot) {
                            NetPlayer.outMessage("SHOT " + cell.getCellY() + " " + cell.getCellX());
                            setTempCell(cell);
                            playerTurnTask();
                        } else if (place) {
                            System.out.println("x: " + cell.getCellX() + "\ty: " + cell.getCellY());
                            player.createShips(playerField);
                            player.placeShip(cell.getCellY(), cell.getCellX(), shipSize, playerField);
                            if (player.allShipsPlaced()) { // Wenn alle Schiffe platziert, aktiviere shoot button
                                shootButton.setDisable(false);
                                playerRows.setDisable(true);

                            }
                            setFieldColoring();
                            playerField.printField();
                            setLeftLabel(shipSize);
                        }
                    }

                };

                EventHandler<MouseEvent> spielzugGegner = new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                        // Nach einem Wassertreffer ist der Gegner am Zug
                        if (gegnerIstDran)
                            enemyTurnTask();
                    }
                };
                cell.addEventHandler(MouseEvent.MOUSE_PRESSED, spielzugSpieler);
                cell.addEventHandler(MouseEvent.MOUSE_RELEASED, spielzugGegner);
                row.getChildren().add(cell);
            }
            rows.getChildren().add(row);
        }
        counter++;
        return rows;
    }

    void setWindowSizes(double width, double height) {
        windowWidth = width;
        windowHeight = height;
        root.setPrefSize(windowWidth, windowHeight);
    }

    /**
     * Nachdem das Spiel zu Ende ist wird diese Methode aufgerufen, welche je nach Sieger die passende Musik und Szene
     * für den Endscreen enthält.
     *
     * @param whoWon Ist das Spiel beendet wird der Name des Gewinners als String übergeben und überprüft.
     */
    private void gameOver(String whoWon) {
        boolean won;
        if (whoWon.equals("you"))
            won = true;
        else
            won = false;

        boolean play = false;
        if (isPlaying()) {
            super.stopGameMusic();
            play = true;
        }
        NetPlayer.closeSockets();
        setStopTasks = true;
        this.setPlaying(play);
        GameOverScreen gameOver = new GameOverScreen(true);
        Scene gameOverScene = gameOver.gameOverLayout(won);
        gameOver.setWindowSizes(root.getWidth(), root.getHeight());
        gameOver.setPlaying(play);
        startEndScreenMusic(won);

        // Turn on the right tune
        if (won)
            gameOver.setEndScreenPlayer(winnerPlayer);
        else
            gameOver.setEndScreenPlayer(looserPlayer);

        // Switch scene
        BattleshipsApp.getStage().setScene(gameOverScene);
    }

    /**
     * Färbt die Zellen je nach Inhalt. Grün = Schiff, Rot = Treffer, Blau = Wasser, Grau = noch nicht aufgedeckte Zellen
     */
    private void setFieldColoring() {
        // Einfärben der Cells an denen sich ein Schiff befindet
        // durchlaufe alle zeilen
        for (int y = 0; y < playerField.size(); y++) {
            // durchlaufe spalten
            for (int x = 0; x < playerField.size(); x++) {
                if (playerField.getFieldstate(y, x) == SHIP) {
                    cellArrayPlayer[y][x].setFill(Color.GREEN);
                } else if (playerField.getFieldstate(y, x) == FieldState.SHIP_HIT) {
                    cellArrayPlayer[y][x].setFill(Color.RED);
                } else if (playerField.getFieldstate(y, x) == FieldState.WATER_HIT) {
                    cellArrayPlayer[y][x].setFill(Color.AQUA);
                } else {
                    cellArrayPlayer[y][x].setFill(Color.LIGHTGRAY);
                }
            }
        }
    }

    /**
     * Setzt die Label auf den Wert der aktuellen Anzahl an Schiffen die einem zum platzieren verbleiben.
     *
     * @param size Übergibt die Schiffsgröße um das zum Schiff passende Label zu aktualisieren.
     */
    private void setLeftLabel(int size) {
        Integer i;
        if (size == 2) {
            i = playerField.getShips(3);
            size2LeftLbl.setText(i.toString() + " LEFT");
        } else if (size == 3) {
            i = playerField.getShips(2);
            size3LeftLbl.setText(i.toString() + " LEFT");
        } else if (size == 4) {
            i = playerField.getShips(1);
            size4LeftLbl.setText(i.toString() + " LEFT");
        } else if (size == 5) {
            i = playerField.getShips(0);
            size5LeftLbl.setText(i.toString() + " LEFT");
        }
    }

    /**
     * Startet die Musik die im Endscreen abgespielt wird.
     *
     * @param won Gibt an ob gewonnen oder verloren wurde.
     */
    private void startEndScreenMusic(boolean won) {
        looserPlayer = new MediaPlayer(looserMusic);
        winnerPlayer = new MediaPlayer(winnerMusic);

        if (won && isPlaying()) { // only play if won and music is on
            winnerPlayer.setVolume(gameMusicPlayer.getVolume());
            winnerPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            winnerPlayer.play();
        } else if (!won && isPlaying()) {
            looserPlayer.setVolume(gameMusicPlayer.getVolume());
            looserPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            looserPlayer.play();
        }
    }

    void setGameMusicPlayer(MediaPlayer mp) {
        gameMusicPlayer = mp;
    }

    void setNetPlayer(Client netPlayer) {
        NetPlayer = netPlayer;
    }

    /**
     * Durchläuft solange die Schleife bis eine Antwort vom Gegener kommt. D.h hier wird auf eine Antwort des Gegners
     * gewartet.
     *
     * @return Die Antwort des Gegeners.
     */
    String waitForEnemyTurn() {
        String wline = "";
        while (wline == "") {
            wline = NetPlayer.inMessage();
        }
        return wline;
    }

    /**
     * Wird aufgerufen, falls der Spieler ein Mensch ist. Nachdem dies ein mal entschieden wurde, kann die KI nicht das
     * spiel übernehmen. Es wird dann anhand des Attributs isHost entschieden, wer zu erst am Zug ist. Hier wird auch
     * der Handshake ( SIZE --> | <-- OK | OK -->) beendet, mithilfe des gameservice Taks. onSucceededProperty wird so
     * abgeändert, dass es dem Client erlaubt wird, sobald der Handshake vollbracht ist, zu schießen.
     */
    void startGame() {
        kiPlayButton.setDisable(true);
        changeRowState(true);
        if (isHost) {
            gegnerIstDran = true;
            surrenderButton.setDisable(false);
            enemyTurnTask();
        } else {
            gameService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    changeRowState(false);
                    surrenderButton.setDisable(false);

                }
            });
            changeRowState(true);
            gameService.start();
            gameService.onSucceededProperty();
        }

    }

    /**
     * Wenn ein Spielstand im Netzwerk geladen wird, ändert sich auch der "Spielstart". Es ist nur im eigenne Zug möglich
     * zu Speichern. Deshalb darf bei einem geladenen Spiel auch der Host zuerst schießen. Es müssen auch die vorherigen
     * Zustände der Buttons/Rows hergestellt werden. Dies wird mit setLoadedDefaults erreicht.
     * Die Funtion ist sozusagen ein "invertiertes" startGame();
     */
    void startLoadedGame() {
        setLoadedDefaults();
        if (!isHost) {
            gegnerIstDran = true;
            surrenderButton.setDisable(false);
            enemyTurnTask();
        } else {
            gameService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    changeRowState(false);
                    surrenderButton.setDisable(false);

                }
            });
            changeRowState(true);
            gameService.start();
            gameService.onSucceededProperty();
        }
    }

    /**
     * Funktion zum Schießen.
     *
     * @param y Koordinate auf der x-Achse auf welche geschossen werden soll.
     * @param x Koordinate auf der y-Achse auf welche geschossen werden soll.
     * @return 1 = Treffer, 0 = Wasser, -1 = Other.
     */
    private int shoot(int y, int x) {
        if (x >= 0 && y >= 0) {
            if (player.shoot(y, x, playerField)) {
                return 1;
            } else {
                return 0;
            }
        }
        return -1;
    }

    /**
     * Auch der KI muss man vermitteln, wer Anfangen darf. Als Client ist der Handshake nach dem Senden des "OK"
     * abgeschlossen und kiGame() kann aufgerufen werden. Als host muss man auf ein eingehendes "OK" warten, bevor dies
     * passieren darf.
     */
    public void initKIGame() {
        if (isHost && readyCounter == 0) {
            gameService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    NetPlayer.outMessage("OK");
                    kiGame();
                }
            });
            gameService.start();
            gameService.onSucceededProperty();
            readyCounter++;

        }
        if (readyCounter == 0) { // Initialize only if its not done already (ready button)
            NetPlayer.outMessage("OK");
            kiGame();

        }
        readyCounter++;
    }

    /**
     * Wenn der Spieler eine KI ist, müssen auch dementsprechend deren Funktionen für ihren eigenen, bzw. das Warten
     * für auf den Zug des Gegners aufgerufen werden. Wer Beginnen darf wurde mit initKIGame() festgelegt.
     * Im vergleich zu startGame() erden lediglich anstelle von playerTurnTask() kiTurnTask aufgerufen.
     * Für das Warten werden die analogen Funktionen aufgerufen.
     */
    public void kiGame() {

        ki.placeShips(playerField);
        setFieldColoring();

        if (isHost) {

            enemyTurnTaskKI();
        } else {
            gameService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    kiTurnTask();
                }
            });
            changeRowState(true);

            gameService.start();
            gameService.onSucceededProperty();

        }

    }

    private void setTempCell(Cell c) {
        tempCell = c;
    }

    private Cell getTempCell() {
        return tempCell;
    }

    public synchronized void changeRowState(boolean able) {
        enemyRows.setDisable(able);
    }

    /**
     * Setzt die beim Laden eines alten Spielstand festgelegten Settings neu und passt die Feldfärbung an diese
     * Einstellungen
     * an.
     */
    void setLoadedDefaults() {
        setFieldColoring();

        if (player.allShipsPlaced()) {
            gegnerIstDran = false;
            playerRows.setDisable(true);
            shootButton.setDisable(true);
            surrenderButton.setDisable(false);
            this.shoot = true;
            this.placeRandomButton.setDisable(true);
            this.clearButton.setDisable(true);
            this.playerRows.setDisable(true); // Disable player field because you can not shoot your own ships
            this.enemyRows.setDisable(true);

            // Set labels of left ships to zero
            this.size2LeftLbl.setText("0");
            this.size3LeftLbl.setText("0");
            this.size4LeftLbl.setText("0");
            this.size5LeftLbl.setText("0");

            // Deactivate place buttons
            this.boat2Button.setDisable(true);
            this.boat3Button.setDisable(true);
            this.boat4Button.setDisable(true);
            this.boat5Button.setDisable(true);

            // Deactivate turn buttons
            this.turn2Button.setDisable(true);
            this.turn3Button.setDisable(true);
            this.turn4Button.setDisable(true);
            this.turn5Button.setDisable(true);

            // Set the field coloring for the enemy field
            for (int y = 0; y < enemyField.size(); y++) {
                // durchlaufe spalten
                for (int x = 0; x < enemyField.size(); x++) {
                    if (enemyField.getFieldstate(y, x) == SHIP) {
                        cellArrayEnemy[y][x].setFill(Color.GREEN);
                    } else if (enemyField.getFieldstate(y, x) == FieldState.SHIP_HIT) {
                        cellArrayEnemy[y][x].setFill(Color.RED);
                        cellArrayEnemy[y][x].setDisable(true);
                    } else if (enemyField.getFieldstate(y, x) == FieldState.WATER_HIT) {
                        cellArrayEnemy[y][x].setFill(Color.AQUA);
                        cellArrayEnemy[y][x].setDisable(true);
                    } else {
                        cellArrayEnemy[y][x].setFill(Color.LIGHTGRAY);
                        cellArrayEnemy[y][x].setDisable(false);
                    }
                }
            }
        }
    }

    /**
     * Setzt Styles wie Schriftgröße, Schriftart, Hintergrundfarbe und mehr.
     */
    void setButtonStyles() {
        shootButton.setStyle("-fx-font-family: 'Times New Roman';" +
                "-fx-font-size: 14px;" +
                "-fx-border-radius: 1px;" +
                "-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);"
        );
        boat2Button.setStyle("-fx-font-family: 'Times New Roman';" +
                "-fx-font-size: 14px;" +
                "-fx-border-radius: 1px;" +
                "-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);"
        );
        boat3Button.setStyle("-fx-font-family: 'Times New Roman';" +
                "-fx-font-size: 14px;" +
                "-fx-border-radius: 1px;" +
                "-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);"
        );
        boat4Button.setStyle("-fx-font-family: 'Times New Roman';" +
                "-fx-font-size: 14px;" +
                "-fx-border-radius: 1px;" +
                "-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);"
        );
        boat5Button.setStyle("-fx-font-family: 'Times New Roman';" +
                "-fx-font-size: 14px;" +
                "-fx-border-radius: 1px;" +
                "-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);"
        );
        placeRandomButton.setStyle("-fx-font-family: 'Times New Roman';" +
                "-fx-font-size: 14px;" +
                "-fx-border-radius: 1px;" +
                "-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);"
        );
        clearButton.setStyle("-fx-font-family: 'Times New Roman';" +
                "-fx-font-size: 14px;" +
                "-fx-border-radius: 1px;" +
                "-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);"
        );
        kiPlayButton.setStyle("-fx-font-family: 'Times New Roman';" +
                "-fx-font-size: 14px;" +
                "-fx-border-radius: 1px;" +
                "-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);"
        );
        saveButton.setStyle("-fx-font-family: 'Times New Roman';" +
                "-fx-font-size: 14px;" +
                "-fx-border-radius: 1px;" +
                "-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);"
        );
        surrenderButton.setStyle("-fx-font-family: 'Times New Roman';" +
                "-fx-font-size: 14px;" +
                "-fx-border-radius: 1px;" +
                "-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);"
        );
    }

    void setPlayerField(Feld field) {
        this.playerField = field;
    }

    void setEnemyField(Feld field) {
        this.enemyField = field;
    }
}