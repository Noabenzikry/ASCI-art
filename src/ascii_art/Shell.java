package ascii_art;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import image.ImageProcessor;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;
import java.util.Arrays;

/**
 * The Shell class represents a command-line interface for creating ASCII art from images.
 * It provides various commands to manipulate the image, character set, output method, and resolution.
 */
public class Shell {
    // Constants for command keywords
    private static final String EXIT_COMMAND = "exit";
    private static final String COMMAND_INPUT = ">>> ";
    private static final String VIEW_CHARS = "chars";
    private static final String ADD_COMMAND = "add";
    private static final String REMOVE_COMMAND = "remove";
    private static final String RES_COMMAND = "res";
    private static final String IMG_COMMAND = "image";
    private static final String OUTPUT_COMMAND = "output";
    private static final String RUN_ALG_COMMAND = "asciiArt";
    private static final int MIN_ASCI_VAL = 32;
    private static final int MAX_ASCI_VAL = 127;
    private static final String PRINT_INCORRECT_COMMAND= "Did not execute due to incorrect command.";
    private static final String PRINT_INCORRECT_FORMAT="Did not change resolution due to incorrect format.";
    private static final String PRINT_INCORRECT_ADD_FORMAT="Did not add due to incorrect format.";
    private static final String PRINT_INCORRECT_RES= "Did not change resolution due to exceeding boundaries.";
    private static final String PRINT_INCORRECT_CHARSET="Did not execute. Charset is empty.";
    private static final String PRINT_INCORRECT_REMOVE_FORMAT="Did not remove due to incorrect format.";
    private static final String PRINT_INCORRECT_OUTPUT =
            "Did not change output method due to incorrect format.";
    public static final String PRINT_INCORRECT_IMAGE = "Did not execute due to problem with image file.";

    // Default settings
    private static final String DEF_IMAGE = "cat.jpeg";
    private static final char[] DEF_CHARSET = {'0', '1', '2', '3', '4', '5', '6' ,'7', '8', '9'};
    private static final int DEF_IMAGE_RES = 128;
    private static final AsciiOutput DEF_ASCII_OUTPUT = new ConsoleAsciiOutput();
    // class fields
    private Image image;
    private AsciiOutput asciiOutput;
    private int imageResolution;
    private char[] charset;
    private final SubImgCharMatcher charMatcher;
    private ImageProcessor imageProcessor;
    private boolean callImProcessor =false;

    /**
     * Constructs a Shell instance with default settings.
     * @throws IOException if there is an error loading the image.
     */
    public Shell() throws IOException {
        this.charset = DEF_CHARSET;
        this.charMatcher = new SubImgCharMatcher(charset);
        this.image = new Image(DEF_IMAGE);
        asciiOutput = DEF_ASCII_OUTPUT;
        imageResolution = DEF_IMAGE_RES;
        imageProcessor= new ImageProcessor(image,imageResolution);
    }

    /**
     * Runs the shell, using user input
     */
    public void run() {
        String input = "";
        while (true) {
            System.out.print(COMMAND_INPUT);
            input = KeyboardInput.readLine();
            String[] commands = input.split(" ");
            if(input.equals(EXIT_COMMAND)){
                break;
            }
            switch (commands[0]) {
                case VIEW_CHARS:
                    viewChars();
                    break;
                case ADD_COMMAND:
                    handleAddCommand(commands);
                    break;
                case REMOVE_COMMAND:
                    handleRemoveCommand(commands);
                    break;
                case RES_COMMAND:
                    callImProcessor = true;
                    handleResolution(commands);
                    break;
                case IMG_COMMAND:
                    callImProcessor = true;
                    handleNewImage(commands);
                    break;
                case OUTPUT_COMMAND:
                    changeOutput(commands);
                    break;
                case RUN_ALG_COMMAND:
                    if(callImProcessor){
                        imageProcessor= new ImageProcessor(image,imageResolution);
                        callImProcessor = false;
                    }
                    runAlgorithm();
                    break;
                default:
                    System.out.println(PRINT_INCORRECT_COMMAND);
                    break;
            }
        }
    }

    /**
     * Displays the characters in the current character set.
     */
    private void viewChars() {
        Arrays.sort(charset); // Sort the character array
        for (char character : charset) {
            System.out.print(character + " ");
        }
        System.out.println();
    }

    /**
     * Handles the 'add' command to add characters to the charset based on user input
     * @param commands The command and character(s) to add.
     */
    private void handleAddCommand(String[] commands) {
        if (commands.length == 2){
            if (commands[1].length() == 1) {
                charMatcher.addChar(commands[1].charAt(0));
                charset = charMatcher.getCharset();
            } else if (commands[1].equals("all")) {
                for (int i = MIN_ASCI_VAL; i < MAX_ASCI_VAL; i++) {
                    charMatcher.addChar((char)i);
                    charset = charMatcher.getCharset();
                }
            } else if (commands[1].equals("space")){
                charMatcher.addChar(' ');
                charset = charMatcher.getCharset();
            } else if (commands[1].length() == 3 && Character.isLetter(commands[1].charAt(0)) &&
                    Character.isLetter(commands[1].charAt(2)) && commands[1].charAt(1) == '-'){
                char startChar = commands[1].charAt(0); // Character range case, e.g., "a-d"
                char endChar = commands[1].charAt(2);
                if (startChar <= endChar) {
                    for (char c = startChar; c <= endChar; c++) { // Add characters in the specified range
                        charMatcher.addChar(c);
                        charset = charMatcher.getCharset();
                    }
                } else {
                    for (char c = endChar; c <= startChar; c++) { // Handle reverse range, e.g., "d-a"
                        charMatcher.addChar(c);
                        charset = charMatcher.getCharset();
                    }
                }
            } else{
                System.out.println(PRINT_INCORRECT_ADD_FORMAT);
            }
        }
        else{
            System.out.println(PRINT_INCORRECT_ADD_FORMAT);
        }
    }

