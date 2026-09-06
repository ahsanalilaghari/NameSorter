package com.ahsanlaghari.namesorter.io;

import com.ahsanlaghari.namesorter.domain.Name;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FileNameDestinationTest {

    @TempDir
    Path directory;

    @Test
    void createsTheFileWithOneFullNamePerLineInTheOrderGiven() throws IOException {
        Path file = directory.resolve("sorted-names-list.txt");

        new FileNameDestination(file).write(List.of(
                new Name(List.of("Marin"), "Alvarez"),
                new Name(List.of("Adonis", "Julius"), "Archer")));

        assertThat(linesOf(file)).containsExactly("Marin Alvarez", "Adonis Julius Archer");
    }

    @Test
    void overwritesAnExistingFileCompletely() throws IOException {
        Path file = directory.resolve("sorted-names-list.txt");
        Files.write(file, List.of("Old line one", "Old line two", "Old line three"), StandardCharsets.UTF_8);

        new FileNameDestination(file).write(List.of(new Name(List.of("Leo"), "Gardner")));

        assertThat(linesOf(file)).containsExactly("Leo Gardner");
    }

    @Test
    void writesAnEmptyFileForAnEmptyList() throws IOException {
        Path file = directory.resolve("sorted-names-list.txt");

        new FileNameDestination(file).write(List.of());

        assertThat(file).exists();
        assertThat(linesOf(file)).isEmpty();
    }

    @Test
    void writesTheFileAsUtf8() throws IOException {
        Path file = directory.resolve("sorted-names-list.txt");

        new FileNameDestination(file).write(List.of(new Name(List.of("Zoë"), "Müller")));

        assertThat(linesOf(file)).containsExactly("Zoë Müller");
    }

    private static List<String> linesOf(Path file) throws IOException {
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }
}