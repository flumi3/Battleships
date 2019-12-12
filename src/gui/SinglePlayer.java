package gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import javafx.util.Duration;

import logic.*;
import sun.applet.Main;

import static logic.FieldState.SHIP;

/**
 * Repräsentiert das eigentliche Spiel. Hier werden die Felder erzeugt, Schiffe platziert, und gegen die KI gespielt.
 */
public class SinglePlayer extends Menu {
    private double windowWidth;
    private double windowHeight;
    private BorderPane root;
    private int counter;

    /**
     * False bis der Shoot-Button gedrückt wird.
     */
    private boolean shoot;

    /**
     * False bis ein Schiff platziert werden soll. Nachdem das Schiff platziert wurde wieder false.
     */
    private boolean place;

    /**
     * Gibt an ob der Gegner am Zug ist oder nicht.
     */
    private boolean gegnerIstDran = false;

    /**
     * Größe des Spielfeldes.
     */
    private int fieldSize;

    /**
     * Größe des Schiffs, welches man gerade platzieren möchte.
     */
    private int shipSize;

    /**
     * Ergebnis des Schusses. 0 = Wasser, 1 = Hit, -1 = Other.
     */
    private int shotResult;

    /**
     * Array bestehend aus den Zellen des Spielers. Somit kann per Koordinaten auf die einzelnen Zellen zugegriffen werden.
     */
    private Cell[][] cellArrayPlayer;

    /**
     * Array bestehend aus den Zellen des Spielers. Somit kann per Koordinaten auf die einzelnen Zellen zugegriffen werden.
     */
    private Cell[][] cellArrayEnemy; // Cell Array for enemy field, necessary for ship sunk

    private RealPlayer player; // Instance of Spieler class
    private Feld enemyField; // Array for enemy field
    private Feld playerField; // Array for player field
    private KIGegner ki;

    // Audio for end screen
    private static Media winnerMusic;
    private static Media looserMusic;
    private static MediaPlayer winnerPlayer;
    private static MediaPlayer looserPlayer;
    private static MediaPlayer gameMusicPlayer;

    /**
     * Box welche die Zellen des Spielers enthält.
     */
    private VBox playerRows;

    /**
     * Box welche die Zellen des Gegners enhält.
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
     * Initialisiert wichtige Attribute und Instanzen so wie die Musik des Verlierers bzw. des Gewinners.
     */
    SinglePlayer() {
        player = new RealPlayer();
        ki = new KIGegner();
        root = new BorderPane();
        winnerMusic = new Media(getClass().getResource("GameWonMusic.mp3").toExternalForm());
        looserMusic = new Media(getClass().getResource("GameLostMusic.mp3").toExternalForm());
    }

