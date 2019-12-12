package gui;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * Diese Klasse enthält das Konzept des Menüs, von welchem so gut wie alle anderen GUI Klassen erben, um Code Redundanz
 * zu vermeiden.
 */
public class Menu {
    Pane root;

    /**
     * Gibt an ob die Game Musik schon an die Volume-Slider gebunden wurde. Benötigt in der Methode startGameMusic().
     */
    static boolean bound;
    private static int counter;

    /**
     * Gibt an ob der Musik-Player aktiv oder inaktiv ist.
     */
    private static boolean playing;

    // Audio Volume
    private double audioVolume;
    private double musicVolume;
    private double effectsVolume;

    // Sound values
    private static boolean soundsOn; // Tells if the sound is enabled
    private static boolean musicOn; // Tells if the music is enabled
    private static boolean soundEffectsOn; // Tells if the sound effects are enabled

    // Media Files
    private static Media menuMusic;
    private static Media gameMusic;
    private static Media clickSound;

    // Media players
    private static MediaPlayer effectsPlayer;
    private static MediaPlayer menuMusicPlayer;
    private static MediaPlayer gameMusicPlayer;


    /**
     * Setzt die Fenstergöße, initialisiert Musik und Musik-Player so wie die Lautstärke.
     */
    Menu() {
        counter = 0;
        root = new Pane();
        root.setPrefSize(1280, 720);

        // Einmal ganz durchhören... lohnt sich! :)
        menuMusic = new Media(this.getClass().getResource("Ramin Djawadi - Mother Of Dragons.mp3").toString());
        gameMusic = new Media(this.getClass().getResource("Ramin Djawadi - A Lannister Always Pays His Debts.mp3").toString());
        clickSound = new Media(this.getClass().getResource("Page Turn.mp3").toString());

        menuMusicPlayer = new MediaPlayer(menuMusic);
        gameMusicPlayer = new MediaPlayer(gameMusic);
        effectsPlayer = new MediaPlayer(clickSound);

        audioVolume = 0.1; // Start audioVolume
        musicVolume = 0.1;
        effectsVolume = 0.1;

        soundsOn = true;
        musicOn = true;
        soundEffectsOn = true;
    }

    /**
     * Erzeugt basierend auf den übergebenen Parametern x und y eine Linie, welche zur Oberfläche hinzugefügt
     * und anschließend zurück gegeben wird.
     *
     * @param x Legt die x-Position sowie die Dicke der Linie fest.
     * @param y Legt die Länge der Linie fest.
     * @return Die erzeugte Linie.
     */
    Line addLine(double x, double y) {
        Line line = new Line(x, y, x, y + 200);
        line.setStrokeWidth(3);
        line.setStroke(Color.color(1, 1, 1, 0.75));
        line.setScaleY(0);

        root.getChildren().add(line);
        return line;
    }

