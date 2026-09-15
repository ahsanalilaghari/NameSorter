package com.ahsanlaghari.namesorter.io;

import com.ahsanlaghari.namesorter.domain.Name;
import com.ahsanlaghari.namesorter.domain.NameParser;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Reads names from a UTF-8 text file with one name per line.
 *
 * <p>Blank lines are skipped. A line that is not a valid name is also skipped, with a
 * warning naming the file and line number written to the warnings stream, so the rest
 * of the file is still sorted. The warnings stream is injected rather than hard-coded
 * to {@code System.err} so tests can capture it.
 */
public final class FileNameSource implements NameSource {

    private final Path file;
    private final NameParser parser;
    private final PrintStream warnings;

    public FileNameSource(Path file, NameParser parser, PrintStream warnings) {
        this.file = Objects.requireNonNull(file, "file must not be null");
        this.parser = Objects.requireNonNull(parser, "parser must not be null");
        this.warnings = Objects.requireNonNull(warnings, "warnings must not be null");
    }

    @Override
    public List<Name> readNames() throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);

        List<Name> names = new ArrayList<>();
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.isBlank()) {
                continue;
            }
            int lineNumber = index + 1;
            try {
                names.add(parser.parse(line));
            } catch (IllegalArgumentException invalidName) {
                warnings.println(file + ", line " + lineNumber + ": skipped. " + invalidName.getMessage());
            }
        }
        return Collections.unmodifiableList(names);
    }
}