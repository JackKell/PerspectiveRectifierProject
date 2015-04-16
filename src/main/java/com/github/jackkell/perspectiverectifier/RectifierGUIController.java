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
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import util.MatrixSizeException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
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

	public DecimalFormat dFormat = new DecimalFormat("####.000");



	@Override
	public void initialize(URL location, ResourceBundle resources) {
		rectifyToggleButton.setSelected(false);
		rectifyToggleButton.setText("Off");

		mirrorToggleButton.setSelected(false);
		mirrorToggleButton.setText("Off");

		vanishingPointXSlider.setDisable(!rectifyToggleButton.isSelected());
		txtVPX.setDisable(!rectifyToggleButton.isSelected());

		verticalShearSlider.setDisable(!rectifyToggleButton.isSelected());
		txtShear.setDisable(!rectifyToggleButton.isSelected());

		verticalShiftSlider.setDisable(!rectifyToggleButton.isSelected());
		txtVerticalShift.setDisable(!rectifyToggleButton.isSelected());

		horizontalShiftSlider.setDisable(!rectifyToggleButton.isSelected());
		txtHorizontalShift.setDisable(!rectifyToggleButton.isSelected());

		scaleSlider.setDisable(!rectifyToggleButton.isSelected());
		txtScale.setDisable(!rectifyToggleButton.isSelected());

		txtRotation.setText(Math.round(rotationSlider.getValue()) + "");
		txtVPX.setText(dFormat.format(vanishingPointXSlider.getValue()));
		txtShear.setText(dFormat.format(verticalShearSlider.getValue()));
		txtVerticalShift.setText(dFormat.format(verticalShiftSlider.getValue()));
		txtHorizontalShift.setText(dFormat.format(horizontalShiftSlider.getValue()));
		txtScale.setText(dFormat.format(scaleSlider.getValue()) + "");

        rotationSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				txtRotation.setText(Math.round(rotationSlider.getValue()) + "");
				if(Main.hasOriginalImage()) {
					createRectifiedImage();
					showRectifiedImage();
				}
			}
		});
		txtRotation.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					rotationSlider.setValue(Double.parseDouble(txtRotation.getText()));
					if(Main.hasOriginalImage()) {
						createRectifiedImage();
						showRectifiedImage();
					}
				}
			}
		});

		vanishingPointXSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				txtVPX.setText(dFormat.format(vanishingPointXSlider.getValue()));
				if(Main.hasOriginalImage()) {
					createRectifiedImage();
					showRectifiedImage();
				}
			}
		});
		txtVPX.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					vanishingPointXSlider.setValue(Double.parseDouble(txtVPX.getText()));
					if(Main.hasOriginalImage()) {
						createRectifiedImage();
						showRectifiedImage();
					}
				}
			}
		});

		verticalShearSlider.valueProperty().addListener((new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				txtShear.setText(dFormat.format(verticalShearSlider.getValue()));
				if(Main.hasOriginalImage()) {
					createRectifiedImage();
					showRectifiedImage();
				}
			}
		}));
		txtShear.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					verticalShearSlider.setValue(Double.parseDouble(txtShear.getText()));
					if(Main.hasOriginalImage()) {
						createRectifiedImage();
						showRectifiedImage();
					}
				}
			}
		});

		verticalShiftSlider.valueProperty().addListener((new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				txtVerticalShift.setText(dFormat.format(verticalShiftSlider.getValue()));
				if(Main.hasOriginalImage()) {
					createRectifiedImage();
					showRectifiedImage();
				}
			}
		}));
		txtVerticalShift.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					vanishingPointXSlider.setValue(Double.parseDouble(txtVerticalShift.getText()));
					if(Main.hasOriginalImage()) {
						createRectifiedImage();
						showRectifiedImage();
					}
				}
			}
		});

		horizontalShiftSlider.valueProperty().addListener((new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				txtHorizontalShift.setText(dFormat.format(horizontalShiftSlider.getValue()));
				if(Main.hasOriginalImage()) {
					createRectifiedImage();
					showRectifiedImage();
				}
			}
		}));
		txtHorizontalShift.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					horizontalShiftSlider.setValue(Double.parseDouble(txtHorizontalShift.getText()));
					if(Main.hasOriginalImage()) {
						createRectifiedImage();
						showRectifiedImage();
					}
				}
			}
		});

		scaleSlider.valueProperty().addListener((new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				txtScale.setText(dFormat.format(scaleSlider.getValue()));
				if(Main.hasOriginalImage()) {
					createRectifiedImage();
					showRectifiedImage();
				}
			}
		}));
		txtScale.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					scaleSlider.setValue(Double.parseDouble(txtScale.getText()));
					if(Main.hasOriginalImage()) {
						createRectifiedImage();
						showRectifiedImage();
					}
				}
			}
		});

		mirrorToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue == true) {
					mirrorToggleButton.setText("On");
				} else {
					mirrorToggleButton.setText("Off");
				}

				if(Main.hasOriginalImage()) {
					createRectifiedImage();
					showRectifiedImage();
				}
			}
		});

		rectifyToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue == true) {
					rectifyToggleButton.setText("On");
				} else {
					rectifyToggleButton.setText("Off");
				}

				vanishingPointXSlider.setDisable(!rectifyToggleButton.isSelected());
				txtVPX.setDisable(!rectifyToggleButton.isSelected());

				verticalShearSlider.setDisable(!rectifyToggleButton.isSelected());
				txtShear.setDisable(!rectifyToggleButton.isSelected());

				verticalShiftSlider.setDisable(!rectifyToggleButton.isSelected());
				txtVerticalShift.setDisable(!rectifyToggleButton.isSelected());

				horizontalShiftSlider.setDisable(!rectifyToggleButton.isSelected());
				txtHorizontalShift.setDisable(!rectifyToggleButton.isSelected());

				scaleSlider.setDisable(!rectifyToggleButton.isSelected());
				txtScale.setDisable(!rectifyToggleButton.isSelected());

				if(Main.hasOriginalImage()) {
					createRectifiedImage();
					showRectifiedImage();
				}
			}
		});
    }

    @FXML
    public void onBrowseButtonClick() {
		// Creates the file chooser for choosing an image
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg")
				//new FileChooser.ExtensionFilter("PNG File", "*.png"),
				//new FileChooser.ExtensionFilter("JPEG File", "*.jpeg")
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
