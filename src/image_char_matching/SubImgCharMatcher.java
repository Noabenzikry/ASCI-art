package image_char_matching;

import java.util.*;


/**
 * A class for matching ASCII characters to image brightness values.
 */
public class SubImgCharMatcher {
    private char[] charset;
    private final Map<Character, Double> rawBrightnessMap = new HashMap<>();
    private final HashMap<Character, Double> normalizedBrightnessMap;
    private static final HashMap<char[], Double[]> charsetBrightnessMap = new HashMap<>();
    private final Set<Character> modifiedChars = new HashSet<>();
    private double minBrightness = Double.MAX_VALUE;
    private double maxBrightness = Double.MIN_VALUE;
    private boolean needToNormalize;

    /**
     * Constructor to initialize the SubImgCharMatcher with a given character set.
     *
     * @param charset The character set to use for matching.
     */
    public SubImgCharMatcher(char[] charset) {
        Arrays.sort(charset);
        this.charset = charset;
        this.normalizedBrightnessMap = new HashMap<>();
        
        for (char c : charset) {
            calculateBrightness(c);
        }
        Double[] normalizedBrightness = normalizeBrightness(); // normalizes these brightness values
        charsetBrightnessMap.put(charset, normalizedBrightness);
    }

    /**
     * Initializes the brightness map by calculating and storing the brightness values
     * for each character in the charset.
     */
    public void initializeBrightnessMap() {
        //check if we don't already have the charset
        char[] matchingCharset = null;
        boolean foundMatchingCharset = false;
        for (char[] storedCharset : charsetBrightnessMap.keySet()) {// Iterate over each charset
            Arrays.sort(storedCharset);
            if (Arrays.equals(storedCharset, charset)) {
                foundMatchingCharset = true;
                matchingCharset = storedCharset;
                break; // No need to continue searching
            }
        }
        //if already have the set , enter it brightnessMap (normalized value)
        if (foundMatchingCharset) {
            // fill brightnessMap with the normalized brightness values from the storedCharset
            for (int i = 0; i < charset.length; i++) {
                normalizedBrightnessMap.put(charset[i], charsetBrightnessMap.get(matchingCharset)[i]);
            }
            return; // Exit the method since we've already filled brightnessMap
        }

        //there's no matching charset found
        // we have removed/added the min or the max
        ifNoMatchingCharset();
    }

    /**
     * Optimizes normalization of brightness values to reduce time complexity. Handles three cases:
     * 1. Adjusts for changes in min/max values when elements are added or removed.
     * 2. Skips recalculation if no characters have been modified.
     * 3. Recalculates for newly added or modified characters without redoing all values.
     * Updates `charsetBrightnessMap` with the current charset's normalized brightness values,
     * avoiding unnecessary recalculations.
     */
    private void ifNoMatchingCharset() {
        if(needToNormalize){
            Double[] normalizedBrightness = normalizeBrightness();
            charsetBrightnessMap.put(Arrays.copyOf(charset, charset.length), normalizedBrightness);
            needToNormalize = false;
        }else if(modifiedChars.isEmpty()){
            Double[] brightnessArray = new Double[charset.length];
            for (int i = 0; i<charset.length; i++){
                brightnessArray[i] = normalizedBrightnessMap.get(charset[i]);
            }
            charsetBrightnessMap.put(Arrays.copyOf(charset, charset.length), brightnessArray);
        }else {
            for (char c : modifiedChars) {
                double brightness = calculateBrightness(c);
                double normalizedBrightness = (brightness - minBrightness) / (maxBrightness - minBrightness);
                normalizedBrightnessMap.put(c, normalizedBrightness);
            }
            Double[] brightnessArray = new Double[charset.length];
            for (int i = 0; i < charset.length; i++) {
                brightnessArray[i] = normalizedBrightnessMap.get(charset[i]);
            }
            charsetBrightnessMap.put(Arrays.copyOf(charset, charset.length), brightnessArray);
            modifiedChars.clear();
        }
    }


