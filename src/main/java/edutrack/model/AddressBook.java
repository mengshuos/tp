package edutrack.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import edutrack.commons.util.ToStringBuilder;
import edutrack.model.group.Group;
import edutrack.model.group.UniqueGroupList;
import edutrack.model.person.Person;
import edutrack.model.person.UniquePersonList;
import edutrack.model.tag.Tag;
import edutrack.model.tag.UniqueTagList;
import javafx.collections.ObservableList;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .isSamePerson comparison)
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniqueTagList tags;
    private final UniquePersonList persons;
    private final UniqueGroupList groups;

    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        tags = new UniqueTagList();
        persons = new UniquePersonList();
        groups = new UniqueGroupList();
    }

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Replaces the contents of the group list with {@code groups}.
     * {@code groups} must not contain duplicate groups.
     */
    public void setGroups(List<Group> groups) {
        this.groups.setGroups(groups);
    }

    /**
     * Replaces the contents of the tag list with {@code tags}.
     * {@code tags} must not contain duplicate tags.
     */
    public void setTags(List<Tag> tags) {
        this.tags.setTags(tags);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        setGroups(List.copyOf(newData.getGroupList()));
        setTags(List.copyOf(newData.getTagList()));
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
        // Add any groups that the person belongs to into the global group list
        p.getGroups().forEach(group -> {
            if (!groups.contains(group)) {
                groups.add(group);
            }
        });
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    /**
     * Removes the given group from the address book.
     * The group must exist in the address book.
     */
    public void removeGroup(Group group) {
        requireNonNull(group);
        groups.remove(group);
    }

    /**
     * Returns true if a group with the same identity as {@code group} exists in the address book.
     */

    public boolean hasGroup(Group group) {
        requireNonNull(group);
        return groups.contains(group);
    }

    /**
     * Adds a group to the address book.
     * The group must not already exist in the address book.
     */

    public void addGroup(Group group) {
        groups.add(group);
    }

    /**
     * Returns the group from the address book that matches the given group.
     * The group must exist in the address book.
     */
    public Group getGroup(Group group) {
        requireNonNull(group);
        return groups.get(group);
    }



    //// tag-level operations

    /**
     * Returns true if a tag with the same identity as {@code tag} exists in the address book.
     */
    public boolean hasTag(Tag tag) {
        requireNonNull(tag);
        return tags.contains(tag);
    }

    /**
     * Adds a tag to the address book.
     * The tag must not already exist in the address book.
     */
    public void addTag(Tag tag) {
        tags.add(tag);
    }

    /**
     * Returns the tag from the address book that matches the given tag.
     * The tag must exist in the address book.
     */
    public Tag getTag(Tag tag) {
        requireNonNull(tag);
        return tags.get(tag);
    }

    /**
     * Removes {@code tag} from this {@code AddressBook}.
     * {@code tag} must exist in the address book.
     */
    public void deleteTag(Tag key) {
        tags.remove(key);
    }

    /**
     * Sorts the internal list of persons by name alphabetically.
     */
    public void sortPersonList() {
        persons.sortByName();
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Group> getGroupList() {
        return groups.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Tag> getTagList() {
        return tags.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook)) {
            return false;
        }
        AddressBook otherAddressBook = (AddressBook) other;
        return persons.equals(otherAddressBook.persons)
                && groups.equals(otherAddressBook.groups)
                && tags.equals(otherAddressBook.tags);
    }

    @Override
    public int hashCode() {
        return persons.hashCode() * 31 + groups.hashCode() * 17 + tags.hashCode();
    }

}
