package com.ahsanlaghari.namesorter.domain;

import java.util.List;
import java.util.Objects;

/**
 * A person's name: one or more given names followed by a last name.
 *
 * <p>This is the single place where the rule from the brief is enforced: a name
 * must have at least {@value #MIN_GIVEN_NAMES} and at most {@value #MAX_GIVEN_NAMES}
 * given names. Because the rule lives in the constructor, every {@code Name} that
 * exists in the program is valid, regardless of whether it came from a file, a
 * command-line argument or a test.
 *
 * <p>Instances are immutable and compare by value.
 */
public final class Name {

    public static final int MIN_GIVEN_NAMES = 1;
    public static final int MAX_GIVEN_NAMES = 3;

    private final List<String> givenNames;
    private final String lastName;

    /**
     * @param givenNames the given names in written order, e.g. ["Hunter", "Uriah", "Mathew"]
     * @param lastName   the last name, e.g. "Clarke"
     * @throws IllegalArgumentException if the number of given names is outside the allowed
     *                                  range, or if any part of the name is blank
     */
    public Name(List<String> givenNames, String lastName) {
        Objects.requireNonNull(givenNames, "givenNames must not be null");
        Objects.requireNonNull(lastName, "lastName must not be null");
        requireGivenNameCountWithinLimits(givenNames);
        requireNoBlankParts(givenNames, lastName);

        this.givenNames = List.copyOf(givenNames);
        this.lastName = lastName;
    }

    public List<String> givenNames() {
        return givenNames;
    }

    public String lastName() {
        return lastName;
    }

    /** The name as it is written, e.g. "Hunter Uriah Mathew Clarke". */
    public String fullName() {
        return String.join(" ", givenNames) + " " + lastName;
    }

    private static void requireGivenNameCountWithinLimits(List<String> givenNames) {
        int count = givenNames.size();
        if (count < MIN_GIVEN_NAMES || count > MAX_GIVEN_NAMES) {
            throw new IllegalArgumentException(String.format(
                    "A name must have between %d and %d given names, but %d were given: %s",
                    MIN_GIVEN_NAMES, MAX_GIVEN_NAMES, count, givenNames));
        }
    }

    private static void requireNoBlankParts(List<String> givenNames, String lastName) {
        if (lastName.isBlank()) {
            throw new IllegalArgumentException("The last name must not be blank");
        }
        if (givenNames.stream().anyMatch(String::isBlank)) {
            throw new IllegalArgumentException("Given names must not be blank: " + givenNames);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Name)) {
            return false;
        }
        Name that = (Name) other;
        return givenNames.equals(that.givenNames) && lastName.equals(that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(givenNames, lastName);
    }

    @Override
    public String toString() {
        return fullName();
    }
}