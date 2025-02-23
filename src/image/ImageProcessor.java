package image;

import java.awt.Color;

/**
 * The ImageProcessor class represents a utility for processing images.
 * It provides methods for padding images, splitting them into sub-images,
 * and calculating the brightness matrix.
 */
public class ImageProcessor {

    private static final int MAX_RGB_VALUE = 255;
    private static final double RED_VALUE=0.2126;
    private static final double GREEN_VALUE=0.7152;
    private static final double BLUE_VALUE=0.0722;
    private Image image;
    private final int resolution;
    private final double[][] brightnessMatrix;
    private int newWidth;
    private int newHeight;

    /**
     * Constructs an ImageProcessor with the specified image and resolution.
     *
     * @param image The image to be processed.
     * @param resolution The desired resolution for processing.
     */
    public ImageProcessor(Image image, int resolution) {
        this.image = image;
        this.resolution = resolution;
        padImageToPowerOfTwo();
        Image[][] subImages = splitImage();
        this.brightnessMatrix = calculateBrightness(subImages);
    }

    /**
     * Pads the image to the nearest power of two dimensions.
     */
    private void padImageToPowerOfTwo() {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        // Calculate the new dimensions to pad the image
        newWidth = getNextPowerOfTwo(originalWidth);
        newHeight = getNextPowerOfTwo(originalHeight);

        //pr rajouter equitablement des deux cotes
        int paddingWidth = (newWidth - originalWidth) / 2;
        int paddingHeight = (newHeight - originalHeight) / 2;

        // Create a new Color array for the padded image
        Color[][] paddedPixels = new Color[newHeight][newWidth];

        // Initialize all pixels to white for padding
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                paddedPixels[i][j] = Color.WHITE;
            }
        }
        // Copy the original pixels to the centered position in the new array
        for (int i = 0; i < originalHeight; i++) {
            for (int j = 0; j < originalWidth; j++) {
                paddedPixels[i + paddingHeight][j + paddingWidth] = image.getPixel(i, j);
            }
        }
        // Create and save a new Image object with the padded pixels
        this.image = new Image(paddedPixels, newWidth, newHeight);
    }

    /**
     * Gets the next power of two for a given number.
     *
     * @param n The input number.
     * @return The next power of two greater than or equal to the input number.
     */
    private int getNextPowerOfTwo(int n) {
        //if number is power of 2, don't change
        if((n & (n - 1)) == 0){
            return n;
        }
        //else, find the closest power of two
        int nextPowOfTwo = 1;
        while (nextPowOfTwo < n) {
            nextPowOfTwo <<= 1;
        }
        return nextPowOfTwo;
    }

    /**
     * Splits the image into sub-images.
     *
     * @return A 2D array of sub-images.
     */
    private Image[][] splitImage() {
        int subImageSize = image.getWidth() / resolution;
        int numOfRows = image.getHeight() / subImageSize;
        Image[][] subImages = new Image[numOfRows][resolution];

        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < resolution; col++) {
                Color[][] subImagePixels = new Color[subImageSize][subImageSize];
                // sub images
                for (int i = 0; i < subImageSize; i++) {
                    for (int j = 0; j < subImageSize; j++) {
                        subImagePixels[i][j] = image.getPixel(row * subImageSize + i, col * subImageSize + j);
                    }
                }
                subImages[row][col] = new Image(subImagePixels, subImageSize, subImageSize);
            }
        }
        return subImages;
    }
    /**
     * Calculates the brightness matrix for the sub-images.
     *
     * @param splittedImages The array of sub-images.
     * @return The brightness matrix representing the brightness of each sub-image.
     */
    private double[][] calculateBrightness(Image[][] splittedImages) {
        double[][] brightnessArray = new double[splittedImages.length][splittedImages[0].length];
        int subImageSize = image.getWidth() / resolution;

        for (int row = 0; row < splittedImages.length; row++) {
            for (int col = 0; col < splittedImages[0].length; col++) {
                double brightnessSum = getBrightnessSum(splittedImages, row, col);
                brightnessArray[row][col] = brightnessSum /(subImageSize*subImageSize* MAX_RGB_VALUE);
            }
        }
        return brightnessArray;
    }

    /**
     * Calculates the sum of brightness values for a specific sub-image.
     *
     * @param splittedImages The array of sub-images.
     * @param row The row index of the sub-image.
     * @param col The column index of the sub-image.
     * @return The sum of brightness values for the specified sub-image.
     */
    private static double getBrightnessSum(Image[][] splittedImages, int row, int col) {
        Image image = splittedImages[row][col];
        double brightnessSum = 0;
        int width = image.getWidth();
        int height = image.getHeight();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double greyPixel = (((double) image.getPixel(i,j).getRed() * RED_VALUE) +
                        ((double) image.getPixel(i,j).getGreen() * GREEN_VALUE) +
                        ((double) image.getPixel(i,j).getBlue() * BLUE_VALUE));
                brightnessSum += greyPixel;
            }
        }
        return brightnessSum;
    }

    /**
     * Retrieves the brightness matrix of the processed image.
     *
     * @return The brightness matrix.
     */
    public double[][] getBrightness() {
        return brightnessMatrix;
    }

    /**
     *getter that gets the new width after adding the padding
     */
    public int getNewWidth(){
        return newWidth;
    }

    /**
     *getter that gets the new height after adding the padding
     */
    public int getNewHeight(){
        return newHeight;
    }
}