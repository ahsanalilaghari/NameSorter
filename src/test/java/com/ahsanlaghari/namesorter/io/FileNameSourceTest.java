package com.ahsanlaghari.namesorter.io;

import com.ahsanlaghari.namesorter.domain.Name;
import com.ahsanlaghari.namesorter.domain.NameParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
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

    @Test
    void readsOneNamePerLineInFileOrder() throws IOException {
        Path file = fileContaining("Janet Parsons", "Adonis Julius Archer", "Leo Gardner");

        List<Name> names = new FileNameSource(file, parser).readNames();

        assertThat(names).containsExactly(
                new Name(List.of("Janet"), "Parsons"),
                new Name(List.of("Adonis", "Julius"), "Archer"),
                new Name(List.of("Leo"), "Gardner"));
    }

    @Test
    void skipsBlankLines() throws IOException {
        Path file = fileContaining("", "Janet Parsons", "   ", "Leo Gardner", "");

        List<Name> names = new FileNameSource(file, parser).readNames();

        assertThat(names).containsExactly(
                new Name(List.of("Janet"), "Parsons"),
                new Name(List.of("Leo"), "Gardner"));
    }

    @Test
    void readsAnEmptyFileAsNoNames() throws IOException {
        Path file = fileContaining();

        List<Name> names = new FileNameSource(file, parser).readNames();

        assertThat(names).isEmpty();
    }

    @Test
    void readsTheFileAsUtf8() throws IOException {
        Path file = fileContaining("Zoë Müller");

        List<Name> names = new FileNameSource(file, parser).readNames();

        assertThat(names).containsExactly(new Name(List.of("Zoë"), "Müller"));
    }

    @Test
    void readsWindowsAndUnixLineEndingsWithOrWithoutAFinalNewline() throws IOException {
        Path file = directory.resolve("names.txt");
        Files.writeString(file, "Janet Parsons\r\nLeo Gardner\nMarin Alvarez", StandardCharsets.UTF_8);

        List<Name> names = new FileNameSource(file, parser).readNames();

        assertThat(names).containsExactly(
                new Name(List.of("Janet"), "Parsons"),
                new Name(List.of("Leo"), "Gardner"),
                new Name(List.of("Marin"), "Alvarez"));
    }

    @Test
    void reportsTheFileAndLineNumberOfAnInvalidName() throws IOException {
        Path file = fileContaining("Janet Parsons", "Clarke", "Leo Gardner");

        assertThatThrownBy(() -> new FileNameSource(file, parser).readNames())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(file.toString())
                .hasMessageContaining("line 2")
                .hasMessageContaining("Invalid name 'Clarke'");
    }

    @Test
    void reportsTheFileAndLineNumberOfAnInvalidNameCountingBlankLines() throws IOException {
        Path file = fileContaining("Janet Parsons", "", "Clarke", "Leo Gardner");

        assertThatThrownBy(() -> new FileNameSource(file, parser).readNames())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(file.toString())
                .hasMessageContaining("line 3")
                .hasMessageContaining("Invalid name 'Clarke'");
    }

    @Test
    void failsWhenTheFileDoesNotExist() {
        Path missing = directory.resolve("does-not-exist.txt");

        assertThatThrownBy(() -> new FileNameSource(missing, parser).readNames())
                .isInstanceOf(NoSuchFileException.class);
    }

    private Path fileContaining(String... lines) throws IOException {
        Path file = directory.resolve("names.txt");
        Files.write(file, List.of(lines), StandardCharsets.UTF_8);
        return file;
    }
}