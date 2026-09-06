package com.ahsanlaghari.namesorter;

import com.ahsanlaghari.namesorter.domain.Name;
import com.ahsanlaghari.namesorter.domain.NameSorter;
import com.ahsanlaghari.namesorter.io.NameDestination;
import com.ahsanlaghari.namesorter.io.NameSource;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * The whole program in one sentence: read names from a source, sort them, and write the
 * sorted list to every destination.
 *
 * <p>This class only knows the {@link NameSource} and {@link NameDestination} interfaces.
 * It has no idea whether names come from a file or go to the screen; {@code Main} decides
 * that when it wires the pieces together. That separation is what lets this class be
 * tested with in-memory fakes and no disk access.
 */
public final class NameSorterApplication {

    private final NameSource source;
    private final NameSorter sorter;
    private final List<NameDestination> destinations;

    public NameSorterApplication(NameSource source, NameSorter sorter, List<NameDestination> destinations) {
        this.source = Objects.requireNonNull(source, "source must not be null");
        this.sorter = Objects.requireNonNull(sorter, "sorter must not be null");
        this.destinations = List.copyOf(Objects.requireNonNull(destinations, "destinations must not be null"));
    }

    /**
     * @throws IOException              if the source cannot be read or a destination cannot be written
     * @throws IllegalArgumentException if the source contains an invalid name
     */
    public void run() throws IOException {
        List<Name> names = source.readNames();
        List<Name> sortedNames = sorter.sort(names);
        for (NameDestination destination : destinations) {
            destination.write(sortedNames);
        }
    }
}