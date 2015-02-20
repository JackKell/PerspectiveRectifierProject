package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class RectifierGUIController implements Initializable {

	private Parent root;

    @FXML
    public ImageView originalImageView;
    private Image originalImage;

    @FXML
    public ImageView rectifiedImageView;
    private WritableImage rectifiedImage;

    @FXML
    public Button rectifyButton;

    @FXML
    public Button browseButton;

    @FXML
    public Button loadButton;
    
    @FXML
    public Button exportButton;

    @FXML
    public TextField filePathTextField;
    
    @FXML
    public TextField exportFileNameTextField;

    private File selectedFile;
    
    

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
        PixelReader reader = originalImage.getPixelReader();
        int width = (int)originalImage.getWidth();
        int height = (int)originalImage.getHeight();

        rectifiedImage = new WritableImage(width, height);
        PixelWriter writer = rectifiedImage.getPixelWriter();

        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color trueColor = reader.getColor(x, y);
                //Mod Color can be used to change true color to a different value if need be
                Color modColor = new Color(
                        new Random().nextDouble() * 1,
                        new Random().nextDouble() * 1,
                        new Random().nextDouble() * 1,
                        trueColor.getOpacity()
                );
                if (y < (height) /2) {
                    writer.setColor(x, y, trueColor);
                    writer.setColor(x, height - 1 - y, trueColor);
                }
            }
        }
        rectifiedImageView.setImage(rectifiedImage);
    }

    @FXML
    public void onExportButtonClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(root.getScene().getWindow());
        
        System.out.println(selectedDirectory.getAbsolutePath());
        
        File outputDirectory = new File(selectedDirectory.getPath() + File.separator + exportFileNameTextField.getText() +  ".png");

        System.out.println(outputDirectory.getAbsolutePath());

        BufferedImage bImage = SwingFXUtils.fromFXImage(rectifiedImage, null);
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
