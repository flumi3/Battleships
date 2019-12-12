package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OkBox {
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
        Button yesButton = new Button("OK");

        yesButton.setOnAction(e -> {
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
        buttonBox.getChildren().addAll(yesButton);
        layout.getChildren().addAll(label,buttonBox);

        // Create and set scene
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }
}
