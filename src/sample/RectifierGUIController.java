package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class RectifierGUIController implements Initializable {

	private Parent root;

    @FXML
    public ImageView originalImageView;
    private Image originalImage;

    @FXML
    public ImageView rectifiedImageView;
    private Image rectifitedImage;

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
    
    

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

    @FXML
     public void onBrowseButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG File", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG File", "*.png"),
                new FileChooser.ExtensionFilter("JPEG File", "*jpeg"));


        File file = fileChooser.showOpenDialog(root.getScene().getWindow()); //showOpenMultipleDialog(root.getScene().getWindow());

        if (file == null) {
            return;
        }

        selectedFile = file;

        filePathTextField.setText(selectedFile.getPath());
    }

    @FXML
    public void onLoadButtonClick() {
        originalImage = new Image(selectedFile.toURI().toString());
        originalImageView.setImage(originalImage);
    }

    @FXML
    public void onRectifyButtonClick() {
        rectifiedImageView.setImage(originalImage);
    }
    
	public void setParent(Parent parent) {
		root = parent;
	}
}
