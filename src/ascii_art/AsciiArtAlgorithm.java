package ascii_art;

import image.Image;
import image.ImageProcessor;
import image_char_matching.SubImgCharMatcher;

/**
 * A class for generating ASCII art from images. It converts images into ASCII characters by mapping
 * the pixel brightness to characters.
 * The process involves adjusting the image resolution and using a character matcher for the conversion.
 * Users can customize the output detail level through the resolution parameter.
 */

public class AsciiArtAlgorithm {
    private final SubImgCharMatcher charMatcher ;
    private final ImageProcessor imageProcessor;
    /**
     * Constructs an AsciiArtAlgorithm object with the specified parameters.
     * @param charMatcher the character matcher used to map image brightness to ASCII characters
     * @param imageProcessor the image processor for the current image
     */
    public AsciiArtAlgorithm(SubImgCharMatcher charMatcher, ImageProcessor imageProcessor ) {
        this.charMatcher = charMatcher;
        this.imageProcessor=imageProcessor;
    };

    /**
     * Runs the ASCII art algorithm on the provided image.
     * @return a 2D character array representing the ASCII art
     */
    public char[][] run(){
        // Initialize result array
        double[][] brightnessMatrix = imageProcessor.getBrightness();
        char[][] asciiArt = new char[brightnessMatrix.length][brightnessMatrix[0].length];

        // Iterate over the brightness for each pixel and replace with appropriate ASCII characters
        charMatcher.initializeBrightnessMap();
        for (int row = 0; row < brightnessMatrix.length; row++) {
            for (int col = 0; col < brightnessMatrix[row].length; col++) {
                // Use brightness to get ASCII character
                asciiArt[row][col] = charMatcher.getCharByImageBrightness(brightnessMatrix[row][col]);
            }
        }
        return asciiArt;
    }
}