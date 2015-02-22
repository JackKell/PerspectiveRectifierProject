package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;

public class Main extends Application {

    private static RectifiedImage image;

    @Override
    public void start(Stage primaryStage) throws Exception{
		FXMLLoader loader = new FXMLLoader(RectifierGUIController.class.getResource("RectifierGUI.fxml"));
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

    public static void main(String[] args) {
        launch(args);
    }

    public static RectifiedImage getRectifiedImage() {
        return image;
    }

    public static void setRectifiedImage(Image image) {
        Main.image = new RectifiedImage(image);
    }
}