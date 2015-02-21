package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RectifierGUIController implements Initializable {

	private Parent root;

    @FXML
    public ImageView originalImageView;

    @FXML
    public ImageView rectifiedImageView;

    @FXML
    public Button rectifyButton;

    @FXML
    public Button browseButton;
    
    @FXML
    public Button exportButton;

    @FXML
    public TextField filePathTextField;
    
    @FXML
    public TextField exportFileNameTextField;

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
                new FileChooser.ExtensionFilter("JPEG File", "*jpeg")
        );

        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file == null) {
            return;
        }

        filePathTextField.setText(file.getPath());
        loadImage(file);
    }

    private void loadImage(File file) {
        Main.setRectifiedImage(new Image(file.toURI().toString()));
        originalImageView.setImage(Main.getRectifiedImage().getOriginalImage());
    }

    @FXML
    public void onRectifyButtonClick() {
        RectifiedImage image = Main.getRectifiedImage();
        rectifiedImageView.setImage(image.getModifiedImage());
    }

    @FXML
    public void onExportButtonClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        // The directory the dialog opens with
        File selectedDirectory = directoryChooser.showDialog(root.getScene().getWindow());
        System.out.println(selectedDirectory.getAbsolutePath());

        // The directory the user chooses to save the file
        File outputDirectory = new File(selectedDirectory.getPath() + File.separator + exportFileNameTextField.getText() +  ".png");
        System.out.println(outputDirectory.getAbsolutePath());

        // Saves the image in the output directory
        BufferedImage bImage = SwingFXUtils.fromFXImage(Main.getRectifiedImage().getModifiedImage(), null);
        try {
            ImageIO.write(bImage, "png", outputDirectory);
        }
        catch (IOException e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }
    
	public void setParent(Parent parent) {
		root = parent;
	}
}
