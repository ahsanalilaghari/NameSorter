# Name Sorter

![Build](https://github.com/ahsanalilaghari/NameSorter/actions/workflows/build.yml/badge.svg)

Sorts a list of names by last name, then by given names, and writes the result to the
screen and to a file.

Given a file with one name per line:

```text
Janet Parsons
Vaugh Lewis
Adonis Julius Archer
```

running `name-sorter ./unsorted-names-list.txt` prints:

```text
Adonis Julius Archer
Vaugh Lewis
Janet Parsons
```

and writes the same list to `sorted-names-list.txt` in the current directory,
replacing it if it already exists.

## Requirements

- Java 17 or later on the `PATH`.

Maven is not required. The included wrapper (`mvnw` / `mvnw.cmd`) downloads it on first use.

## Build and test

```sh
./mvnw verify          # macOS / Linux
mvnw.cmd verify        # Windows
```

This compiles the code, runs all tests and produces `target/name-sorter.jar`.

## Run

Using the launcher scripts:

```sh
./name-sorter ./unsorted-names-list.txt        # macOS / Linux
name-sorter.cmd .\unsorted-names-list.txt      # Windows
```

Or directly with Java:

```sh
java -jar target/name-sorter.jar ./unsorted-names-list.txt
```

The sorted names are printed to standard output and written to `sorted-names-list.txt`
in the directory the command is run from. The exit code is `0` on success and `1` if the
arguments are wrong, the input file cannot be read, or a line is not a valid name. Error
messages go to standard error and name the file and line number where possible.

## Rules and assumptions

The requirements state that a name has a last name and between one and three given names,
and that names are sorted by last name and then by given names. Everything below is the
assumption of the parts that the requirements left unsaid.

- **Sorting ignores case.** `de Souza` and `De Souza` sort together. Names keep their
  original casing in the output.
- **Given names are compared in written order.** When one name's given names are a prefix
  of another's, the shorter name comes first: `Leo Gardner` before `Leo James Gardner`.
- **Names that compare as equal keep their input order**, and duplicates are kept.
- **Whitespace is forgiving.** Leading, trailing and repeated spaces or tabs are ignored.
  Blank lines are skipped.
- **Hyphens and apostrophes are part of a word.** `Mary-Jane O'Brien` is one given name
  and one last name.
- **An invalid line stops the run.** A line with zero or more than three given names is
  reported with its line number and nothing is written. Silently skipping it would produce
  a sorted list with entries missing, which is worse than a clear failure.
- **Files are read and written as UTF-8**, with either Windows or Unix line endings
  accepted on input. The output file ends with a newline, as text files conventionally do.
- **The sample input contains `Vaugh Lewis`** while the expected output in the requirements
  shows `Vaughn Lewis`. The program echoes its input, so it prints `Vaugh`.

## How the code is organised

```text
com.ahsanlaghari.namesorter
├── Main                        parses arguments, wires everything together, maps errors to exit codes
├── NameSorterApplication       read from a source, sort, write to every destination
├── domain                      the problem itself, no I/O
│   ├── Name                    value object; enforces the 1-3 given names rule
│   ├── NameParser              one line of text -> Name
│   ├── LastNameThenGivenNamesComparator   the ordering rule
│   └── NameSorter              applies whatever Comparator it is given
└── io                          getting names in and out
    ├── NameSource              interface: where names come from
    ├── FileNameSource          reads one name per line from a UTF-8 file
    ├── NameDestination         interface: where sorted names go
    ├── ConsoleNameDestination  prints to a stream
    └── FileNameDestination     writes to a file, overwriting it
```

A run flows top to bottom: `Main` builds a `FileNameSource`, a `NameSorter` and two
destinations, and hands them to `NameSorterApplication`, which reads, sorts and writes.
The `domain` package never imports from `io`.

## Design notes

**Each likely change has one place to go.** Every axis of change is a constructor argument
typed to an interface, and a JDK interface is preferred when one fits.

| To change...                              | Add or edit                                    | Untouched                 |
|-------------------------------------------|------------------------------------------------|---------------------------|
| Where names come from (args, JSON, HTTP)  | a new `NameSource`, one line in `Main`         | parsing, sorting, writing |
| Where names go (database, JSON, email)    | a new `NameDestination`, one line in `Main`    | everything else           |
| The ordering (given name first, reversed) | a new `Comparator<Name>`, one line in `Main`   | the sorter, all I/O       |
| How many given names are allowed          | two constants in `Name`                        | everything else           |
| The line format (`Last, First`)           | `NameParser`                                   | everything else           |

**Interfaces only where they earn their place.** `NameSource` and `NameDestination` are
interfaces because the application needs more than one implementation and the tests need
in-memory fakes. `NameParser` and `NameSorter` are plain classes: they have one
implementation each and are fast enough to use directly in tests. Both are injected, so
extracting an interface later is a mechanical change.

**Validation lives on the domain object.** `Name` rejects an invalid number of given names
in its constructor, so a name that exists is always valid no matter which source produced
it. The parser only adds the offending line to the message; the file source adds the line
number.

**No frameworks.** The program has no runtime dependencies. Tests use JUnit 5 and AssertJ
for readable assertions, with hand-written fakes instead of a mocking library.

**Everything is read into memory.** Right for the thousands of names this is built for. A
streaming source would be a new `NameSource` implementation.

## Tests

`./mvnw test` runs the suite. Every class has a test file next to it in `src/test`, and
each test checks one behaviour and is named as a sentence, so the report reads as a
specification. File handling is tested against real files in a temporary directory, and
`MainTest` runs the whole program against `unsorted-names-list.txt` and checks both the
printed output and the written file.

## Known limitations and possible next steps

- **Input is read fully into memory.** Fine for thousands of names, not for millions. A
  streaming `NameSource` and an external sort would slot in behind the existing interfaces.
- **Sorting is by Unicode value, not locale.** `compareToIgnoreCase` orders accented and
    non-Latin letters by code point, so `Émile` sorts after `Zoe`. Using a `java.text.Collator`
    in `LastNameThenGivenNamesComparator` would give locale-aware ordering. This would be the
    first change to make for international name lists.