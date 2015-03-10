package com.github.jackkell.perspectiverectifier;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.MatrixSizeException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RectifierGUIController implements Initializable {

	private Parent root;
    private ImageRectifier.RectifyMode mode;
    private double rotation;

    @FXML
    public ImageView originalImageView;

    @FXML
    public ImageView rectifiedImageView;

    @FXML
    public ChoiceBox choiceBox;

    @FXML
    public Slider slider;

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
        choiceBox.setItems(FXCollections.observableArrayList(ImageRectifier.RectifyMode.values()));
        choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mode = ImageRectifier.RectifyMode.values()[newValue.intValue()];
                createRectifiedImage();
                showRectifiedImage();
            }
        });

        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                rotation = newValue.doubleValue();
                createRectifiedImage();
                showRectifiedImage();
            }
        });
    }

    @FXML
     public void onBrowseButtonClick() {
        // Creates the file chooser for choosing an image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG File", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG File", "*.png"),
                new FileChooser.ExtensionFilter("JPEG File", "*jpeg")
        );

        // Opens the file chooser and grabs the image the user selected
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file == null)
            return;

        // Saves the image in the program
        Image originalImage = new Image(file.toURI().toString());
        ImageRectifier imgRectifier = Main.getImageRectifier();
        imgRectifier.setOriginalImage(originalImage);

        // Shows the path text and saves the original image in the program
       //filePathTextField.setText(file.getPath());
        originalImageView.setImage(originalImage);

        createRectifiedImage();
        showRectifiedImage();
    }

    @FXML
    public void onExportButtonClick() throws MatrixSizeException {
        //DirectoryChooser directoryChooser = new DirectoryChooser();

        // The directory the dialog opens with
        //File selectedDirectory = directoryChooser.showDialog(root.getScene().getWindow());
        //System.out.println(selectedDirectory.getAbsolutePath());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG File", "*.png"),
                new FileChooser.ExtensionFilter("JPG File", "*.jpg"),
                new FileChooser.ExtensionFilter("JPEG File", "*.jpeg"));

        // The directory the user chooses to save the file
        File file = fileChooser.showSaveDialog(root.getScene().getWindow());
        System.out.println(file.getAbsolutePath());

        // Saves the image in the output directory
        BufferedImage bImage = SwingFXUtils.fromFXImage(Main.getImageRectifier().getRectifiedImage(), null);
        try {
            ImageIO.write(bImage, "png", file);
        }
        catch (IOException e) {
            Logger.getLogger(RectifierGUIController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void createRectifiedImage() {
        ImageRectifier imgRectifier = Main.getImageRectifier();
        imgRectifier.create(mode, rotation);
    }

    private void showRectifiedImage() {
        WritableImage rectifiedImage = Main.getImageRectifier().getRectifiedImage();
        rectifiedImageView.setImage(rectifiedImage);
    }

	public void setParent(Parent parent) {
		root = parent;
	}
}
