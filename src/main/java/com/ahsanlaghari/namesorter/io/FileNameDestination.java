package com.ahsanlaghari.namesorter.io;

import com.ahsanlaghari.namesorter.domain.Name;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Writes names to a UTF-8 text file, one per line.
 *
 * <p>The file is created if missing and replaced if present.
 */
public final class FileNameDestination implements NameDestination {

    private final Path file;

    public FileNameDestination(Path file) {
        this.file = Objects.requireNonNull(file, "file must not be null");
    }

    @Override
    public void write(List<Name> names) throws IOException {
        List<String> lines = names.stream()
                .map(Name::fullName)
                .collect(Collectors.toList());

        Files.write(file, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }
}