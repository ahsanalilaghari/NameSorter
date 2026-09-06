package com.ahsanlaghari.namesorter.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NameTest {

    @Test
    void exposesGivenNamesAndLastName() {
        Name name = new Name(List.of("Hunter", "Uriah", "Mathew"), "Clarke");

        assertThat(name.givenNames()).containsExactly("Hunter", "Uriah", "Mathew");
        assertThat(name.lastName()).isEqualTo("Clarke");
    }

    @Test
    void fullNameJoinsAllPartsWithSingleSpaces() {
        Name name = new Name(List.of("Hunter", "Uriah", "Mathew"), "Clarke");

        assertThat(name.fullName()).isEqualTo("Hunter Uriah Mathew Clarke");
    }

    @ParameterizedTest(name = "{0} given name(s) is allowed")
    @ValueSource(ints = {1, 2, 3})
    void acceptsBetweenOneAndThreeGivenNames(int count) {
        List<String> givenNames = Collections.nCopies(count, "Given");

        Name name = new Name(givenNames, "Last");

        assertThat(name.givenNames()).hasSize(count);
    }

    @Test
    void rejectsZeroGivenNames() {
        assertThatThrownBy(() -> new Name(List.of(), "Clarke"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 1 and 3 given names");
    }

    @Test
    void rejectsFourGivenNames() {
        assertThatThrownBy(() -> new Name(List.of("A", "B", "C", "D"), "Clarke"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 1 and 3 given names");
    }

    @Test
    void rejectsBlankLastName() {
        assertThatThrownBy(() -> new Name(List.of("Leo"), "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("last name must not be blank");
    }

    @Test
    void rejectsBlankGivenName() {
        assertThatThrownBy(() -> new Name(List.of("Leo", "   "), "Gardner"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Given names must not be blank");
    }

    @Test
    void rejectsNullArguments() {
        assertThatThrownBy(() -> new Name(null, "Clarke"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Name(List.of("Leo"), null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void isNotAffectedByLaterChangesToTheListItWasBuiltFrom() {
        List<String> givenNames = new ArrayList<>(List.of("Hunter"));
        Name name = new Name(givenNames, "Clarke");

        givenNames.add("Uriah");

        assertThat(name.givenNames()).containsExactly("Hunter");
    }

    @Test
    void namesWithTheSamePartsAreEqual() {
        Name first = new Name(List.of("Leo"), "Gardner");
        Name second = new Name(List.of("Leo"), "Gardner");

        assertThat(first).isEqualTo(second).hasSameHashCodeAs(second);
    }

    @Test
    void namesWithDifferentPartsAreNotEqual() {
        Name name = new Name(List.of("Leo"), "Gardner");

        assertThat(name).isNotEqualTo(new Name(List.of("Leo"), "Lewis"));
        assertThat(name).isNotEqualTo(new Name(List.of("Vaugh"), "Gardner"));
    }
}