package com.ahsanlaghari.namesorter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs the real program end to end against the sample file, checking both
 * what is printed and what is written to disk.
 */
class MainTest {

    /** The sample input checked into the project root, which is where Maven runs tests from. */
    private static final Path SAMPLE_INPUT = Path.of("unsorted-names-list.txt").toAbsolutePath();

    private static final List<String> EXPECTED_SORTED_NAMES = List.of(
            "Marin Alvarez",
            "Adonis Julius Archer",
            "Beau Tristan Bentley",
            "Hunter Uriah Mathew Clarke",
            "Leo Gardner",
            "Vaugh Lewis",
            "London Lindsey",
            "Mikayla Lopez",
            "Janet Parsons",
            "Frankie Conner Ritter",
            "Shelby Nathan Yoder");

    @TempDir
    Path workingDirectory;

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @Test
    void sortsTheSampleFileToTheScreenAndToTheOutputFile() throws IOException {
        int exitCode = run(SAMPLE_INPUT.toString());

        assertThat(exitCode).isZero();
        assertThat(errorOutput()).isEmpty();
        assertThat(standardOutput().lines()).containsExactlyElementsOf(EXPECTED_SORTED_NAMES);
        assertThat(linesOf(outputFile())).containsExactlyElementsOf(EXPECTED_SORTED_NAMES);
    }

    @Test
    void writesTheOutputFileToTheWorkingDirectoryEvenWhenTheInputIsElsewhere() throws IOException {
        Path elsewhere = Files.createDirectory(workingDirectory.resolve("elsewhere"));
        Path input = Files.write(elsewhere.resolve("names.txt"), List.of("Leo Gardner"), StandardCharsets.UTF_8);

        int exitCode = run(input.toString());

        assertThat(exitCode).isZero();
        assertThat(linesOf(outputFile())).containsExactly("Leo Gardner");
        assertThat(elsewhere.resolve(Main.OUTPUT_FILE_NAME)).doesNotExist();
    }

    @Test
    void printsUsageAndFailsUnlessExactlyOneArgumentIsGiven() {
        assertThat(run()).isEqualTo(1);
        assertThat(run("one.txt", "two.txt")).isEqualTo(1);
        assertThat(errorOutput()).contains("Usage: name-sorter");
        assertThat(outputFile()).doesNotExist();
    }

    @Test
    void reportsAMissingInputFileWithoutWritingOutput() {
        int exitCode = run("no-such-file.txt");

        assertThat(exitCode).isEqualTo(1);
        assertThat(errorOutput()).contains("File not found").contains("no-such-file.txt");
        assertThat(outputFile()).doesNotExist();
    }

    @Test
    void reportsAnInvalidNameWithItsLineNumberWithoutWritingOutput() throws IOException {
        Files.write(workingDirectory.resolve("names.txt"),
                List.of("Janet Parsons", "Clarke"), StandardCharsets.UTF_8);

        int exitCode = run("names.txt");

        assertThat(exitCode).isEqualTo(1);
        assertThat(errorOutput()).contains("line 2").contains("Invalid name 'Clarke'");
        assertThat(outputFile()).doesNotExist();
    }

    private int run(String... args) {
        return Main.run(args, workingDirectory,
                new PrintStream(out, true, StandardCharsets.UTF_8),
                new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    private Path outputFile() {
        return workingDirectory.resolve(Main.OUTPUT_FILE_NAME);
    }

    private String standardOutput() {
        return out.toString(StandardCharsets.UTF_8);
    }

    private String errorOutput() {
        return err.toString(StandardCharsets.UTF_8);
    }

    private static List<String> linesOf(Path file) throws IOException {
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }
}