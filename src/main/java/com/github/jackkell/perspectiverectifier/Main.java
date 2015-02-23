package com.github.jackkell.perspectiverectifier;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    private static ImageRectifier imageRectifier;

    public static void main(String[] args) {
        imageRectifier = new ImageRectifier();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
		FXMLLoader loader = new FXMLLoader(RectifierGUIController.class.getResource("/RectifierGUI.fxml"));
		AnchorPane pane = loader.load();
		RectifierGUIController controller = loader.getController();
		controller.setParent(pane);

        primaryStage.setTitle("Image Rectifier");
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
    }

    public static ImageRectifier getImageRectifier() {
        return Main.imageRectifier;
    }
}