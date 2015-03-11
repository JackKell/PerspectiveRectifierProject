package com.github.jackkell.perspectiverectifier;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import util.Pair;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Created by Nathan on 3/10/2015.
 */
public class ImageViewDraw {

    double initX;
    double initY;

    public void start(ImageView canvas, AnchorPane anchorPane) {

        final double minX = canvas.getLayoutX();
        final double minY = canvas.getLayoutY();
        double tempMaxX = canvas.getFitWidth() + minX;
        double tempMaxY = canvas.getFitHeight() + minY;

        if(canvas.getImage().getWidth() > canvas.getFitWidth()) {

            double ratio = canvas.getImage().getWidth() / canvas.getFitWidth();
            tempMaxY = canvas.getImage().getHeight() / ratio + minY;

            if(tempMaxY > canvas.getFitHeight()) {
                ratio = canvas.getImage().getHeight() / canvas.getFitHeight();
                tempMaxX = canvas.getImage().getWidth() / ratio;
                tempMaxY = canvas.getFitHeight() + minY;
            }
        }

        else {
            tempMaxX = canvas.getImage().getWidth() + minX;
            tempMaxY = canvas.getImage().getHeight() + minY;
        }

        final double maxX = tempMaxX;
        final double maxY = tempMaxY;

        Pair[] cornerPointsArray = new Pair[4];

        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                for (int i = 0; i < cornerPointsArray.length; i++) {
                    if(cornerPointsArray[i] == null) {
                        cornerPointsArray[i] = new Pair(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                        System.out.println(cornerPointsArray[i].getX() + ", " + cornerPointsArray[i].getY());

                        if(i != 0) {
                            Line line = new Line((Double)cornerPointsArray[i - 1].getX(), (Double)cornerPointsArray[i-1].getY(), (Double)cornerPointsArray[i].getX(), (Double)cornerPointsArray[i].getY());
                            line.setFill(Color.rgb(0,0,255));
                            line.setStroke(Color.rgb(0,0,255));
                            line.setStrokeWidth(1);
                            anchorPane.getChildren().add(line);
                        }

                        if(i == cornerPointsArray.length - 1) {
                            Line line = new Line((Double)cornerPointsArray[0].getX(), (Double)cornerPointsArray[0].getY(), (Double)cornerPointsArray[i].getX(), (Double)cornerPointsArray[i].getY());
                            line.setFill(Color.rgb(0,0,255));
                            line.setStroke(Color.rgb(0,0,255));
                            line.setStrokeWidth(1);
                            anchorPane.getChildren().add(line);
                        }

                        break;
                    }
                }
            }
        });

        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                initX = mouseEvent.getSceneX();
                initY = mouseEvent.getSceneY();
                //System.out.println(initX + ", " + initY);
                mouseEvent.consume();
            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getSceneX() < maxX && mouseEvent.getSceneY() < maxY
                        && mouseEvent.getSceneX() > minX && mouseEvent.getSceneY() > minY) {
                    //System.out.println(initX + ", " + initY);
                    Line line = new Line(initX, initY, mouseEvent.getX() + minX, mouseEvent.getY() + minY);
                    line.setFill(Color.rgb(0,0,255));
                    line.setStroke(Color.rgb(0,0,255));
                    line.setStrokeWidth(1);
                    //anchorPane.getChildren().add(line);
                }

                initX = mouseEvent.getSceneX() > maxX ? maxX : mouseEvent.getSceneX();
                initY = mouseEvent.getSceneY() > maxY ? maxY : mouseEvent.getSceneY();
            }
        });
    }
}
