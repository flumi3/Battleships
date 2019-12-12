package gui;

import javafx.beans.binding.Bindings;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Repräsentiert einen einzelnen Menüreiter. Beispielsweise "SINGLE PLAYER" oder "OPTIONS". Mit Hilfe der Listen aus
 * anderen Klassen, welche Paare aus String und Runnable enthalten, werden hier die personalisierten Buttons erstellt.
 */
public class MenuItem extends Pane {
    private Text text;

    /**
     * Schatten für den Menü-Punkt.
     */
    private Effect shadow = new DropShadow(10, Color.BLACK); // Schatten

    /**
     * Verschwommener Effekt für den Menü-Punkt, welcher beim darüber hovern weg geht.
     */
    private Effect blur = new BoxBlur(2, 2, 1); // Verschwommener Effekt

    /**
     * Erzeugt einen Menüreiter in Form eines Polygons, welcher dann in den Menü-Szenen wie ein Button fungiert.
     *
     * @param name Name des Menüreiters.
     */
    public MenuItem(String name) {
        Polygon bg = new Polygon(
                0, 0,
                200, 0,
                215, 15,
                200, 30,
                0, 30
        );
        bg.setStroke(Color.color(1, 1, 1, 0.75));
        bg.setEffect(new GaussianBlur(5)); // Verschwommener Effekt bei den Rändern

        bg.fillProperty().bind(
                Bindings.when(pressedProperty())
                        .then(Color.color(0, 0, 0, 0.75))
                        .otherwise(Color.color(0, 0, 0, 0.25))
        );

        text = new Text(name);
        text.setTranslateX(5);
        text.setTranslateY(20);
        text.setFont(Font.font("Times New Roman", 14));
        text.setFill(Color.WHITE);

        text.effectProperty().bind(
                Bindings.when(hoverProperty())
                        .then(shadow) // Drop Shadow sobald über das item gehovered wird
                        .otherwise(blur) // Verschwommener Effekt solange nichts passiert
        );
        getChildren().addAll(bg, text);
    }

    /**
     * Führt bei einem Mausklick auf den Menüreiter den übergebenen Code aus.
     *
     * @param action Code welcher ausgeführt werden soll.
     */
    public void setOnAction(Runnable action) {
        setOnMouseClicked(e -> action.run());
    }
}
