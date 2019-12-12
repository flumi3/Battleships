package gui;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Titel welcher in den Menüs angezeigt wird.
 */
public class Title extends Pane {
    private Text text;

    /**
     * Erzeugt basierend auf den übergebenen Parametern einen Titel.
     *
     * @param name Name des Titels.
     * @param fontSize Schriftgröße des Titels.
     */
    public Title(String name, int fontSize) {
        String spread = "";
        for (char c : name.toCharArray()) {
            spread += c + " ";
        }

        text = new Text(spread);
        text.setFont(Font.font("Times New Roman", fontSize));
        text.setFill(Color.WHITE);
        text.setEffect(new DropShadow(40, Color.BLACK));

        getChildren().addAll(text);
    }

    public double getTitleWidth() {
        return text.getLayoutBounds().getWidth();
    }
}