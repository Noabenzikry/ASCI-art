// Oryan Hassidim
// Oryan.Hassidim@mail.huji.ac.il
// last modified: 01/03/2024

package oh_tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;
import org.junit.Test;
import ascii_art.Shell;
import image_char_matching.SubImgCharMatcher;
import ascii_art.KeyboardInput;

public class OhTests {
    private static class TestCase {
        private String input;
        private String expected;

        public TestCase(String input, String expected) {
            this.input = input;
            this.expected = expected;
        }
    }

    private static class Restart extends TestCase {
        public Restart() {
            super(null, null);
        }
    }

    @Test
    public void TestRemove() {
        SubImgCharMatcher matcher = new SubImgCharMatcher("0123456789".toCharArray());
        assert matcher.getCharByImageBrightness(.33) == '4';
        matcher.removeChar('4');
        assert matcher.getCharByImageBrightness(.33) == '5';
    }

    @Test
    public void TestErrorMessages() throws IOException {
        var TestCases = new TestCase[] {
                // add
                new TestCase("add --", ">>> Did not add due to incorrect format.\n>>> "),
                new TestCase("add pm", ">>> Did not add due to incorrect format.\n>>> "),

                // remove
                new TestCase("remove --", ">>> Did not remove due to incorrect format.\n>>> "),
                new TestCase("remove pm", ">>> Did not remove due to incorrect format.\n>>> "),

                // res
                new TestCase("res 0", ">>> Did not change resolution due to incorrect format.\n>>> "),
                new TestCase("res up \nres up \nres up \nres up \nres down",
                        ">>> Resolution set to 256.\n>>> Resolution set to 512.\n>>> Resolution set to 1024.\n>>> Did not change resolution due to exceeding boundaries.\n>>> Resolution set to 512.\n>>> "),
                new Restart(),
                new TestCase("res down\n".repeat(8) + "res up",
                        ">>> Resolution set to 64.\n>>> Resolution set to 32.\n>>> Resolution set to 16.\n>>> Resolution set to 8.\n>>> Resolution set to 4.\n>>> Resolution set to 2.\n>>> Resolution set to 1.\n>>> Did not change resolution due to exceeding boundaries.\n>>> Resolution set to 2.\n>>> "),
                new Restart(),

                // image
                new TestCase("image 0", ">>> Did not execute due to problem with image file.\n>>> "),

                // output
                new TestCase("output 0", ">>> Did not change output method due to incorrect format.\n>>> "),

                // asciiArt
                new TestCase("remove all\nasciiArt", ">>> >>> Did not execute. Charset is empty.\n>>> "),

                // incorrect command
                new TestCase("adder", ">>> Did not execute due to incorrect command.\n>>> "),
                new TestCase("print", ">>> Did not execute due to incorrect command.\n>>> "),
        };
        Random rand = new Random();
        Shell shell = new Shell();
        for (var testCase : TestCases) {
            var in = new java.io.ByteArrayInputStream((testCase.input + "\nexit\n").getBytes());
            var out = new java.io.ByteArrayOutputStream();
            System.setIn(in);
            System.setOut(new java.io.PrintStream(out));
            KeyboardInput.Refresh();
            if (testCase instanceof Restart) {
                shell = new Shell();
                continue;
            }
            if (rand.nextBoolean())
                shell = new Shell();
            shell.run();
            var got = out.toString().replaceAll(System.lineSeparator(), "\n");
            if (!got.equals(testCase.expected)) {
                var text = "*Input:*" + testCase.input + "\n*Expected:*" + testCase.expected + "\n*Got:*" + got + "\n";
                System.out.println("*Input:*\n" + testCase.input);
                System.out.println("*Expected:*\n" + testCase.expected);
                System.out.println("*Got:*\n" + got);
                throw new AssertionError(text);
            }
        }
    }

    @Test
    public void TestResults() throws IOException {
        // res 128, chars 0-9
        var input = "output html\nasciiArt\nexit\n";
        var in = new java.io.ByteArrayInputStream(input.getBytes());
        var out = new java.io.ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new java.io.PrintStream(out));
        KeyboardInput.Refresh();
        Shell shell = new Shell();
        shell.run();
        var got = out.toString().replaceAll(System.lineSeparator(), "\n");
        var expected = ">>> >>> >>> ";
        if (!got.equals(expected)) {
            var text = "*Input:*\n" + input + "\n*Expected:*\n" + expected + "\n*Got:*\n" + got + "\n";
            System.out.println("*Input:*\n" + input);
            System.out.println("*Expected:*\n" + expected);
            System.out.println("*Got:*\n" + got);
            throw new AssertionError(text);
        }
        got = Files.readString(new File("out.html").toPath());
        expected = Files.readString(new File("out (1).html").toPath());
        if (!got.equals(expected)) {
            var text = "*Expected:*\n" + expected + "\n*Got:*\n" + got + "\n";
            System.out.println("*Expected:*\n" + expected);
            System.out.println("*Got:*\n" + got);
            throw new AssertionError(text);
        }

        // res 256, chars all
        input = "output html\nres up\nadd all\nasciiArt\nexit\n";
        in = new java.io.ByteArrayInputStream(input.getBytes());
        out = new java.io.ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new java.io.PrintStream(out));
        KeyboardInput.Refresh();
        shell = new Shell();
        shell.run();
        got = out.toString().replaceAll(System.lineSeparator(), "\n");
        expected = ">>> >>> Resolution set to 256.\n>>> >>> >>> ";
        if (!got.equals(expected)) {
            var text = "*Input:*\n" + input + "\n*Expected:*\n" + expected + "\n*Got:*\n" + got + "\n";
            System.out.println("*Input:*\n" + input);
            System.out.println("*Expected:*\n" + expected);
            System.out.println("*Got:*\n" + got);
            throw new AssertionError(text);
        }
        got = Files.readString(new File("out.html").toPath());
        expected = Files.readString(new File("out (2).html").toPath());
        if (!got.equals(expected)) {
            var text = "*Expected:*\n" + expected + "\n*Got:*\n" + got + "\n";
            System.out.println("*Expected:*\n" + expected);
            System.out.println("*Got:*\n" + got);
            throw new AssertionError(text);
        }
    }
}