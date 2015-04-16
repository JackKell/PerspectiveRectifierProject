package com.github.jackkell.perspectiverectifier;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import util.MatrixSizeException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RectifierGUIController implements Initializable {

	private Parent root;

    @FXML
	public AnchorPane parentPane;
    public ImageView originalImageView;
    public ImageView rectifiedImageView;

    public Slider rotationSlider;
	public Slider vanishingPointXSlider;
	public Slider verticalShearSlider;
	public Slider verticalShiftSlider;
	public Slider horizontalShiftSlider;
	public Slider scaleSlider;

	public TextField txtRotation;
	public TextField txtVPX;
	public TextField txtShear;
	public TextField txtVerticalShift;
	public TextField txtHorizontalShift;
	public TextField txtScale;

    public Button browseButton;
    public Button exportButton;

	public ToggleButton mirrorToggleButton;
	public ToggleButton rectifyToggleButton;

	private final String TOGGLE_ON = "On";
	private final String TOGGLE_OFF = "Off";

	private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("####.000");
	private Map<Slider, TextField> sliders = new HashMap<>();
	private Set<ToggleButton> toggleButtons = new HashSet<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sliders.put(rotationSlider, txtRotation);
		sliders.put(vanishingPointXSlider, txtVPX);
		sliders.put(verticalShearSlider, txtShear);
		sliders.put(verticalShiftSlider, txtVerticalShift);
		sliders.put(horizontalShiftSlider, txtHorizontalShift);
		sliders.put(scaleSlider, txtScale);

		toggleButtons.add(rectifyToggleButton);
		toggleButtons.add(mirrorToggleButton);

		for(ToggleButton toggleButton : toggleButtons) {
			toggleButton.setSelected(false);
			toggleButton.setText(TOGGLE_OFF);
		}

		txtRotation.setText(DECIMAL_FORMAT.format(rotationSlider.getValue()));
		txtVPX.setText(DECIMAL_FORMAT.format(vanishingPointXSlider.getValue()));
		txtShear.setText(DECIMAL_FORMAT.format(verticalShearSlider.getValue()));
		txtVerticalShift.setText(DECIMAL_FORMAT.format(verticalShiftSlider.getValue()));
		txtHorizontalShift.setText(DECIMAL_FORMAT.format(horizontalShiftSlider.getValue()));
		txtScale.setText(DECIMAL_FORMAT.format(scaleSlider.getValue()));

		toggleSliders();

		for(Slider slider : sliders.keySet()) {
			TextField textField = sliders.get(slider);

			// Sets the listeners for the sliders
			slider.valueProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					textField.setText(DECIMAL_FORMAT.format(slider.getValue()));
					if(Main.hasOriginalImage()) {
						createRectifiedImage();
						showRectifiedImage();
					}
				}
			});
			slider.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if(event.getButton() == MouseButton.SECONDARY) {
						slider.setValue(0);
					}
				}
			});

			// Sets the listeners for the text fields
			textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if(event.getCode() == KeyCode.ENTER) {
						slider.setValue(Double.parseDouble(textField.getText()));
						if(Main.hasOriginalImage()) {
							createRectifiedImage();
							showRectifiedImage();
						}
					}
				}
			});
		}

		// Sets the listeners for the toggle buttons
		for(ToggleButton toggleButton : toggleButtons) {
			toggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					String toggleText = newValue ? TOGGLE_ON : TOGGLE_OFF;
					toggleButton.setText(toggleText);

					if(toggleButton == rectifyToggleButton) {
						toggleSliders();
					}

					if(Main.hasOriginalImage()) {
						createRectifiedImage();
						showRectifiedImage();
					}
				}
			});
		}
    }

    @FXML
    public void onBrowseButtonClick() {
		// Creates the file chooser for choosing an image
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg")
		);

		// Opens the file chooser and grabs the image the user selected
		File file = fileChooser.showOpenDialog(root.getScene().getWindow());
		if (file == null)
			return;

		// Saves the image in the program
		Image originalImage = new Image(file.toURI().toString());
		Main.setOriginalImage(originalImage);

		// Shows the path text and saves the original image in the program
		originalImageView.setImage(originalImage);

		// Centers the image in the tiny space it has
		double imageViewOffset = originalImageView.getBoundsInParent().getWidth() / 2;
		originalImageView.setLayoutX(128 - imageViewOffset);

		createRectifiedImage();
		showRectifiedImage();
	}

    @FXML
    public void onExportButtonClick() throws MatrixSizeException {
		// Creates the file chooser for saving an image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("PNG File", "*.png"),
                new FileChooser.ExtensionFilter("JPG File", "*.jpg"),
                new FileChooser.ExtensionFilter("JPEG File", "*.jpeg")
		);

        // The directory the user chooses to save the file
        File file = fileChooser.showSaveDialog(root.getScene().getWindow());

        // Saves the image in the output directory
        if(file != null) {
            System.out.println(file.getAbsolutePath());

            BufferedImage bImage = SwingFXUtils.fromFXImage(Main.getRectifiedImage().getImage(), null);
            try {
                ImageIO.write(bImage, "png", file);
            }
            catch (IOException e) {
                Logger.getLogger(RectifierGUIController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

	// Toggles all the sliders and text fields that need to be toggled
	private void toggleSliders() {
		for(Slider slider : sliders.keySet()) {
			TextField textField = sliders.get(slider);

			if(slider == rotationSlider)
				continue;

			slider.setDisable(!rectifyToggleButton.isSelected());
			textField.setDisable(!rectifyToggleButton.isSelected());
		}
	}

    private void createRectifiedImage() {
		RectifiedImage.Builder builder = new RectifiedImage.Builder(Main.getOriginalImage())
			.rotation(rotationSlider.getValue());

		if(mirrorToggleButton.isSelected()) {
			builder.mirror();
		}

		if(rectifyToggleButton.isSelected()) {
			builder.changePerspective();
		}

		if (!txtVPX.getText().equals("")) {
			builder.vpX(Double.parseDouble(txtVPX.getText()));
		}

		if (!txtShear.getText().equals("")) {
			builder.shear(Double.parseDouble(txtShear.getText()));
		}

		if (!txtVerticalShift.getText().equals("")) {
			builder.verticalShift(Double.parseDouble(txtVerticalShift.getText()));
		}

		if (!txtHorizontalShift.getText().equals("")) {
			builder.horizontalShift(Double.parseDouble(txtHorizontalShift.getText()));
		}

		if (!txtScale.getText().equals("")) {
			builder.scale(Double.parseDouble(txtScale.getText()));
		}

        Main.setRectifiedImage(builder.build());
    }

    private void showRectifiedImage() {
        rectifiedImageView.setImage(Main.getRectifiedImage().getImage());
    }

	public void setParent(Parent parent) {
		root = parent;
	}
}
