package com.ahsanlaghari.namesorter.domain;

import java.util.Comparator;
import java.util.List;

/**
 * Orders names by last name, then by each given name in written order.
 *
 * <p>This is the ordering asked for by the PDF. It is the only place in the program
 * that knows how two names compare, so a different ordering (for example given name
 * first) is a new {@link Comparator} handed to {@link NameSorter}, not a change here.
 *
 * <p>Comparison ignores case, so {@code "de Souza"} and {@code "De Souza"} sort together.
 * The PDF does not say either way, so I am going with the usual assumption in comparisons
 * Only the comparison ignores case but the names themselves keep their original casing.
 *
 * <p>When one name's given names are a prefix of another's, the shorter name comes
 * first, so {@code "Leo Gardner"} sorts before {@code "Leo James Gardner"}.
 */
public final class LastNameThenGivenNamesComparator implements Comparator<Name> {

    @Override
    public int compare(Name first, Name second) {
        int byLastName = first.lastName().compareToIgnoreCase(second.lastName());
        if (byLastName != 0) {
            return byLastName;
        }
        return compareGivenNames(first.givenNames(), second.givenNames());
    }

    private static int compareGivenNames(List<String> first, List<String> second) {
        int sharedLength = Math.min(first.size(), second.size());
        for (int position = 0; position < sharedLength; position++) {
            int byGivenName = first.get(position).compareToIgnoreCase(second.get(position));
            if (byGivenName != 0) {
                return byGivenName;
            }
        }
        return Integer.compare(first.size(), second.size());
    }
}