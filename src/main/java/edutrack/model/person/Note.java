package edutrack.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a tutor's note attached to a student.
 * Immutable; empty string is allowed to represent absence of a note.
 */
public class Note {
    public static final String MESSAGE_CONSTRAINTS =
            "Notes may be empty. If present, the first character must not be a whitespace or control character.";

    /*
     * The first character of the note must not be a whitespace or control character,
     * otherwise a blank string could match.
     */
    public static final String VALIDATION_REGEX = "[^\\s\\p{Cntrl}][\\p{Print}\\p{Space}]*";

    public final String value;

    /**
     * Constructs a {@code Note}.
     *
     * @param note A valid note; null or empty becomes an empty note.
     * @throws IllegalArgumentException if the note is non-empty and invalid.
     */
    public Note(String note) {
        requireNonNull(note);
        value = note.isEmpty() ? "" : note;
        if (!isValidNote(value)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
    }

    /**
     * Returns true if a given string is a valid note.
     * Null or empty is considered valid (represents no note).
     */
    public static boolean isValidNote(String test) {
        if (test == null || test.isEmpty()) {
            return true;
        }
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Note)) {
            return false;
        }

        Note otherNote = (Note) other;
        return value.equals(otherNote.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
