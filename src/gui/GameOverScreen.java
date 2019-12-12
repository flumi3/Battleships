package gui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * Wird dem Nutzer angezeigt, nachdem das Spiel durch Gewinnen oder Verlieren beendet wurde.
 */
public class GameOverScreen extends Menu {
    private double windowWidth;
    private double windowHeight;

    private SinglePlayer sp; // Instance of SinglePlayer
    private Scene spScene; // Scene which is created if you start the single player game

    private Pane root;
    private VBox menuBox;
    private Scene scene;
    private Line line;

    /**
     * Gibt an ob die aufrufende Klasse ein Multiplayer-Spiel oder ein Singleplayer-Spiel war, da sich die Szenen aufgrund
     * dessen unterscheiden.
     */
    private boolean isMultiplayer;

    /**
     * Media Player, welcher die Musik im Endbildschirm abspielt.
     */
    private MediaPlayer endScreenPlayer;

    /**
     * Menü-Punkte bestehend aus einem String, welcher den Name des Menü-Punktes darstellt und einem Runnable, in welchem
     * der Code steht, der ausgeführt werden soll, nachdem auf ein Menü-Punkt geklickt wurde. Basierend auf dieser Liste
     * werden die MenuItems erstellt. Somit wurden hier personalisierte Buttons erstellt.
     */
    private List<Pair<String, Runnable>> menuData = Arrays.asList(
            new Pair<String, Runnable>("PLAY AGAIN", () -> {
                endScreenPlayer.stop();
                sp.setWindowSizes(root.getWidth(), root.getHeight());
                spScene = sp.singlePlayerLayout(); // Initalisiert die Szene erst hier, da nun field size bekannt
                if (super.isPlaying()) {
                    endScreenPlayer.stop();
                    sp.setMusicVolume(endScreenPlayer.getVolume());
                    sp.startGameMusic();
                }
                BattleshipsApp.getStage().setScene(spScene); // Switch scenes
            }),
            new Pair<String, Runnable>("GO TO MAIN MENU", () -> {
                endScreenPlayer.stop();
                MainMenu mainMenu = new MainMenu();
                mainMenu.setWindowSizes(root.getWidth(), root.getHeight());
                Scene mmScene = mainMenu.mainMenuLayout();
                if (super.isPlaying()) {
                    mainMenu.setMusicOn(true);
                    mainMenu.setMusicVolume(endScreenPlayer.getVolume());
                }
                BattleshipsApp.getStage().setScene(mmScene);
            }),
            new Pair<String, Runnable>("EXIT GAME", Platform::exit)
    );

    /**
     * Initialisiert den Container so wie die Menü-Box und die Instanz des Single Players.
     */
    GameOverScreen(boolean isMultiplayer) {
        this.isMultiplayer = isMultiplayer;
        this.root = super.root;
        this.menuBox = new VBox();
        this.sp = new SinglePlayer();
    }

    /**
     * Erzeugt die Szene, die der Nutzer sieht, wenn das Spiel gewonnen oder verloren wurde. Ist das Spiel ein Multiplayer-Spiel
     * wird der Menü Punkt "PLAY AGAIN" nicht erzeugt. Dies geschiet nur bei einem Single Player Spiel.
     *
     * @param won Gibt an, ob der Nutzer gewonnen oder verloren hat.
     * @return Die erzeugte Szene.
     */
    Scene gameOverLayout(boolean won) {
        int i;
        if (isMultiplayer)
            i = 1;
        else
            i = 0;

        // Only create menu item "PLAY AGAIN" in the single player
        while (i < menuData.size()) {
            MenuItem item = new MenuItem(menuData.get(i).getKey());
            item.setOnAction(menuData.get(i).getValue());
            item.setTranslateX(-300);

            Rectangle clip = new Rectangle(300, 30);
            clip.translateXProperty().bind(item.translateXProperty().negate());

            item.setClip(clip);

            menuBox.getChildren().addAll(item);

            i++;
        }

        if (won)
            winnerScreen();
        else
            looserScreen();

        // Add line
        line = super.addLine(root.getWidth() / 2 - 100, root.getHeight() / 3 + 50);

        // Add menu
        super.addMenu(menuBox);

        // Start animation
        super.startAnimation(menuBox, line);

        scene = new Scene(root);
        return scene;
    }

    /**
     * Fügt dem Layout ein Hintergrundbild und Titel hinzu wenn der Nutzer gewonnen hat.
     */
    private void winnerScreen() {
        // Add background
        super.addBackground("YouWon.png");

        // Add title
        super.addTitle("YOU WON!", 52);
    }

    /**
     * Fügt dem Layout ein Hintergrundbild und Titel hinzu wenn der Nutzer verloren hat.
     */
    private void looserScreen() {
        // Add background
        super.addBackground("YouLost.png");

        // Add title
        super.addTitle("YOU LOST!", 52);
    }

    void setWindowSizes(double width, double height) {
        windowWidth = width;
        windowHeight = height;
        root.setPrefSize(windowWidth, windowHeight);
    }

    void setEndScreenPlayer(MediaPlayer mp) {
        this.endScreenPlayer = mp;
    }
}
