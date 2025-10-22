package edutrack.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edutrack.commons.core.index.Index;
import edutrack.commons.util.ToStringBuilder;
import edutrack.logic.Messages;
import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;
import edutrack.model.person.Person;
import edutrack.model.tag.Tag;

/**
 * Unassigns a tag from a person identified by index.
 */
public class TagUnassignCommand extends Command {
    public static final String COMMAND_WORD = "tag/unassign";
    public static final String MESSAGE_SUCCESS = "Unassigned tag from %1$s: %2$s";
    public static final String MESSAGE_TAG_NOT_FOUND = "This tag does not exist in the address book";
    public static final String MESSAGE_TAG_NOT_ASSIGNED = "This person does not have this tag";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Unassigns a tag from a person.\n"
            + "Parameters: INDEX (must be a positive integer) t/TAG\n"
            + "Example: " + COMMAND_WORD + " 1 t/Physics";

    private final Index index;
    private final Tag tag;

    /**
     * Creates a TagUnassignCommand to unassign the specified {@code Tag} from a person at {@code Index}
     */
    public TagUnassignCommand(Index index, Tag tag) {
        requireNonNull(index);
        requireNonNull(tag);
        this.index = index;
        this.tag = tag;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.hasTag(tag)) {
            throw new CommandException(MESSAGE_TAG_NOT_FOUND);
        }

        List<Person> lastShownList = model.getFilteredPersonList();
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());

        if (!personToEdit.getTags().contains(tag)) {
            throw new CommandException(MESSAGE_TAG_NOT_ASSIGNED);
        }

        // Remove tag from person
        Set<Tag> updatedTags = new HashSet<>(personToEdit.getTags());
        updatedTags.remove(tag);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                updatedTags,
                personToEdit.getGroups());

        model.setPerson(personToEdit, editedPerson);

        return new CommandResult(String.format(MESSAGE_SUCCESS, editedPerson.getName(), tag));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TagUnassignCommand)) {
            return false;
        }

        TagUnassignCommand otherCommand = (TagUnassignCommand) other;
        return index.equals(otherCommand.index) && tag.equals(otherCommand.tag);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("tag", tag)
                .toString();
    }
}

