package com.ahsanlaghari.namesorter.io;

import com.ahsanlaghari.namesorter.domain.Name;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

/**
 * Prints names to a stream, one per line.
 *
 * <p>The stream is passed in rather than hard-coded so tests can capture the output.
 */
public final class ConsoleNameDestination implements NameDestination {

    private final PrintStream out;

    public ConsoleNameDestination(PrintStream out) {
        this.out = Objects.requireNonNull(out, "out must not be null");
    }

    @Override
    public void write(List<Name> names) {
        for (Name name : names) {
            out.println(name.fullName());
        }
    }
}