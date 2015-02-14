package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class RectifierGUIController implements Initializable {

	private Parent root;

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

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

    @FXML
    public void onRectifyButtonClick() {

    }

    @FXML
    public void onBrowseButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG File", "*.png"),
                new FileChooser.ExtensionFilter("JPG File", "*.jpg"),
                new FileChooser.ExtensionFilter("JPEG File", "*jpeg"));

        File file = fileChooser.showOpenDialog(root.getScene().getWindow()); //showOpenMultipleDialog(root.getScene().getWindow());

        if (file == null) {
            return;
        }

        filePathTextField.setText(selectedFile.getPath());

        // update view!!!

    }

    /* EXAMPLE CODE
    @FXML public void openJavaFileChooser() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open a Java File");
		fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Java File", "*.java"));
		List<File> files = fileChooser.showOpenMultipleDialog(root.getScene().getWindow());

		if(files.isEmpty()) {
			return;
		}

		for(File file : files) {
			addJavaFile(file);
		}

		updateView(javaFilesView, javaFiles);
	}
    */

	public void setParent(Parent parent) {
		root = parent;
	}
}
