/**Author: Patryk Stefanski
 * Assignment  1 for DataStructures&Algorithms 2
 * Simple fruit Analyser  that marks each fruit and presents a label
 */


package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public ImageView imageView1, imageView2;
    public Button chooseFileButton, exitButton, randomColorButton, analyse, showImage, showBWImage, colorSingleFruitButton;
    public TextArea FilePath, imageDetails, recommendedColor, fruitCount;
    public TextField hueIn, saturationIn, brightnessIn, hueInRange, saturationInRange, brightnessInRange, fruitID;


    public ChoiceBox chooseFruitBox;
    public AnchorPane analyseFruit;
    public CheckBox onScreenLabel;

    File file;



    Image img;
    PixelReader pr;
    WritableImage wimg, BW, BWwimg;
    PixelWriter pw, rectw;
    int imageWidth;
    int imageHeight;



    int[] disjointSet;
    int[] sortedCluster;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chooseFruitBox.getItems().addAll("Strawberry", "Plum", "Orange");


    }

    /** Choose image from computer files
     */

    public void chooseFile(ActionEvent actionEvent) {

        FileChooser fc = new FileChooser();
        Stage stg = new Stage();
        file = fc.showOpenDialog(stg);

        if (file != null) {
            img = new Image(file.toURI().toString(), 216, 216, false, true);
        }

        imageView1.setImage(img);
        pr = img.getPixelReader();
        imageWidth = (int) img.getWidth();
        imageHeight = (int) img.getHeight();
        wimg = new WritableImage(pr, imageWidth, imageHeight);
        BWwimg = new WritableImage(pr, imageWidth, imageHeight);
        pw = BWwimg.getPixelWriter();
        rectw = wimg.getPixelWriter();
    }

    /** Convert Image to black and white by checking if  hue,saturation and brightness of pixel is within selected range
     * by traversing an array and setting 1 and 0 accordingly in the array  , using pixelreader
     */
    public void imageToBW() {
        double hue = 0.00, brightness = 0.00, saturation = 0.00;
        BW = new WritableImage(pr, imageWidth, imageHeight);
        disjointSet = new int[imageWidth * imageHeight];
        int j = 0, HR = 0, SR = 0, BR = 0;

        if (!hueInRange.getText().isEmpty() && !saturationInRange.getText().isEmpty() && !brightnessInRange.getText().isEmpty()) {
            HR = Integer.parseInt(hueInRange.getText());
            SR = Integer.parseInt(saturationInRange.getText());
            BR = Integer.parseInt(brightnessInRange.getText());
        }

        if (chooseFruitBox.getValue() != null) {
            if (chooseFruitBox.getValue().toString().equals("Strawberry")) {
                hue = 355.00;
            }
            if (chooseFruitBox.getValue().toString().equals("Orange")) {
                hue = 35.00;
            }
            if (chooseFruitBox.getValue().toString().equals("Plum")) {
                hue = 242.00;
            }
        }

        if (!hueIn.getText().isEmpty()) {
            hue = Double.parseDouble(hueIn.getText());
        }

        if (hueIn.getText().isEmpty() && saturationIn.getText().isEmpty() && brightnessIn.getText().isEmpty()) {
            for (int row = 0; row < img.getHeight(); row++) {
                for (int col = 0; col < img.getWidth(); col++) {
                    Color color = pr.getColor(col, row);

                    if (color.getHue() >= hue - 10 && color.getHue() <= hue + 10) {
                        pw.setColor(col, row, Color.WHITE);
                        disjointSet[j] = j;

                    } else if (hue >= 350 && color.getHue() <= 10) {
                        pw.setColor(col, row, Color.WHITE);
                        disjointSet[j] = j;

                    } else if (hue <= 10 && color.getHue() >= 350) {
                        pw.setColor(col, row, Color.WHITE);
                        disjointSet[j] = j;

                    } else {
                        pw.setColor(col, row, Color.BLACK);
                        disjointSet[j] = -1;

                    }
                    j++;
                }
            }
        }

        if (!hueIn.getText().isEmpty() && !saturationIn.getText().isEmpty() && !brightnessIn.getText().isEmpty()) {
            for (int row = 0; row < img.getHeight(); row++) {
                for (int col = 0; col < img.getWidth(); col++) {
                    Color color = pr.getColor(col, row);

                    if (color.getHue() >= hue - HR && color.getHue() <= hue + HR && color.getBrightness() >= brightness - BR && color.getBrightness() <= brightness + BR && color.getSaturation() >= saturation - SR && color.getSaturation() <= saturation + SR) {
                        pw.setColor(col, row, Color.WHITE);
                        disjointSet[j] = j;

                    } else if (hue >= 340 && color.getHue() <= 10 && color.getBrightness() >= brightness - BR && color.getBrightness() <= brightness + BR && color.getSaturation() >= saturation - SR && color.getSaturation() <= saturation + SR) {
                        pw.setColor(col, row, Color.WHITE);
                        disjointSet[j] = j;

                    } else if (hue <= 20 && color.getHue() >= 340 && color.getBrightness() >= brightness - BR && color.getBrightness() <= brightness + BR && color.getSaturation() >= saturation - SR && color.getSaturation() <= saturation + SR) {
                        pw.setColor(col, row, Color.WHITE);
                        disjointSet[j] = j;

                    } else {
                        pw.setColor(col, row, Color.BLACK);
                        disjointSet[j] = -1;

                    }
                    j++;

                }
            }
        }
    }

    /** Groups all pixels next to another to one set by setting the same root values  to the same value if the values in the array are the same
     */
    public void groupPixels() {
        for (int i = 0; i < disjointSet.length; i++) {

            if (i < disjointSet.length - imageWidth && disjointSet[i] != -1 && disjointSet[i + imageWidth] != -1) {
                quickUnion(disjointSet, i, i + imageWidth);
            }
            if (i < disjointSet.length && disjointSet[i] != -1 && disjointSet[i + 1] != -1) {
                quickUnion(disjointSet, i, i + 1);
            }
        }
    }

    /** Draws a rectangle around the fruit  and inserts  a tooltip with fruit size and size rank
     */

    public void drawRectangle() {
        int maxHeight, minHeight, maxWidth, minWidth;
        removeRectangles();
         int fruitCounter=0;


        for (int i = 0; i < sortedCluster.length; i++) {
            maxHeight = sortedCluster[i] / imageWidth;
            minHeight = sortedCluster[i] / imageWidth;
            maxWidth = sortedCluster[i] % imageWidth;
            minWidth = sortedCluster[i] % imageWidth;

            for (int j = 0; j < disjointSet.length; j++) {
                if (find(disjointSet, j) == sortedCluster[i]) {
                    if (j / imageWidth < maxHeight) {
                        maxHeight = j / imageWidth;
                    }
                    if (j / imageWidth > minHeight) {
                        minHeight = j / imageWidth;
                    }
                    if (j % imageWidth < minWidth) {
                        minWidth = j % imageWidth;
                    }
                    if (j % imageWidth > maxWidth) {
                        maxWidth = j % imageWidth;
                    }
                }
            }

            if (pixelCounter(sortedCluster[i])>40) {
                fruitCounter++;

                Rectangle theRect = new Rectangle(minWidth, maxHeight, maxWidth - minWidth, minHeight - maxHeight);

                theRect.setFill(Color.TRANSPARENT);
                theRect.setStroke(Color.BLUE);
                theRect.setTranslateX(imageView2.getLayoutX());  //you need to do a translation so that the rect is written to
                theRect.setTranslateY(imageView2.getLayoutY());  //image and not the form
                Tooltip.install(theRect, new Tooltip("Fruit Cluster Number : " + (i + 1) + "\n" + "Estimated size(pixels) : " + pixelCounter(sortedCluster[i]) + "\n"));

                ((Pane) imageView2.getParent()).getChildren().add(theRect);

                if(onScreenLabel.isSelected()) {
                    javafx.scene.control.Label label = new javafx.scene.control.Label();
                    label.setTranslateX(imageView2.getLayoutX());
                    label.setTranslateY(imageView2.getTranslateY());
                    ((Pane) imageView2.getParent()).getChildren().addAll(label);
                    label.setLayoutX(maxWidth + 1);
                    label.setLayoutY(maxHeight);
                    label.setText(String.valueOf(i + 1));
                    label.setTextFill(Color.WHITE);
                }
            }
        }
        fruitCount.setText("Number of fruit/s in image : " + fruitCounter);
    }

    /** Colors  separate fruits in B&W image with random rgb values
     */
    public void colorClusterRandom() {
        Random random = new Random();

        for (int i = 0; i < sortedCluster.length; i++) {
            int r = random.nextInt(255);
            int g = random.nextInt(255);
            int b = random.nextInt(255);
            Color c = Color.rgb(r, g, b);
            for (int j = 0; j < disjointSet.length; j++) {
                if (find(disjointSet, j) == sortedCluster[i]) {
                    BWwimg.getPixelWriter().setColor(j % imageWidth, j / imageWidth, c);
                }
            }
        }
    }
    /** Colors a single fruit  chosen by user by inputting fruit id
     */
    public void colorSingleFruit(ActionEvent actionEvent) {
        int i = Integer.parseInt(fruitID.getText());
        for (int j = 0; j < disjointSet.length; j++) {
            if (find(disjointSet, j) == sortedCluster[i - 1]) {
                BWwimg.getPixelWriter().setColor(j % imageWidth, j / imageWidth, Color.AQUA);
            }
        }
    }

    /** Removes rectangles created from previous fruit analysis
     */
    public void removeRectangles() {
        List<Rectangle> rectList = new ArrayList<>();
        List<Label> labelList = new ArrayList<>();

        for (Node y : ((AnchorPane) imageView2.getParent()).getChildren()) {
            if (y instanceof javafx.scene.control.Label)
                labelList.add((javafx.scene.control.Label) y);
        }
        for (Node x : ((AnchorPane) imageView2.getParent()).getChildren()) {
            if (x instanceof Rectangle)
                rectList.add((Rectangle) x);
        }
        ((Pane) imageView2.getParent()).getChildren().removeAll(rectList);
        ((Pane) imageView2.getParent()).getChildren().removeAll(labelList);

    }

    /** Counts pixels in a disjoint set
     */
    public int pixelCounter(int i) {
        int pixelCount = 0;
        for (int j = 0; j < disjointSet.length; j++) {
            if (find(disjointSet, j) == i) {
                pixelCount++;
            }
        }
        return pixelCount;
    }

    /** Sort pixel clusters from big to small
     */
    public void sortClusters() {
        sortedCluster = new int[countFruit()];
        int j = 0;

        for (int i = 0; i < disjointSet.length - 1; i++) {
            if (disjointSet[i] == i) {
                sortedCluster[j] = i;
                if (j < countFruit() - 1) {
                    j++;
                }
            }
        }

        int n = sortedCluster.length;
        for (int i = 0; i < n - 1; i++) {
            for (int l = 0; l < n - i - 1; l++) {
                if (pixelCounter(sortedCluster[l]) < pixelCounter(sortedCluster[l + 1])) {
                    int temp = sortedCluster[l + 1];
                    sortedCluster[l + 1] = sortedCluster[l];
                    sortedCluster[l] = temp;
                }
            }
        }

    }


    /** Union by height  from lecture notes
     */
    public static void quickUnion(int[] a, int p, int q) {
        a[find(a, q)] = find(a, p); //The root of q is made reference the root of p

    }

    /** Find  with path compression from lecture notes
     */
    public static int find(int[] a, int id) {
        if (a[id] == -1) {
            id = -1;
            return id;
        }
        while (a[id] != id) {
            a[id] = a[a[id]];
            id = a[id];
        }
        return id;
    }
    /** Counts amount of fruits in a image
     */
    public int countFruit() {
        int j = 0;

        for (int i = 0; i < disjointSet.length; i++) {
            if (disjointSet[i] == i ) {
                j++;
            }
        }
        return j;
    }


    public void showBWImage(ActionEvent actionEvent) {
        imageView2.setImage(BWwimg);
    }


    public void showImage(ActionEvent actionEvent) {
        imageView2.setImage(wimg);
    }

    /** Simple exit
     */
    public void exit(ActionEvent actionEvent) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();

    }

    /** Calls all functions necessary to successfully analyse a image for fruit
     */
    public void analyse(ActionEvent actionEvent) {

        imageToBW();
        groupPixels();

        sortClusters();
        drawRectangle();
        imageDetails.setText("Width : " + imageWidth + " Height : " + imageHeight + " File Name : " + file.getName());


//        for (int i = 0; i < disjointSet.length; i++) {
//            System.out.print(find(disjointSet, i) + ((i + 1) % imageWidth == 0 ? "\n" : " "));
//        }
//        for (int i = 0; i < disjointSet.length; i++) {
//            System.out.print(disjointSet[i] + ((i+1 ) % imageWidth == 0 ? "\n" : " "));
//        }


    }

    /** Retrieves hue ,saturation and brightness from rgb values
     */
    public void rgb_to_hsv(double r, double g, double b) {

        // R, G, B values are divided by 255
        // to change the range from 0..255 to 0..1
        r = r / 255.0;
        g = g / 255.0;
        b = b / 255.0;

        // h, s, v = hue, saturation, value
        double cmax = Math.max(r, Math.max(g, b)); // maximum of r, g, b
        double cmin = Math.min(r, Math.min(g, b)); // minimum of r, g, b
        double diff = cmax - cmin; // diff of cmax and cmin.
        double h = -1, s = -1;

        // if cmax and cmax are equal then h = 0
        if (cmax == cmin)
            h = 0;

            // if cmax equal r then compute h
        else if (cmax == r)
            h = (60 * ((g - b) / diff) + 360) % 360;

            // if cmax equal g then compute h
        else if (cmax == g)
            h = (60 * ((b - r) / diff) + 120) % 360;

            // if cmax equal b then compute h
        else if (cmax == b)
            h = (60 * ((r - g) / diff) + 240) % 360;

        // if cmax equal zero
        if (cmax == 0)
            s = 0;
        else
            s = (diff / cmax) * 100;

        // compute v
        double v = cmax * 100;

        recommendedColor.setText(" Hue : " + Math.round(h) + " Saturation : " + Math.round(s) + "  Brightness : " + Math.round(v));

    }

    /** Retrieves rgb  value from mouse cursor position
     */
    public void getColorDetails() throws AWTException {
        Point P = MouseInfo.getPointerInfo().getLocation();
        Robot robot = new Robot();
        java.awt.Color r = robot.getPixelColor(P.getLocation().x, P.getLocation().y);
        rgb_to_hsv(r.getRed(), r.getGreen(), r.getBlue());

    }


    public int[] getDisjointSet() {
        return disjointSet;
    }

    public void setImg(Image img) {
        this.img = img;
    }


}

