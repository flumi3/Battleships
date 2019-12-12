package gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Menü in welchem der Nutzer Einstellungen am Spielesound vornehmen kann.
 */
public class OptionsMenu extends Menu {
    private double windowWidth;
    private double windowHeight;
    private MainMenu mainMenu;

    private Scene currentScene;
    private Scene lastScene;

    private Pane root;
    private GridPane menuBox;

    OptionsMenu() {
        root = super.root;
        menuBox = new GridPane();
    }

    /**
     * Erzeugt die Szene, welche der Nutzer sieht, wenn er im Hauptmenü auf den Reiter "Options" drückt. Diese besteht
     * aus mehreren CheckBoxes für die unterschiedlichen Sounds so wie zwei Slidern welche an die Musik- und Soundeffekt-
     * Lautstärke gebunden sind. Rechts davon geben Label an auf welcher Prozentzahl sich die Lautstärke befindet.
     *
     * @return Die erzeugte Szene.
     */
    Scene optionsMenuLayout() {
        // Add background
        super.addBackground("MenuBackground.png");

        // Add title
        super.addTitle("OPTIONS", 38);

        // Customize grid pane
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setHgap(50);
        menuBox.setVgap(20);
        ColumnConstraints c = new ColumnConstraints();
        c.setPrefWidth(200);
        menuBox.getColumnConstraints().addAll(c);

        // Create captions
        Text soundLbl = new Text("GAME SOUNDS");
        soundLbl.setFont(Font.font("Times New Roman", 20));
        soundLbl.setFill(Color.WHITE);
        soundLbl.setEffect(new DropShadow(20, Color.BLACK));

        Text musicLbl = new Text("MUSIC");
        musicLbl.setFont(Font.font("Times New Roman", 20));
        musicLbl.setFill(Color.WHITE);
        musicLbl.setEffect(new DropShadow(20, Color.BLACK));

        Text musicVolumeLbl = new Text("VOLUME");
        musicVolumeLbl.setFont(Font.font("Times New Roman", 20));
        musicVolumeLbl.setFill(Color.WHITE);
        musicVolumeLbl.setEffect(new DropShadow(20, Color.BLACK));

        Text soundEffectsLbl = new Text("SOUND EFFECTS");
        soundEffectsLbl.setFont(Font.font("Times New Roman", 20));
        soundEffectsLbl.setFill(Color.WHITE);
        soundEffectsLbl.setEffect(new DropShadow(20, Color.BLACK));

        Text soundEffectsVolumeLbl = new Text("VOLUME");
        soundEffectsVolumeLbl.setFont(Font.font("Times New Roman", 20));
        soundEffectsVolumeLbl.setFill(Color.WHITE);
        soundEffectsVolumeLbl.setEffect(new DropShadow(20, Color.BLACK));

        Text audioCheckBoxLbl = new Text("ON");
        audioCheckBoxLbl.setFont(Font.font("Times New Roman", 14));
        audioCheckBoxLbl.setFill(Color.WHITE);
        audioCheckBoxLbl.setEffect(new DropShadow(20, Color.BLACK));

        Text musicCheckBoxLbl = new Text("ON");
        musicCheckBoxLbl.setFont(Font.font("Times New Roman", 14));
        musicCheckBoxLbl.setFill(Color.WHITE);
        musicCheckBoxLbl.setEffect(new DropShadow(20, Color.BLACK));

        Text effectsCheckBoxLbl = new Text("ON");
        effectsCheckBoxLbl.setFont(Font.font("Times New Roman", 14));
        effectsCheckBoxLbl.setFill(Color.WHITE);
        effectsCheckBoxLbl.setEffect(new DropShadow(20, Color.BLACK));

        Text soundValueLbl = new Text();
        soundValueLbl.setFont(Font.font("Times New Roman", 16));
        soundValueLbl.setFill(Color.WHITE);
        soundValueLbl.setEffect(new DropShadow(20, Color.BLACK));
        soundValueLbl.setText("75");

        Text musicValueLbl = new Text();
        musicValueLbl.setFont(Font.font("Times New Roman", 16));
        musicValueLbl.setFill(Color.WHITE);
        musicValueLbl.setEffect(new DropShadow(20, Color.BLACK));
        musicValueLbl.setText("75");

        Text effectsValueLbl = new Text();
        effectsValueLbl.setFont(Font.font("Times New Roman", 16));
        effectsValueLbl.setFill(Color.WHITE);
        effectsValueLbl.setEffect(new DropShadow(20, Color.BLACK));
        effectsValueLbl.setText("75");

        // Create Back button
        Button backButton = new Button("BACK");
        backButton.setTranslateX(windowWidth / 2 - 25);
        backButton.setTranslateY(windowHeight / 3 + 340);
        backButton.setStyle("-fx-font-family: 'Times New Roman';" +
                "-fx-font-size: 16px;" +
                "-fx-border-radius: 1px;" +
                "-fx-background-color: #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);"
        );
        backButton.setOnAction(e -> {
            super.startClickSound();
            mainMenu.setWindowSizes(root.getWidth(), root.getHeight());
            BattleshipsApp.getStage().setScene(lastScene);
        });
        root.getChildren().add(backButton);

        // Add listener for the horizontal placement
        root.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double width = (double) newWidth;
            backButton.setTranslateX((width / 2) - 25);
        });

        // Add listener for the vertical placement
        root.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            double height = (double) newHeight;
            backButton.setTranslateY(height / 3 + 340);
        });

        // Create CheckBoxes for on and off
        CheckBox audioCheckBox = new CheckBox();
        CheckBox musicCheckBox = new CheckBox();
        CheckBox effectsCheckBox = new CheckBox();
        audioCheckBox.setSelected(true);
        musicCheckBox.setSelected(true);
        effectsCheckBox.setSelected(true);

        // Check Box events
        audioCheckBox.setOnMouseClicked(e -> {
            if (!audioCheckBox.isSelected()) {
                setSoundsOn(false);
                super.pauseMenuMusic();
            } else {
                super.setSoundsOn(true);
                super.resumeMenuMusic();
            }
        });
        musicCheckBox.setOnMouseClicked(e -> {
            if (!musicCheckBox.isSelected()) {
                super.setMusicOn(false);
                super.pauseMenuMusic();
            } else {
                super.setMusicOn(true);
                super.resumeMenuMusic();
            }
        });
        effectsCheckBox.setOnMouseClicked(e -> {
            if (!effectsCheckBox.isSelected())
                super.setSoundEffectsOn(false);
            else
                super.setSoundEffectsOn(true);
        });

        // Create HBox for check box and its label
        HBox audioBox = new HBox(10);
        audioBox.getChildren().addAll(audioCheckBox, audioCheckBoxLbl);
        HBox musicBox = new HBox(10);
        musicBox.getChildren().addAll(musicCheckBox, musicCheckBoxLbl);
        HBox effectsBox = new HBox(10);
        effectsBox.getChildren().addAll(effectsCheckBox, effectsCheckBoxLbl);

        // Create Slider for music and sound effects volume
        Slider musicSlider = new Slider();
        Slider soundEffectsSlider = new Slider();

        // Customize music slider
        musicSlider.setMin(0);
        musicSlider.setMax(0.15);
        musicSlider.setValue(0.1);
        musicSlider.setPrefWidth(300);

        // Customize effects slider
        soundEffectsSlider.setMin(0);
        soundEffectsSlider.setMax(0.15);
        soundEffectsSlider.setValue(0.1);
        soundEffectsSlider.setPrefWidth(300);

        // Handle slider events
        musicSlider.setOnMouseDragged(e -> {
            double value = musicSlider.getValue();
            Integer lblValue = (int) ((100 / 0.15) * value);
            super.setMusicVolume(value);
            musicValueLbl.setText(lblValue.toString());
        });
        musicSlider.setOnMouseClicked(e -> {
            double value = musicSlider.getValue();
            Integer lblValue = (int) ((100 / 0.15) * value);
            super.setMusicVolume(value);
            musicValueLbl.setText(lblValue.toString());
        });
        soundEffectsSlider.setOnMouseDragged(e -> {
            double value = soundEffectsSlider.getValue();
            Integer lblValue = (int) ((100 / 0.15) * value);
            super.setEffectsVolume(value);
            effectsValueLbl.setText(lblValue.toString());
        });
        soundEffectsSlider.setOnMouseClicked(e -> {
            double value = soundEffectsSlider.getValue();
            Integer lblValue = (int) ((100 / 0.15) * value);
            super.setEffectsVolume(value);
            effectsValueLbl.setText(lblValue.toString());
        });

        // Bind audio volume to the sliders
        super.getMenuMusicPlayer().volumeProperty().bind(musicSlider.valueProperty());
        super.getGameMusicPlayer().volumeProperty().bind(musicSlider.valueProperty());
        super.getEffectsPlayer().volumeProperty().bind(soundEffectsSlider.valueProperty());

        // Create HBox for slider and its label
        HBox musicSliderBox = new HBox(10);
        musicSliderBox.getChildren().addAll(musicSlider, musicValueLbl);
        HBox effectSliderBox = new HBox(10);
        effectSliderBox.getChildren().addAll(soundEffectsSlider, effectsValueLbl);

        // Add nods to the grid pane
        menuBox.add(soundLbl, 0, 0);
        menuBox.add(audioBox, 1, 0);

        menuBox.add(musicLbl, 0, 2);
        menuBox.add(musicBox, 1, 2);
        menuBox.add(musicVolumeLbl, 0, 3);
        menuBox.add(musicSliderBox, 1, 3);

        menuBox.add(soundEffectsLbl, 0, 5);
        menuBox.add(effectsBox, 1, 5);
        menuBox.add(soundEffectsVolumeLbl, 0, 6);
        menuBox.add(effectSliderBox, 1, 6);

        // Add menu box to pane
        menuBox.setTranslateX(windowWidth / 2 - 275);
        menuBox.setTranslateY(windowHeight / 3 + 55);
        root.getChildren().add(menuBox);

        // Add listener for the horizontal placement
        root.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double width = (double) newWidth;
            menuBox.setTranslateX((width / 2) - 275);
        });
        // Add listener for the vertical placement
        root.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            double height = (double) newHeight;
            menuBox.setTranslateY(height / 3 + 55);
        });


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
