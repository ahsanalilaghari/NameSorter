package com.ahsanlaghari.namesorter.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LastNameThenGivenNamesComparatorTest {

    private final LastNameThenGivenNamesComparator comparator = new LastNameThenGivenNamesComparator();
    private final NameParser parser = new NameParser();

    @Test
    void ordersByLastNameBeforeAnythingElse() {
        // "Marin" would come after "Adonis" alphabetically; the last name must win.
        assertThat(compare("Marin Alvarez", "Adonis Julius Archer")).isNegative();
        assertThat(compare("Adonis Julius Archer", "Marin Alvarez")).isPositive();
    }

    @Test
    void usesTheFirstGivenNameWhenLastNamesMatch() {
        assertThat(compare("Adam Smith", "Zoe Smith")).isNegative();
        assertThat(compare("Zoe Smith", "Adam Smith")).isPositive();
    }

    @Test
    void usesLaterGivenNamesWhenEarlierOnesMatch() {
        assertThat(compare("Hunter Uriah Adam Clarke", "Hunter Uriah Mathew Clarke")).isNegative();
        assertThat(compare("Hunter Uriah Mathew Clarke", "Hunter Uriah Adam Clarke")).isPositive();
    }

    @Test
    void putsTheShorterNameFirstWhenItsGivenNamesAreAPrefixOfTheOther() {
        assertThat(compare("Leo Gardner", "Leo James Gardner")).isNegative();
        assertThat(compare("Leo James Gardner", "Leo Gardner")).isPositive();
    }

    @Test
    void ignoresCaseWhenComparing() {
        assertThat(compare("marin alvarez", "Adonis Julius Archer")).isNegative();
        assertThat(compare("LEO GARDNER", "leo gardner")).isZero();
    }

    @Test
    void treatsIdenticalNamesAsEqual() {
        assertThat(compare("Leo Gardner", "Leo Gardner")).isZero();
    }

    private int compare(String first, String second) {
        return comparator.compare(parser.parse(first), parser.parse(second));
    }
}