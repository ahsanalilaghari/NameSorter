package com.ahsanlaghari.namesorter.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class NameSorterTest {

    private final NameParser parser = new NameParser();
    private final NameSorter sorter = new NameSorter(new LastNameThenGivenNamesComparator());

    @Test
    void sortsTheExampleFromTheRequirements() {
        List<Name> unsorted = names(
                "Janet Parsons",
                "Vaugh Lewis",
                "Adonis Julius Archer",
                "Shelby Nathan Yoder",
                "Marin Alvarez",
                "London Lindsey",
                "Beau Tristan Bentley",
                "Leo Gardner",
                "Hunter Uriah Mathew Clarke",
                "Mikayla Lopez",
                "Frankie Conner Ritter");

        List<Name> sorted = sorter.sort(unsorted);

        assertThat(sorted).containsExactlyElementsOf(names(
                "Marin Alvarez",
                "Adonis Julius Archer",
                "Beau Tristan Bentley",
                "Hunter Uriah Mathew Clarke",
                "Leo Gardner",
                "Vaugh Lewis",
                "London Lindsey",
                "Mikayla Lopez",
                "Janet Parsons",
                "Frankie Conner Ritter",
                "Shelby Nathan Yoder"));
    }

    @Test
    void leavesTheInputListUntouched() {
        List<Name> unsorted = new ArrayList<>(names("Janet Parsons", "Marin Alvarez"));

        sorter.sort(unsorted);

        assertThat(unsorted).containsExactlyElementsOf(names("Janet Parsons", "Marin Alvarez"));
    }

    @Test
    void keepsNamesThatCompareAsEqualInTheirInputOrder() {
        // These differ only by case, so the comparator treats them as equal.
        List<Name> unsorted = names("leo gardner", "Leo Gardner", "LEO GARDNER");

        List<Name> sorted = sorter.sort(unsorted);

        assertThat(sorted).containsExactlyElementsOf(unsorted);
    }

    @Test
    void keepsDuplicateNamesRatherThanRemovingThem() {
        List<Name> sorted = sorter.sort(names("Leo Gardner", "Marin Alvarez", "Leo Gardner"));

        assertThat(sorted).containsExactlyElementsOf(names("Marin Alvarez", "Leo Gardner", "Leo Gardner"));
    }

    @Test
    void sortsAnEmptyListToAnEmptyList() {
        assertThat(sorter.sort(List.of())).isEmpty();
    }

    @Test
    void usesWhateverOrderingItIsGiven() {
        NameSorter reverseSorter = new NameSorter(new LastNameThenGivenNamesComparator().reversed());

        List<Name> sorted = reverseSorter.sort(names("Marin Alvarez", "Janet Parsons", "Leo Gardner"));

        assertThat(sorted).containsExactlyElementsOf(names("Janet Parsons", "Leo Gardner", "Marin Alvarez"));
    }

    private List<Name> names(String... lines) {
        return Arrays.stream(lines)
                .map(parser::parse)
                .collect(Collectors.toList());
    }
}