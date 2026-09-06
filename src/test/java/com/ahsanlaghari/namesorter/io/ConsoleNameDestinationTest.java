package com.ahsanlaghari.namesorter.io;

import com.ahsanlaghari.namesorter.domain.Name;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsoleNameDestinationTest {

    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();
    private final ConsoleNameDestination destination =
            new ConsoleNameDestination(new PrintStream(captured, true, StandardCharsets.UTF_8));

    @Test
    void printsOneFullNamePerLineInTheOrderGiven() {
        destination.write(List.of(
                new Name(List.of("Marin"), "Alvarez"),
                new Name(List.of("Adonis", "Julius"), "Archer")));

        assertThat(output()).isEqualTo(
                "Marin Alvarez" + System.lineSeparator()
                        + "Adonis Julius Archer" + System.lineSeparator());
    }

    @Test
    void printsNothingForAnEmptyList() {
        destination.write(List.of());

        assertThat(output()).isEmpty();
    }

    private String output() {
        return captured.toString(StandardCharsets.UTF_8);
    }
}