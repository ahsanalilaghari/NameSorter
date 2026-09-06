package com.ahsanlaghari.namesorter.io;

import com.ahsanlaghari.namesorter.domain.Name;

import java.io.IOException;
import java.util.List;

/**
 * Somewhere sorted names go: the screen, a file, a database, a test.
 *
 * <p>The application writes to every destination it is given, so adding a new kind of
 * output is a new implementation and one line of wiring in {@code Main}.
 */
public interface NameDestination {

    /**
     * @param names the names to write, in the order they should appear
     * @throws IOException if the destination cannot be written to
     */
    void write(List<Name> names) throws IOException;
}