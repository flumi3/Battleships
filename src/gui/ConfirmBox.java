package gui;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

/**
 * Pop-Up-Fenster, welches erscheint, sobald das Spiel gespeichert wurde. Dort wird man gefragt ob man das Spiel nach dem
 * Speichern beenden moechte oder weiter spielen will.
 */
public class ConfirmBox {
    static boolean answer;

    public static boolean display(String title, String message) {
        // Create stage
        Stage window = new Stage();

        // Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);

        // Set title and minimal
        window.setTitle(title);
        window.setMinWidth(250);

        // Create label and set its message
        Label label = new Label();
        label.setText(message);

        // Create answer buttons
        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");

        // Handle the button events
        yesButton.setOnAction(e -> {
            answer = false;
            window.close();
        });
        noButton.setOnAction(e -> {
            answer = true;
            window.close();
        });

        // Create boxes which will include the items
        VBox layout = new VBox(10);
        HBox buttonBox = new HBox(10);

        // Set positions and edge spaces of the items
        layout.setPadding(new Insets(5,0 , 5, 0));
        layout.setAlignment(Pos.CENTER);
        buttonBox.setAlignment(Pos.CENTER);

        // Add buttons to the buttonBox and add label and buttonBox to the layout
        buttonBox.getChildren().addAll(yesButton, noButton);
        layout.getChildren().addAll(label,buttonBox);

        // Create and set scene
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }

}
