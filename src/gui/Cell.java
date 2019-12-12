package gui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Jede Instanz dieser Klasse repräsentiert eine einzelne Zelle auf dem Spielfeld.
 * Auf diesen werden später die Schiffe platziert und Schüsse ausgeführt.
 */
public class Cell extends Rectangle {
    private int x;
    private int y;

    /**
     * Erzeugt, basierend auf den Übergabeparametern eine Zelle.
     *
     * @param x Position der Zelle auf der x-Achse.
     * @param y Position der Zelle auf der y-Achse.
     * @param size Größe der Zelle
     */
    Cell(int x, int y, int size) {
        super(size, size);
        this.x = x;
        this.y = y;
        setFill(Color.LIGHTGRAY);
        setStroke(Color.BLACK);
    }

    int getCellX() {
        return x;
    }

    int getCellY() {
        return y;
    }
}
