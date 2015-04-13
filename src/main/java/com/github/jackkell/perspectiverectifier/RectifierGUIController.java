package com.github.jackkell.perspectiverectifier;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import util.MatrixSizeException;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RectifierGUIController implements Initializable {

	private Parent root;
    private RectifiedImage.Mode mode;
    private double rotation;
	private Point2D[] cornerPointsArray = new Point2D.Double[4];
	private int lines = 0;
	private int points = 0;

    @FXML
	public AnchorPane parentPane;
    public ImageView originalImageView;
    public ImageView rectifiedImageView;
    public ChoiceBox choiceBox;
	public ColorPicker cpColorPicker;
    public Slider slider;
    public Button browseButton;
    public Button exportButton;
    public Button clearButton;
	public Button rectifyButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
        choiceBox.setItems(FXCollections.observableArrayList(RectifiedImage.Mode.values()));
		choiceBox.getSelectionModel().select(RectifiedImage.Mode.NONE);
		mode = RectifiedImage.Mode.NONE;
        choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				mode = RectifiedImage.Mode.values()[newValue.intValue()];
				createRectifiedImage();
				showRectifiedImage();
			}
		});

		cpColorPicker.setValue(Color.RED);

        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                rotation = newValue.doubleValue();
                createRectifiedImage();
                showRectifiedImage();
            }
        });

		originalImageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				System.out.println(mouseEvent.getSceneX() + ", "  + mouseEvent.getSceneY());
				if(lines >= 4) {
					return;
				}

				if(cornerPointsArray[points] == null) {
					cornerPointsArray[points] = new Point2D.Double(mouseEvent.getSceneX() - originalImageView.getX(), mouseEvent.getSceneY() - originalImageView.getY());
					points++;

					if(points > 1) {
						Line line = new Line(cornerPointsArray[points - 2].getX(), cornerPointsArray[points - 2].getY(), cornerPointsArray[points - 1].getX(), cornerPointsArray[points - 1].getY());
						addLine(getStyledLine(line));
					}

					if(points == cornerPointsArray.length) {
						Line line = new Line(cornerPointsArray[0].getX(), cornerPointsArray[0].getY(), cornerPointsArray[points - 1].getX(), cornerPointsArray[points - 1].getY());
						addLine(getStyledLine(line));
					}
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
		removeLines();
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

    @FXML
    public void onClearButtonClick() {
		removeLines();
    }

	@FXML
	public void onRectifyButtonClick() {
		createRectifiedImage();
		showRectifiedImage();
	}

	private Line getStyledLine(Line line) {
		line.setFill(cpColorPicker.getValue());
		line.setStroke(cpColorPicker.getValue());
		line.setStrokeWidth(3);
		return line;
	}

	private void addLine(Line line) {
		parentPane.getChildren().add(line);
		lines++;
	}

	private void removeLines() {
		while(lines > 0) {
			for(int child = 0; child < parentPane.getChildren().size(); child++) {
				if(parentPane.getChildren().get(child).getClass() == (new Line()).getClass()) {
					parentPane.getChildren().remove(child);
					lines--;
				}
			}
		}

		for(int i = 0; i < points; i++) {
			cornerPointsArray[i] = null;
		}
		points = 0;
	}

    private void createRectifiedImage() {
		RectifiedImage.Builder builder = new RectifiedImage.Builder(Main.getOriginalImage())
			.rotation(rotation).mode(mode);

		if(cornerPointsArray.length == 4)
			builder = builder.points(cornerPointsArray);

        Main.setRectifiedImage(builder.build());
    }

    private void showRectifiedImage() {
        rectifiedImageView.setImage(Main.getRectifiedImage().getImage());
    }

	public void setParent(Parent parent) {
		root = parent;
	}
}
