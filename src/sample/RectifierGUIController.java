package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class RectifierGUIController implements Initializable {

    @FXML
    public javafx.scene.image.ImageView originalImageView;

    @FXML
    public javafx.scene.image.ImageView rectifiedImageView;

    @FXML
    public Button rectifyButton;

    @FXML
    public Button browseButton;

    @FXML
    public Button loadButton;

    @FXML
    public TextField filePathTextField;

    private File selectedFile;
    private final FileChooser fileChooser = new FileChooser();
    private Stage stage = new Stage();

    @FXML
    public void onRectifyButtonClick() {

    }

    @FXML
    public void onBrowseButtonClick() {
        File tempFile;
        //fileChooser.setTitle("Select Image File");
        tempFile = fileChooser.showOpenDialog(stage);
        if (tempFile != null) {
            // is there a better way to check file type to ensure it's an image?
            if (tempFile.toString().substring(((int)tempFile.length() - 3)) == ".png") {
                selectedFile = tempFile;
                filePathTextField.setText(selectedFile.getPath());
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
