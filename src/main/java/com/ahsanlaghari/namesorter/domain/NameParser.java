package com.ahsanlaghari.namesorter.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Turns one line of text such as {@code "Hunter Uriah Mathew Clarke"} into a {@link Name}.
 *
 * <p>The format is the one seen in the PDF: words separated by whitespace, where the
 * last word is the last name and every word before it is a given name. Leading, trailing
 * and repeated whitespace is ignored, so {@code "  Leo   Gardner "} parses the same as
 * {@code "Leo Gardner"}. Casing is preserved exactly as written.
 *
 * <p>This class only knows how to split a line. Whether the resulting parts make a
 * valid name is decided by {@link Name} itself; the parser simply adds the offending
 * line to the error message so the user can find it.
 */
public final class NameParser {

    private static final String WHITESPACE = "\\s+";

    /**
     * @param line a single line of text containing one name
     * @return the parsed name
     * @throws IllegalArgumentException if the line is blank or does not form a valid name
     */
    public Name parse(String line) {
        Objects.requireNonNull(line, "line must not be null");

        String trimmedLine = line.strip();
        if (trimmedLine.isEmpty()) {
            throw new IllegalArgumentException("Cannot parse a name from a blank line");
        }

        List<String> words = Arrays.asList(trimmedLine.split(WHITESPACE));
        List<String> givenNames = words.subList(0, words.size() - 1);
        String lastName = words.get(words.size() - 1);

        try {
            return new Name(givenNames, lastName);
        } catch (IllegalArgumentException invalidName) {
            throw new IllegalArgumentException(
                    "Invalid name '" + trimmedLine + "': " + invalidName.getMessage(), invalidName);
        }
    }
}