    /**
     * Calculates the brightness value for a given character.
     *
     * @param c The character for which to calculate brightness.
     * @return The brightness value of the character.
     */
    private double calculateBrightness(char c) {
        if(rawBrightnessMap.containsKey(c)){
            return rawBrightnessMap.get(c);
        }
        // Assume CharConverter.convertToBoolArray(c) converts character c to 16x16 boolean array
        boolean[][] boolArray = CharConverter.convertToBoolArray(c);
        double whitePixels = 0;
        double totalPixels = boolArray.length * boolArray[0].length;

        for (boolean[] row : boolArray) {
            for (boolean pixel : row) {
                if (pixel) {
                    whitePixels++;
                }
            }
        }
        double brightness = whitePixels /totalPixels;
        rawBrightnessMap.put(c, brightness);
        return brightness;

    }

    /**
     * Normalizes the brightness values of characters in the charset.
     */
    private Double[] normalizeBrightness() {
        for (char c : charset) {
            double brightness = rawBrightnessMap.get(c);
            if(brightness > maxBrightness) {
                maxBrightness = brightness;
            } if (brightness < minBrightness) {
                minBrightness = brightness;
            }
        }
        // Find min and max brightness values
        Double[] normalizedBrightness = new Double[charset.length];
        // Normalize brightness values
        for (int i = 0; i< charset.length; i++) {
            char c = charset[i];
            double brightness = rawBrightnessMap.get(c);
            double normalized = (brightness - minBrightness) /
                    (maxBrightness - minBrightness);
            normalizedBrightness[i] = normalized;
            normalizedBrightnessMap.put(c, normalized);
        }

        return normalizedBrightness;
    }

    /**
     * Finds the character in the charset that best matches the given brightness value.
     *
     * @param brightness The target brightness value to match.
     * @return The character that best matches the given brightness.
     */
    public char getCharByImageBrightness(double brightness) {
        char closestChar = charset[0];
        double minDiff = Double.MAX_VALUE;

        // Find the closest character to the given brightness
        for (char c : charset) {
            double charBrightness = normalizedBrightnessMap.get(c);
            double diff = Math.abs(charBrightness - brightness);

            // Update the closest character if found
            if (diff < minDiff) {
                minDiff = diff;
                closestChar = c;
            } else if (diff == minDiff && c < closestChar) {
                // If diff is the same, choose character with smaller ASCII value
                closestChar = c;
            }
        }
        return closestChar;
    }
    /**
     * Adds a character to the charset map and calculates its brightness value.
     *
     * @param c The character to add.
     */
    public void addChar(char c) {
        // Check if the character is already in the charset
        if (!normalizedBrightnessMap.containsKey(c)) {
            double brightness = calculateBrightness(c);
            if (brightness < minBrightness) {
                minBrightness = brightness;
                needToNormalize = true;
            } if (brightness > maxBrightness) {
                maxBrightness = brightness;
                needToNormalize = true;
            }
            char[] newCharset = Arrays.copyOf(charset, charset.length + 1);// Resize the charset
            newCharset[newCharset.length - 1] = c; // Add the character to the end of the new array
            charset = newCharset;  // Update the charset reference to point to the new array
            normalizedBrightnessMap.put(c, brightness);
            modifiedChars.add(c);
        }
    }

    /**
     * Removes a character from the charset.
     *
     * @param c The character to remove.
     */
    public void removeChar(char c) {
        double currBrightness = calculateBrightness(c);
        if(currBrightness==minBrightness){
            minBrightness = Double.MAX_VALUE;
            needToNormalize=true;
        } if(currBrightness==maxBrightness){
            maxBrightness=Double.MIN_VALUE;
            needToNormalize=true;
        }
        normalizedBrightnessMap.remove(c);
        // Remove the character from the charset array
        int index = -1;
        for (int i = 0; i < charset.length; i++) {
            if (charset[i] == c) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            char[] newCharset = new char[charset.length - 1];
            System.arraycopy(charset, 0, newCharset, 0, index);
            System.arraycopy(charset, index + 1, newCharset, index, charset.length - index - 1);
            charset = newCharset;
        }
    }

    /**
     * Returns the current character set.
     * @return The character set used by this instance.
     */
    public char[] getCharset() {
        return charset;
    }
}