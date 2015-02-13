package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class RectifierGUIController implements Initializable {

    @FXML
    public ImageView originalImageView;

    @FXML
    public ImageView rectifiedImageView;

    @FXML
    public Button rectifyButton;

    @FXML
    public Button browseButton;

    @FXML
    public Button loadButton;

    @FXML
    public TextField filePathTextField;

    @FXML
    private File selectedFile;

    @FXML
    private final FileChooser fileChooser = new FileChooser();

    @FXML
    private final Stage stage = new Stage();

    @FXML
    public void onRectifyButtonClick() {

    }

    @FXML
    public void onBrowseButtonClick(Stage stage) {
        File tempFile;
        tempFile = fileChooser.showOpenDialog(stage);
        if (tempFile != null) {
            selectedFile = tempFile;
            filePathTextField.setText(selectedFile.getPath());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
