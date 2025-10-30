package edutrack.model.tag;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A list of tags that enforces uniqueness between its elements and does not allow nulls.
 * A tag is considered unique by comparing using {@code Tag#equals}.
 * Supports a minimal set of list operations.
 */
public class UniqueTagList implements Iterable<Tag> {

    private final ObservableList<Tag> internalList = FXCollections.observableArrayList();
    private final ObservableList<Tag> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent tag as the given argument.
     */
    public boolean contains(Tag toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::equals);
    }

    /**
     * Adds a tag to the list.
     * The tag must not already exist in the list.
     */
    public void add(Tag toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new IllegalArgumentException("This tag already exists.");
        }
        internalList.add(toAdd);
    }

    /**
     * Returns the tag in the list that matches the given tag.
     * The tag must exist in the list.
     */
    public Tag get(Tag tag) {
        requireNonNull(tag);
        return internalList.stream()
                .filter(tag::equals)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tag does not exist in the list."));
    }

    /**
     * Removes the equivalent tag from the list.
     * The tag must exist in the list.
     */
    public void remove(Tag toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new IllegalArgumentException("Tag does not exist in the list.");
        }
    }

    /**
     * Replaces the contents of this list with {@code tags}.
     */
    public void setTags(List<Tag> tags) {
        requireNonNull(tags);
        internalList.setAll(tags);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Tag> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Tag> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UniqueTagList)) {
            return false;
        }
        UniqueTagList otherList = (UniqueTagList) other;
        return internalList.equals(otherList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }
}

