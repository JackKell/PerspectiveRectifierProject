package com.github.jackkell.perspectiverectifier;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Window;

public class Main extends Application { // work on retrieving the pixels contained by the lines made by the ImageViewDraw (combine lines?, create polygon?)

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
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMaxWidth(primaryStage.getWidth());
        primaryStage.setMaxHeight(primaryStage.getHeight());
    }

    public static ImageRectifier getImageRectifier() {
        return Main.imageRectifier;
    }
}