    /**
     * Erzeugt Szene, welche der Nutzer sieht, wenn er ein Einzelspieler-Spiel gestartet hat und nun gegen die KI spielt.
     * Auf der linken Seite befinden sich alle Funktionalitäten des Spiels. Von dort aus werden Schiffe platziert, das
     * Spiel gespeichert, der KI-Fight gestartet und mehr. Auf der linken Seite befinden sich die Felder des Spielers und
     * der KI, auf welche geschossen und platziert werden kann.
     *
     * @return Die erzeugte Szene.
     */
    Scene singlePlayerLayout() {
        // Only initialize if its not done already, neccessary because load game sets the field size to the saved one
        if (this.fieldSize == 0) {
            this.fieldSize = NewGameMenu.getFieldSize();
        }

        // Initialize only if its not done already, necessary because load game sets the fields to the saved ones
        if (enemyField == null && playerField == null) {
            enemyField = new Feld(fieldSize); // Create Array for enemy field
            playerField = new Feld(fieldSize); // Create Array for player field
            player.createShips(playerField);
            ki.placeShips(enemyField); // KI platziert ihre Schiffe
        }

        // Create array of cells so you can access them via coordinates
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
        Button saveButton = new Button("SAVE GAME");
        Button surrenderButton = new Button("SURRENDER");

        // Set button styles
        setButtonStyles();
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

        // Handle button events
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
            this.enemyRows.setDisable(false);
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
            if (x != -1 && y != -1) {
                player.placeShip(x, y, 2, playerField);
                setFieldColoring();
            }
        });
        turn3Button.setOnAction(e -> {
            int x = player.getLast3()[0];
            int y = player.getLast3()[1];
            if (x != -1 && y != -1) {
                player.placeShip(x, y, 3, playerField);
                setFieldColoring();
            }
        });
        turn4Button.setOnAction(e -> {
            int x = player.getLast4()[0];
            int y = player.getLast4()[1];
            if (x != -1 && y != -1) {
                player.placeShip(x, y, 4, playerField);
                setFieldColoring();
            }
        });
        turn5Button.setOnAction(e -> {
            int x = player.getLast5()[0];
            int y = player.getLast5()[1];
            if (x != -1 && y != -1) {
                player.placeShip(x, y, 5, playerField);
                setFieldColoring();
            }
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
                    cellArrayPlayer[y][x].setDisable(true);
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
            shootButton.setDisable(true);
            boat2Button.setDisable(false);
            boat3Button.setDisable(false);
            boat4Button.setDisable(false);
            boat5Button.setDisable(false);
            turn2Button.setDisable(false);
            turn3Button.setDisable(false);
            turn4Button.setDisable(false);
            turn5Button.setDisable(false);
            setLeftLabel(2);
            setLeftLabel(3);
            setLeftLabel(4);
            setLeftLabel(5);

            // Alle Cells auf Standardfarbe zurücksetzten
            for (int y = 0; y < playerField.size(); y++) {
                // durchlaufe spalten
                for (int x = 0; x < playerField.size(); x++) {
                    cellArrayPlayer[y][x].setFill(Color.LIGHTGRAY);
                    cellArrayPlayer[y][x].setDisable(false);
                }
            }
        });
        saveButton.setOnAction(e -> {
            Feld[] fieldArray = {playerField, enemyField};
            GameSaver gameSaver = new GameSaver();
            gameSaver.saveFile(fieldArray);

            Boolean answer = ConfirmBox.display("", "Resume game?");
            if (answer)
                Platform.exit();
        });
        surrenderButton.setOnAction(e ->  {
            Boolean answer = ConfirmBox.display("", "Are you sure you want to surrender?");
            if (!answer) {
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
        setLoadedDefaults();

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
        buttonHolder.add(saveButton, 0, 10, 2, 1);
        buttonHolder.add(surrenderButton, 0, 11, 2, 1);

        // Add both fields to the field holder and set scene
        fieldHolder.getChildren().addAll(enemyRowsHolder, playerRowsHolder);
        root.getChildren().add(background);
        root.setRight(fieldHolder);
        root.setLeft(buttonHolder);

        return new Scene(root);
    }

    /**
     * Erzeugt die Spielfelder, auf denen die Schiffe platziert und Schüsse abgefeuert werden und fügt sie dem Layout hinzu.
     * Außerdem befinden sich hier die EventHandler für den Zug des Spielers und der KI. Dies funktioniert mit mehreren
     * Threads, damit der GUI-Thread nicht einfriert und alles flüssig zum Zeitpunkt des Zuges dargestellt wird.
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
                if (counter == 0) {
                    cellArrayEnemy[y][x] = cell;
                }else {
                    cellArrayPlayer[y][x] = cell;
                }

                EventHandler<MouseEvent> spielzugSpieler = new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                    	 // Schießen
                        if (shoot) {
                            shotResult = shoot(cell.getCellY(), cell.getCellX());

                            // Wassertreffer
                            if (shotResult == 0) {
                                cell.setFill(Color.AQUA);
                                gegnerIstDran = true;
                                cell.setDisable(true);

                            // Schiffstreffer
                            } else if (shotResult == 1) {
                                cell.setFill(Color.RED);
                                cell.setDisable(true);

                                // Wurde das Schiff versenkt, färbe es dunkelrot
                                if (player.getVersenkt()) {
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
                                // Ueberpruefe ob Spiel zuende
                                if (enemyField.gameOver())
                                    gameOver(true);
                            }

                        // Schiff platzieren
                        } else if (place) {
                            System.out.println("x: " + cell.getCellX() + "\ty: " + cell.getCellY());
                            player.placeShip(cell.getCellY(), cell.getCellX(), shipSize, playerField);
                            if (player.allShipsPlaced()) { // Wenn alle Schiffe platziert, aktiviere shoot button
                                shootButton.setDisable(false);
                            }
                            setFieldColoring();
                            playerField.printField();
                            setLeftLabel(shipSize);
                        }          
                    }
                    
                  };
                  
                  EventHandler<MouseEvent> spielzugGegner = new EventHandler<MouseEvent>() {
                      public void handle(MouseEvent event) {

                          // Nach einem Wassertreffer ist die KI am Zug
                          if(shotResult == 0 && gegnerIstDran == true) {
                              playerRows.setDisable(true);
                              ki.shoot(playerField);
                              FieldState fieldstate = playerField.getFieldstate(ki.getShotY(), ki.getShotX());
                              boolean hit = ki.schussVerarbeiten(playerField, fieldstate);
                              if(hit) {
                                  ki.versenktInfo(playerField, ki.getShotY(), ki.getShotX());
                              }

                              double time = 250;
                              Timeline timeline = new Timeline();
                              setFieldColoring(timeline, time);
                              while (hit) { // KI darf so lange schießen bis sie Wasser trifft
                                  if (playerField.gameOver()) {
                                      timeline.play();
                                      hit = false;
                                      gameOver(false);
                                  } else {
                                      time+=250;
                                      ki.shoot(playerField);
                                      fieldstate = playerField.getFieldstate(ki.getShotY(), ki.getShotX());
                                      hit = ki.schussVerarbeiten(playerField, fieldstate);
                                      if(hit) {
                                          ki.versenktInfo(playerField, ki.getShotY(), ki.getShotX());
                                      }
                                      setFieldColoring(timeline, time);
                                  }
                              }
                              timeline.play();
                              gegnerIstDran = false;
                              playerRows.setDisable(false);
                          }
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
     * Führt die Schuss-Operation aus.
     *
     * @param y Koordinate der geklickten Zelle auf der y-Achse.
     * @param x Koordinate der geklickten Zelle auf der x-Achse.
     * @return 0 bei einem Wassertreffer. 1 bei einem Schiffstreffer oder -1 sollte etwas schief gelaufen sein.
     */
    private int shoot(int y, int x) {
        if (x >= 0 && y >= 0) {
            if (player.shoot(y, x, enemyField)) {
                return 1;
            } else {
                return 0;
            }
        }
        return -1;
    }

    /**
     * Wechselt bei fertigem Spiel die Szene GameOverScreen. Je nachdem man gewonnen oder verloren hat, werden
     * unterschiedliche Sountracks abgespielt und unterschiedliche Szenen gezeigt.
     *
     * @param won Ist das Spiel beendet true, andernfalls false.
     */
    private void gameOver(boolean won) {
        boolean play = false;
        if (isPlaying()) {
            super.stopGameMusic();
            play = true;
        }
        this.setPlaying(play);
        GameOverScreen gameOver = new GameOverScreen(false);
        Scene gameOverScene = gameOver.gameOverLayout(won);
        gameOver.setWindowSizes(root.getWidth(), root.getHeight());
        gameOver.setPlaying(play);
        startEndScreenMusic(won);
        if (won)
            gameOver.setEndScreenPlayer(winnerPlayer);
        else
            gameOver.setEndScreenPlayer(looserPlayer);
        BattleshipsApp.getStage().setScene(gameOverScene);
    }

    /**
     * Färbt die einzelnen Zellen des Spielfeldes des Spielers basierend auf ihrem Inhalt und animiert die Einfärbung.
     *
     * @param timeline
     * @param time
     */
    private void setFieldColoring(Timeline timeline, double time) {
        // Einfärben der Cells an denen sich ein Schiff befindet
        // durchlaufe alle zeilen
        for (int y = 0; y < playerField.size(); y++) {
            // durchlaufe spalten
            for (int x = 0; x < playerField.size(); x++) {
                if (playerField.getFieldstate(y, x) == SHIP) {
                	KeyValue kv = new KeyValue(cellArrayPlayer[y][x].fillProperty(), Color.GREEN);
                	KeyFrame kf = new KeyFrame(Duration.millis(time), kv);
                	timeline.getKeyFrames().addAll(kf);
                } else if (playerField.getFieldstate(y, x) == FieldState.SHIP_HIT) {
                	KeyValue kv = new KeyValue(cellArrayPlayer[y][x].fillProperty(), Color.RED);
                	KeyFrame kf = new KeyFrame(Duration.millis(time), kv);
                	timeline.getKeyFrames().addAll(kf);
                } else if (playerField.getFieldstate(y, x) == FieldState.WATER_HIT) {
                	KeyValue kv = new KeyValue(cellArrayPlayer[y][x].fillProperty(), Color.AQUA);
                	KeyFrame kf = new KeyFrame(Duration.millis(time), kv);
                	timeline.getKeyFrames().addAll(kf);
                } else {
                    cellArrayPlayer[y][x].setFill(Color.LIGHTGRAY);
                }
            }
        }
    }

    /**
     * Färbt die Zellen des Spielfeldes basierend auf ihrem Inhalt.
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
                    cellArrayPlayer[y][x].setDisable(true);
                } else if (playerField.getFieldstate(y, x) == FieldState.WATER_HIT) {
                    cellArrayPlayer[y][x].setFill(Color.AQUA);
                    cellArrayPlayer[y][x].setDisable(true);
                } else {
                    cellArrayPlayer[y][x].setFill(Color.LIGHTGRAY);
                    cellArrayPlayer[y][x].setDisable(false);
                }
            }
        }
    }

    /**
     * Setzt die Anzahl der verbleibenden Schiffe, welche noch gesetzt werden können.
     *
     * @param size Größe des Schiffs, bei welchem der Wert verändert werden soll.
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
     * Startet die Musik, welche abgespielt wird, wenn das Spiel zu Ende ist. Hat man das Spiel gewonnen, wird eine
     * andere Musik abgespielt als wenn man das Spiel verloren hat.
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
        } else if (!won && isPlaying()){
            looserPlayer.setVolume(gameMusicPlayer.getVolume());
            looserPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            looserPlayer.play();
        }
    }

    void setFieldSize(int size) {
        this.fieldSize = size;
    }

    void setPlayerField (Feld field) {
        this.playerField = field;
    }

    void setEnemyField(Feld field) {
        this.enemyField = field;
    }

    void setGameMusicPlayer(MediaPlayer mp) {
        gameMusicPlayer = mp;
    }

    /**
     * Setzt die beim laden eines alten Spielstand festgelegten Settings neu und passt die Feldfärbung auf diese
     * geladenen Settings an.
     */
    void setLoadedDefaults() {
        setFieldColoring();

        if (player.allShipsPlaced()) {
            shootButton.setDisable(false);
            this.shoot = true;
            this.placeRandomButton.setDisable(true);
            this.clearButton.setDisable(true);
            this.playerRows.setDisable(true); // Disable player field because you can not shoot your own ships
            this.enemyRows.setDisable(false);

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
                    if (enemyField.getFieldstate(y, x) == FieldState.SHIP_HIT) {
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
     * Setzt die Styles für die Buttons.
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
    }
}
