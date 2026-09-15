package com.ahsanlaghari.namesorter.io;

import com.ahsanlaghari.namesorter.domain.Name;
import com.ahsanlaghari.namesorter.domain.NameParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileNameSourceTest {

    @TempDir
    Path directory;

    private final NameParser parser = new NameParser();
    private final ByteArrayOutputStream warnings = new ByteArrayOutputStream();

    @Test
    void readsOneNamePerLineInFileOrder() throws IOException {
        Path file = fileContaining("Janet Parsons", "Adonis Julius Archer", "Leo Gardner");

        List<Name> names = source(file).readNames();

        assertThat(names).containsExactly(
                new Name(List.of("Janet"), "Parsons"),
                new Name(List.of("Adonis", "Julius"), "Archer"),
                new Name(List.of("Leo"), "Gardner"));
        assertThat(warningOutput()).isEmpty();
    }

    @Test
    void skipsBlankLines() throws IOException {
        Path file = fileContaining("", "Janet Parsons", "   ", "Leo Gardner", "");

        List<Name> names = source(file).readNames();

        assertThat(names).containsExactly(
                new Name(List.of("Janet"), "Parsons"),
                new Name(List.of("Leo"), "Gardner"));
        assertThat(warningOutput()).isEmpty();
    }

    @Test
    void readsAnEmptyFileAsNoNames() throws IOException {
        Path file = fileContaining();

        List<Name> names = source(file).readNames();

        assertThat(names).isEmpty();
    }

    @Test
    void readsTheFileAsUtf8() throws IOException {
        Path file = fileContaining("Zoë Müller");

        List<Name> names = source(file).readNames();

        assertThat(names).containsExactly(new Name(List.of("Zoë"), "Müller"));
    }

    @Test
    void readsWindowsAndUnixLineEndingsWithOrWithoutAFinalNewline() throws IOException {
        Path file = directory.resolve("names.txt");
        Files.writeString(file, "Janet Parsons\r\nLeo Gardner\nMarin Alvarez", StandardCharsets.UTF_8);

        List<Name> names = source(file).readNames();

        assertThat(names).containsExactly(
                new Name(List.of("Janet"), "Parsons"),
                new Name(List.of("Leo"), "Gardner"),
                new Name(List.of("Marin"), "Alvarez"));
    }

    @Test
    void skipsAnInvalidNameAndWarnsWithTheFileAndLineNumber() throws IOException {
        Path file = fileContaining("Janet Parsons", "Clarke", "Leo Gardner");

        List<Name> names = source(file).readNames();

        assertThat(names).containsExactly(
                new Name(List.of("Janet"), "Parsons"),
                new Name(List.of("Leo"), "Gardner"));
        assertThat(warningOutput())
                .contains(file.toString())
                .contains("line 2")
                .contains("skipped")
                .contains("Invalid name 'Clarke'");
    }

    @Test
    void countsBlankLinesWhenReportingTheLineNumber() throws IOException {
        Path file = fileContaining("Janet Parsons", "", "Clarke", "Leo Gardner");

        source(file).readNames();

        assertThat(warningOutput()).contains("line 3");
    }

    @Test
    void warnsOnceForEachInvalidLine() throws IOException {
        Path file = fileContaining("Clarke", "Janet Parsons", "One Two Three Four Five");

        List<Name> names = source(file).readNames();

        assertThat(names).containsExactly(new Name(List.of("Janet"), "Parsons"));
        assertThat(warningOutput().lines()).hasSize(2);
    }

    @Test
    void failsWhenTheFileDoesNotExist() {
        Path missing = directory.resolve("does-not-exist.txt");

        assertThatThrownBy(() -> source(missing).readNames())
                .isInstanceOf(NoSuchFileException.class);
    }

    private FileNameSource source(Path file) {
        return new FileNameSource(file, parser, new PrintStream(warnings, true, StandardCharsets.UTF_8));
    }

    private String warningOutput() {
        return warnings.toString(StandardCharsets.UTF_8);
    }

    private Path fileContaining(String... lines) throws IOException {
        Path file = directory.resolve("names.txt");
        Files.write(file, List.of(lines), StandardCharsets.UTF_8);
        return file;
    }
}