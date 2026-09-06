package com.ahsanlaghari.namesorter.io;

import com.ahsanlaghari.namesorter.domain.Name;

import java.io.IOException;
import java.util.List;

/**
 * Somewhere names come from: a file, command-line arguments, a network call, a test.
 *
 * <p>The application depends on this interface rather than on any particular source,
 * so a new kind of input is a new implementation and one line of wiring in {@code Main}.
 */
public interface NameSource {

    /**
     * @return every name the source holds, in the order the source provides them
     * @throws IOException              if the source cannot be read
     * @throws IllegalArgumentException if the source contains something that is not a valid name
     */
    List<Name> readNames() throws IOException;
}