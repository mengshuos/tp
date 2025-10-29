package edutrack.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;
import edutrack.model.person.Person;
import edutrack.model.tag.Tag;

/**
 * Deletes a tag from the address book.
 * Removes the tag from all persons that have it assigned.
 */
public class TagDeleteCommand extends Command {
    public static final String COMMAND_WORD = "tag/delete";
    public static final String MESSAGE_SUCCESS = "Tag deleted: %s (removed from %d person(s))";
    public static final String MESSAGE_TAG_NOT_FOUND = "This tag does not exist in the address book";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes an existing tag "
            + "and removes it from all persons that have it assigned.\n"
            + "Parameters: t/TAG\n"
            + "Example: " + COMMAND_WORD + " t/Physics";

    private final Tag toDelete;

    /**
     * Creates a TagDeleteCommand to delete the specified {@code Tag}
     */
    public TagDeleteCommand(Tag toDelete) {
        requireNonNull(toDelete);
        this.toDelete = toDelete;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.hasTag(toDelete)) {
            throw new CommandException(MESSAGE_TAG_NOT_FOUND);
        }

        // Remove tag from all persons
        List<Person> allPersons = model.getAddressBook().getPersonList();
        int affectedPersonCount = 0;

        for (Person person : allPersons) {
            if (person.getTags().contains(toDelete)) {
                // Create new person without this tag
                Set<Tag> updatedTags = new HashSet<>(person.getTags());
                updatedTags.remove(toDelete);

                Person updatedPerson = new Person(
                        person.getName(),
                        person.getPhone(),
                        person.getEmail(),
                        person.getAddress(),
                        updatedTags,
                        person.getGroups()
                );

                model.setPerson(person, updatedPerson);
                affectedPersonCount++;
            }
        }

        // Delete the tag from the model
        model.deleteTag(toDelete);

        return new CommandResult(String.format(MESSAGE_SUCCESS, toDelete.toString(), affectedPersonCount));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TagDeleteCommand)) {
            return false;
        }

        TagDeleteCommand otherCommand = (TagDeleteCommand) other;
        return toDelete.equals(otherCommand.toDelete);
    }
}

