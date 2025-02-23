package image_char_matching;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Inspired by, and partly copied from
 * https://github.com/korhner/asciimg/blob/95c7764a6abe0e893fae56b3b6b580e09e1de209/src/main/java/io/korhner
 * /asciimg/image/AsciiImgCache.java
 * described in the blog:
 * https://dzone.com/articles/ascii-art-generator-java
 * Adaptations made by Dan Nirel and again by Rachel Behar.
 * The class converts characters to a binary "image" (2D array of booleans).
 */
public class CharConverter {
    private static final double X_OFFSET_FACTOR = 0.2;
    private static final double Y_OFFSET_FACTOR = 0.75;
    private static final String FONT_NAME = "Courier New";
    /**
     * Default pixel resolution for character images,
     * defining both width and height of the generated square image in pixels.
     */
    public static final int DEFAULT_PIXEL_RESOLUTION = 16;

    /**
     * Renders a given character, according to how it looks in the font specified in the
     * constructor, to a square black&white image (2D array of booleans),
     * whose dimension in pixels is specified.
     */
    public static boolean[][] convertToBoolArray(char c) {
        BufferedImage img = getBufferedImage(c, FONT_NAME, DEFAULT_PIXEL_RESOLUTION);
        boolean[][] matrix = new boolean[DEFAULT_PIXEL_RESOLUTION][DEFAULT_PIXEL_RESOLUTION];
        for(int y = 0 ; y < DEFAULT_PIXEL_RESOLUTION ; y++) {
            for(int x = 0 ; x < DEFAULT_PIXEL_RESOLUTION ; x++) {
                matrix[y][x] = img.getRGB(x, y) == 0; //is the color black
            }
        }
        return matrix;
    }

    /**
     * Converts a character to a boolean 2D array representing a square black-and-white image.
     * The character is rendered in a specified font and resolution. Each array element represents
     * a pixel, where `true` indicates a black pixel and `false` indicates a white pixel.
     *
     * @param c The character to convert.
     * @return A 2D boolean array representing the character's image.
     */
    private static BufferedImage getBufferedImage(char c, String fontName, int pixelsPerRow) {
        String charStr = Character.toString(c);
        Font font = new Font(fontName, Font.PLAIN, pixelsPerRow);
        BufferedImage img = new BufferedImage(pixelsPerRow, pixelsPerRow, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setFont(font);
        int xOffset = (int)Math.round(pixelsPerRow *X_OFFSET_FACTOR);
        int yOffset = (int)Math.round(pixelsPerRow *Y_OFFSET_FACTOR);
        g.drawString(charStr, xOffset, yOffset);
        return img;
    }

}
