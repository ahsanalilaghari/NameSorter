package com.ahsanlaghari.namesorter.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NameParserTest {

    private final NameParser parser = new NameParser();

    @Test
    void treatsTheLastWordAsTheLastNameAndTheRestAsGivenNames() {
        Name name = parser.parse("Hunter Uriah Mathew Clarke");

        assertThat(name).isEqualTo(new Name(List.of("Hunter", "Uriah", "Mathew"), "Clarke"));
    }

    @Test
    void parsesANameWithASingleGivenName() {
        Name name = parser.parse("Leo Gardner");

        assertThat(name).isEqualTo(new Name(List.of("Leo"), "Gardner"));
    }

    @Test
    void ignoresLeadingTrailingAndRepeatedWhitespace() {
        Name name = parser.parse("  Leo \t  Gardner   ");

        assertThat(name).isEqualTo(new Name(List.of("Leo"), "Gardner"));
    }

    @Test
    void keepsHyphensAndApostrophesAsPartOfAWord() {
        Name name = parser.parse("Mary-Jane O'Brien");

        assertThat(name).isEqualTo(new Name(List.of("Mary-Jane"), "O'Brien"));
    }

    @Test
    void preservesCasingExactlyAsWritten() {
        Name name = parser.parse("leo GARDNER");

        assertThat(name).isEqualTo(new Name(List.of("leo"), "GARDNER"));
    }

    @Test
    void rejectsALineWithOnlyOneWordAndNamesTheLine() {
        assertThatThrownBy(() -> parser.parse("Clarke"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid name 'Clarke'")
                .hasMessageContaining("between 1 and 3 given names");
    }

    @Test
    void rejectsALineWithTooManyWordsAndNamesTheLine() {
        assertThatThrownBy(() -> parser.parse("One Two Three Four Five"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid name 'One Two Three Four Five'")
                .hasMessageContaining("between 1 and 3 given names");
    }

    @Test
    void rejectsABlankLine() {
        assertThatThrownBy(() -> parser.parse("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("blank line");
    }

    @Test
    void rejectsNull() {
        assertThatThrownBy(() -> parser.parse(null))
                .isInstanceOf(NullPointerException.class);
    }
}