    /**
     * Handles the 'remove' command to remove characters from the charset based on user input
     * @param commands The command and character(s) to remove
     */
    private void handleRemoveCommand(String[] commands) {
        if (commands.length == 2) {
            if (commands[1].length() == 1) {
                charMatcher.removeChar(commands[1].charAt(0));
                charset = charMatcher.getCharset();
            } else if (commands[1].equals("all")) {
                for (int i = MIN_ASCI_VAL; i < MAX_ASCI_VAL; i++) {
                    charMatcher.removeChar((char) i);
                    charset = charMatcher.getCharset();
                }
            } else if (commands[1].equals("space")) {
                charMatcher.removeChar(' ');
                charset = charMatcher.getCharset();
            } else if (commands[1].length() == 3 && Character.isLetter(commands[1].charAt(0)) &&
                    Character.isLetter(commands[1].charAt(2)) && commands[1].charAt(1) == '-') {
                char startChar = commands[1].charAt(0);
                char endChar = commands[1].charAt(2);
                if (startChar <= endChar) {
                    for (char c = startChar; c <= endChar; c++) {
                        charMatcher.removeChar(c);
                        charset = charMatcher.getCharset();
                    }
                } else {
                    for (char c = endChar; c <= startChar; c++) {
                        charMatcher.removeChar(c);
                        charset = charMatcher.getCharset();
                    }
                }
            } else{
                System.out.println(PRINT_INCORRECT_REMOVE_FORMAT);
            }
        }
        else{
            System.out.println(PRINT_INCORRECT_REMOVE_FORMAT);
        }
    }

    /**
     * Handles the 'res' command to adjust image resolution.
     * @param commands The command and resolution adjustment.
     */
    private void handleResolution(String[] commands) {
        int minCharsInRow = Math.max(1, imageProcessor.getNewWidth() / imageProcessor.getNewHeight());
        if (commands.length == 2){
            if (commands[1].equals("up")) {
                if (imageResolution * 2 <= imageProcessor.getNewWidth()) {
                    imageResolution *= 2;
                    System.out.println("Resolution set to " + imageResolution + ".");
                } else {
                    System.out.println(PRINT_INCORRECT_RES);
                }
            }
            else if (commands[1].equals("down")) {
                if (imageResolution / 2 >= minCharsInRow) {
                    imageResolution /= 2;
                    System.out.println("Resolution set to " + imageResolution + ".");
                } else {
                    System.out.println(PRINT_INCORRECT_RES);
                }
            }
            else {
                System.out.println(PRINT_INCORRECT_FORMAT);
            }
        }
        else {
            System.out.println(PRINT_INCORRECT_FORMAT);
        }
    }

    /**
     * Handles the 'image' command to load a new image.
     *
     * @param commandWord The command and image filename.
     */
    private void handleNewImage(String[] commandWord) {
        try {
            image = new Image(commandWord[1]);
        } catch (IOException e) {
            System.out.println(PRINT_INCORRECT_IMAGE);
        }
    }

    /**
     * Handles the 'output' command to change the output method.
     *
     * @param command The command and desired output method.
     */
    private void changeOutput(String[] command) {
        if (command.length == 2) {
            if (command[1].equals("console")) {
                asciiOutput = new ConsoleAsciiOutput();
            } else if (command[1].equals("html")) {
                asciiOutput = new HtmlAsciiOutput("out.html", "Courier New");
            } else{ // neither console nor html
                System.out.println(PRINT_INCORRECT_OUTPUT);
            }
        } else{ // didn't write a second string
            System.out.println(PRINT_INCORRECT_OUTPUT);
        }
    }

    /**
     * Runs the ASCII art generation algorithm and outputs the result.
     */
    private void runAlgorithm() {
        if (charset.length != 0) {
            AsciiArtAlgorithm asciiArtAlgorithm = new AsciiArtAlgorithm(charMatcher,
                    imageProcessor);
            char[][] asciiArt = asciiArtAlgorithm.run();
            asciiOutput.out(asciiArt);
        }
        else System.out.println(PRINT_INCORRECT_CHARSET);
    }

    /**
     * The main method to start the shell.
     *
     * @param args Command-line arguments (not used).
     * @throws IOException If an error occurs while initializing the shell.
     */
    public static void main(String[] args) throws IOException {
        Shell shell = new Shell();
        shell.run();

    }
}
