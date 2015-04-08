package com.github.jackkell.perspectiverectifier;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Window;

public class Main extends Application {

	private static Image originalImage;
	private static RectifiedImage rectifiedImage;

    public static void main(String[] args) {
		originalImage = null;
		rectifiedImage = null;
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

	public static Image getOriginalImage() {
		return Main.originalImage;
	}

	public static void setOriginalImage(Image originalImage) {
		Main.originalImage = originalImage;
	}

    public static RectifiedImage getRectifiedImage() {
        return Main.rectifiedImage;
    }

	public static void setRectifiedImage(RectifiedImage rectifiedImage) {
		Main.rectifiedImage = rectifiedImage;
	}
}