    /**
     * Fügt dem Layout ein Hintergrundbild hinzu und passt es auf die Fenstergröße an.
     *
     * @param path Gibt den Pfad des Bildes an, welches als Hintergrund verwendet werden soll.
     */
    void addBackground(String path) {
        ImageView background = new javafx.scene.image.ImageView(new Image(getClass().getResource(path).toExternalForm()));
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());
        root.getChildren().add(background);
    }

    /**
     * Fügt einen Titel zum Layout hinzu und bindet die Position des Titels an die Fenstergröße.
     *
     * @param name Name des Titels.
     * @param fontSize Legt die Schriftgröße des Titels fest.
     */
    void addTitle(String name, int fontSize) {
        Title title = new Title(name, fontSize);
        title.setTranslateX(root.getWidth() / 2 - title.getTitleWidth() / 2);
        title.setTranslateY(root.getHeight() / 3);
        root.getChildren().add(title);

        // Add listener for the horizontal title placement
        root.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double width = (double) newWidth;
            title.setTranslateX(width / 2 - title.getTitleWidth() / 2);
        });

        // Add listener for the vertical title placement
        root.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            double height = (double) newHeight;
            title.setTranslateY(height / 3);
        });
    }

    /**
     * Fügt dem Layout ein übegebenes Menü hinzu und bindet dessen Position an die Fenstergröße.
     *
     * @param menuBox Menü, welches zum Layout hinzugefügt werden soll.
     */
    void addMenu(VBox menuBox) {
        menuBox.setTranslateX(root.getWidth() / 2 - 95);
        menuBox.setTranslateY(root.getHeight() / 3 + 55);
        root.getChildren().add(menuBox);

        // Add listener for the horizontal menu placement
        root.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double width = (double) newWidth;
            menuBox.setTranslateX(width / 2 - 95);
        });

        // Add listener for the vertical menu placement
        root.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            double height = (double) newHeight;
            menuBox.setTranslateY(height / 3 + 55);
        });
    }

    /**
     * Startet die Menü Animation bei der die Menü-Reiter eingefahren werden.
     *
     * @param menuBox Menü, welches animiert werden soll.
     * @param line Linie, welche animiert werden soll.
     */
    void startAnimation(VBox menuBox, Line line) {
        ScaleTransition st = new ScaleTransition(Duration.seconds(1), line);
        st.setToY(1);
        st.setOnFinished(e -> {

            for (int i = 0; i < menuBox.getChildren().size(); i++) {
                Node n = menuBox.getChildren().get(i);

                TranslateTransition tt = new TranslateTransition(Duration.seconds(1 + i * 0.15), n);
                tt.setToX(0);
                tt.setOnFinished(e2 -> n.setClip(null));
                tt.play();
            }
        });
        st.play();
    }

    /**
     * Startet die Menü Musik.
     */
    void startMenuMusic() {
        if (soundsOn && musicOn) {
            menuMusicPlayer.stop();
            menuMusicPlayer.setCycleCount(AudioClip.INDEFINITE);
            menuMusicPlayer.setVolume(musicVolume);
            menuMusicPlayer.play();
        }
    }

    /**
     * Startet die Musik welche nur im Spiel zu hören ist, nicht im Menü.
     */
    void startGameMusic() {
        if (soundsOn && musicOn) {
            gameMusicPlayer.setCycleCount(AudioClip.INDEFINITE);
            if (!bound)
                gameMusicPlayer.setVolume(musicVolume);
            gameMusicPlayer.play();
            playing = true;
        }
    }

    /**
     * Startet den Ton (Seite umblättern), welcher bei Mausclicks auf Menüpunkte zu hören ist.
     */
    void startClickSound() {
        if (soundsOn && soundEffectsOn) {
            effectsPlayer.stop(); // Stop before, because you cant replay when its not stopped every time
            effectsPlayer.setCycleCount(1);
            if (counter == 0) { // Initialize only if its not done already, necessary because binding error
                effectsPlayer.setVolume(effectsVolume);
                counter++;
            }
            effectsPlayer.play();
        }
    }

    void stopMenuMusic() {
        menuMusicPlayer.stop();
    }
    void pauseMenuMusic() {
        menuMusicPlayer.pause();
    }
    void resumeMenuMusic() {
        menuMusicPlayer.play();
    }
    void stopGameMusic() {
        gameMusicPlayer.stop();
        playing = false;
    }


    // ---- Getter ---- //
    MediaPlayer getMenuMusicPlayer() {
        return menuMusicPlayer;
    }
    MediaPlayer getGameMusicPlayer() {
        return gameMusicPlayer;
    }
    MediaPlayer getEffectsPlayer() {
        return effectsPlayer;
    }
    boolean isPlaying() {
        return playing;
    }


    // ---- Setter ---- //
    void setMusicVolume(double v) {
        this.musicVolume = v;
    }
    void setEffectsVolume(double v) {
        this.effectsVolume = v;
    }
    void setSoundsOn(boolean value) {
        soundsOn = value;
    }
    void setMusicOn(boolean value) {
        musicOn = value;
    }
    void setSoundEffectsOn(boolean value) {
        soundEffectsOn = value;
    }
    void setPlaying(boolean value) {
        playing = value;
    }
}
