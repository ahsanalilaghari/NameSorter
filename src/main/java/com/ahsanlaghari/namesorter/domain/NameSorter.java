package com.ahsanlaghari.namesorter.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Sorts a list of names using whatever ordering it is given.
 *
 * <p>The sorter decides the mechanism (copy the list, sort the copy) and the
 * {@link Comparator} decides the order. Keeping the two apart means the ordering rule
 * can be swapped without touching this class.
 */
public final class NameSorter {

    private final Comparator<Name> ordering;

    public NameSorter(Comparator<Name> ordering) {
        this.ordering = Objects.requireNonNull(ordering, "ordering must not be null");
    }

    /**
     * @param names the names to sort, left untouched
     * @return a new, unmodifiable list in sorted order. Names that compare as equal keep
     *         the order they had in the input.
     */
    public List<Name> sort(List<Name> names) {
        List<Name> sorted = new ArrayList<>(names);
        sorted.sort(ordering);
        return Collections.unmodifiableList(sorted);
    }
}