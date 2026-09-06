package com.ahsanlaghari.namesorter;

import com.ahsanlaghari.namesorter.domain.LastNameThenGivenNamesComparator;
import com.ahsanlaghari.namesorter.domain.NameParser;
import com.ahsanlaghari.namesorter.domain.NameSorter;
import com.ahsanlaghari.namesorter.io.ConsoleNameDestination;
import com.ahsanlaghari.namesorter.io.FileNameDestination;
import com.ahsanlaghari.namesorter.io.FileNameSource;
import com.ahsanlaghari.namesorter.io.NameDestination;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

/**
 * Command-line entry point: {@code name-sorter <path-to-unsorted-names-list>}.
 *
 * <p>This is the only class that touches the command line, the real file system paths and
 * the exit code. It wires the concrete pieces together and hands them to
 * {@link NameSorterApplication}. To change where names come from or go to, change the
 * wiring here and nowhere else.
 */
public final class Main {

    /** Written to the directory the command is run from. */
    static final String OUTPUT_FILE_NAME = "sorted-names-list.txt";

    private static final int EXIT_SUCCESS = 0;
    private static final int EXIT_FAILURE = 1;

    private Main() {
    }

    public static void main(String[] args) {
        Path currentDirectory = Path.of(System.getProperty("user.dir"));
        int exitCode = run(args, currentDirectory, System.out, System.err);
        System.exit(exitCode);
    }

    /**
     * Runs the program and returns the exit code instead of exiting, so tests can call it.
     *
     * @param args             the command-line arguments: exactly one, the input file path
     * @param workingDirectory where a relative input path is resolved from and where the
     *                         output file is written
     * @param out              where the sorted names are printed
     * @param err              where usage and error messages are printed
     */
    static int run(String[] args, Path workingDirectory, PrintStream out, PrintStream err) {
        if (args.length != 1) {
            err.println("Usage: name-sorter <path-to-unsorted-names-list>");
            return EXIT_FAILURE;
        }
        Path inputFile = workingDirectory.resolve(args[0]);
        Path outputFile = workingDirectory.resolve(OUTPUT_FILE_NAME);

        List<NameDestination> destinations = List.of(
                new ConsoleNameDestination(out),
                new FileNameDestination(outputFile));

        NameSorterApplication application = new NameSorterApplication(
                new FileNameSource(inputFile, new NameParser()),
                new NameSorter(new LastNameThenGivenNamesComparator()),
                destinations);

        try {
            application.run();
            return EXIT_SUCCESS;
        } catch (NoSuchFileException missingFile) {
            err.println("File not found: " + missingFile.getFile());
            return EXIT_FAILURE;
        } catch (IOException ioFailure) {
            err.println("Could not read or write a file: " + ioFailure.getMessage());
            return EXIT_FAILURE;
        } catch (IllegalArgumentException invalidName) {
            err.println(invalidName.getMessage());
            return EXIT_FAILURE;
        }
    }
}