package com.ahsanlaghari.namesorter.io;

import com.ahsanlaghari.namesorter.domain.Name;
import com.ahsanlaghari.namesorter.domain.NameParser;

import java.io.IOException;
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
 * <p>Blank lines are skipped, since a trailing newline or a stray empty line is not a
 * mistake worth stopping the run for. Any other line must be a valid name. The first
 * invalid line stops the run with an error that names the file and line number, rather
 * than silently producing a sorted list with entries missing.
 */
public final class FileNameSource implements NameSource {

    private final Path file;
    private final NameParser parser;

    public FileNameSource(Path file, NameParser parser) {
        this.file = Objects.requireNonNull(file, "file must not be null");
        this.parser = Objects.requireNonNull(parser, "parser must not be null");
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
            names.add(parseLine(line, lineNumber));
        }
        return Collections.unmodifiableList(names);
    }

    private Name parseLine(String line, int lineNumber) {
        try {
            return parser.parse(line);
        } catch (IllegalArgumentException invalidName) {
            throw new IllegalArgumentException(
                    file + ", line " + lineNumber + ": " + invalidName.getMessage(), invalidName);
        }
    }
}