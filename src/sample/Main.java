package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    private static RectifiedImage image;

    @Override
    public void start(Stage primaryStage) throws Exception{
		FXMLLoader loader = new FXMLLoader(RectifierGUIController.class.getResource("RectifierGUI.fxml"));
		AnchorPane pane = loader.load();
		RectifierGUIController controller = loader.getController();
		controller.setParent(pane);

        primaryStage.setTitle("Image Rectifier");
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static RectifiedImage getImage() {
        return image;
    }
}