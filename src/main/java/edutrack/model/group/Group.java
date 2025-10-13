package edutrack.model.group;

import static edutrack.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Group in the address book.
 * Guarantees: immutable; name is valid as declared in {@link #isValidGroupName(String)}
 */
public class Group {

    public static final String MESSAGE_CONSTRAINTS =
            "Group names should be alphanumeric and may include '-', '_' or '/'";
    public static final String VALIDATION_REGEX = "[\\p{Alnum}_\\-/]+";

    public final String groupName;

    /**
     * Constructs a {@code Group}.
     *
     * @param groupName A valid group name.
     */
    public Group(String groupName) {
        requireNonNull(groupName);
        checkArgument(isValidGroupName(groupName), MESSAGE_CONSTRAINTS);
        this.groupName = groupName;
    }

    /**
     * Returns true if a given string is a valid group name.
     */
    public static boolean isValidGroupName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Group)) {
            return false;
        }

        Group otherGroup = (Group) other;
        return groupName.equalsIgnoreCase(otherGroup.groupName);
    }

    @Override
    public int hashCode() {
        return groupName.toLowerCase().hashCode();
    }

    /**
     * Format state as text for viewing.
     */
    public String toString() {
        return '[' + groupName + ']';
    }
}
