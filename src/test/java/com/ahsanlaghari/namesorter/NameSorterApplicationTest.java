package com.ahsanlaghari.namesorter;

import com.ahsanlaghari.namesorter.domain.LastNameThenGivenNamesComparator;
import com.ahsanlaghari.namesorter.domain.Name;
import com.ahsanlaghari.namesorter.domain.NameSorter;
import com.ahsanlaghari.namesorter.io.NameDestination;
import com.ahsanlaghari.namesorter.io.NameSource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NameSorterApplicationTest {

    private static final Name ALVAREZ = new Name(List.of("Marin"), "Alvarez");
    private static final Name ARCHER = new Name(List.of("Adonis", "Julius"), "Archer");
    private static final Name PARSONS = new Name(List.of("Janet"), "Parsons");

    private final NameSorter sorter = new NameSorter(new LastNameThenGivenNamesComparator());

    @Test
    void readsSortsAndWritesTheNames() throws IOException {
        NameSource source = new InMemoryNameSource(PARSONS, ARCHER, ALVAREZ);
        RecordingNameDestination destination = new RecordingNameDestination();

        new NameSorterApplication(source, sorter, List.of(destination)).run();

        assertThat(destination.received).containsExactly(ALVAREZ, ARCHER, PARSONS);
    }

    @Test
    void writesTheSameSortedListToEveryDestination() throws IOException {
        NameSource source = new InMemoryNameSource(PARSONS, ALVAREZ);
        RecordingNameDestination first = new RecordingNameDestination();
        RecordingNameDestination second = new RecordingNameDestination();

        new NameSorterApplication(source, sorter, List.of(first, second)).run();

        assertThat(first.received).containsExactly(ALVAREZ, PARSONS);
        assertThat(second.received).containsExactly(ALVAREZ, PARSONS);
    }

    @Test
    void writesAnEmptyListWhenTheSourceHasNoNames() throws IOException {
        NameSource source = new InMemoryNameSource();
        RecordingNameDestination destination = new RecordingNameDestination();

        new NameSorterApplication(source, sorter, List.of(destination)).run();

        assertThat(destination.wasWrittenTo).isTrue();
        assertThat(destination.received).isEmpty();
    }

    @Test
    void writesNothingWhenTheSourceCannotBeRead() {
        NameSource failingSource = () -> {
            throw new IOException("disk on fire");
        };
        RecordingNameDestination destination = new RecordingNameDestination();

        assertThatThrownBy(() -> new NameSorterApplication(failingSource, sorter, List.of(destination)).run())
                .isInstanceOf(IOException.class)
                .hasMessage("disk on fire");
        assertThat(destination.wasWrittenTo).isFalse();
    }

    /** A source that hands back a fixed list of names. */
    private static final class InMemoryNameSource implements NameSource {
        private final List<Name> names;

        InMemoryNameSource(Name... names) {
            this.names = List.of(names);
        }

        @Override
        public List<Name> readNames() {
            return names;
        }
    }

    /** A destination that remembers what it was asked to write. */
    private static final class RecordingNameDestination implements NameDestination {
        private final List<Name> received = new ArrayList<>();
        private boolean wasWrittenTo = false;

        @Override
        public void write(List<Name> names) {
            received.addAll(names);
            wasWrittenTo = true;
        }
    }
}