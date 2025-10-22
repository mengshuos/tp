package edutrack.model.group;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A list of groups that enforces uniqueness between its elements and does not allow nulls.
 * A group is considered unique by comparing using {@code Group#equals}.
 * Supports a minimal set of list operations.
 *
 */
public class UniqueGroupList {

    private final ObservableList<Group> internalList = FXCollections.observableArrayList();
    private final ObservableList<Group> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent group as the given argument.
     */
    public boolean contains(Group toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::equals);
    }

    /**
     * Adds a group to the list.
     * The group must not already exist in the list.
     */

    public void add(Group toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new IllegalArgumentException("This group already exists.");
        }
        internalList.add(toAdd);
    }

    public void setGroups(List<Group> groups) {
        requireNonNull(groups);
        internalList.setAll(groups);
    }

    public ObservableList<Group> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UniqueGroupList)) {
            return false;
        }
        UniqueGroupList o = (UniqueGroupList) other;
        // compare by contents
        return internalList.equals(o.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

}


