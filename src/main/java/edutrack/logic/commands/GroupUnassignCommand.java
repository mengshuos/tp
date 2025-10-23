package edutrack.logic.commands;

import static edutrack.logic.parser.CliSyntax.PREFIX_GROUP;
import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edutrack.commons.core.index.Index;
import edutrack.logic.Messages;
import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;
import edutrack.model.group.Group;
import edutrack.model.person.Person;

/**
 * Unassigns a group from one or more persons in the address book.
 */
public class GroupUnassignCommand extends Command {
    public static final String COMMAND_WORD = "group/unassign";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Removes a group from one or more persons "
            + "identified by their index numbers in the displayed person list. "
            + "Parameters: INDEX [INDEX]... "
            + PREFIX_GROUP + "GROUP_NAME\n"
            + "Example: " + COMMAND_WORD + " 1 2 3 "
            + PREFIX_GROUP + "CS2103T";
    public static final String MESSAGE_SUCCESS = "Unassigned group %s from %d person(s)";
    public static final String MESSAGE_GROUP_NOT_FOUND = "This group does not exist.";

    private final Group group;
    private final List<Index> indices;

    /**
     * Creates a GroupUnassignCommand to unassign the specified group from persons at the given indices.
     */
    public GroupUnassignCommand(Group group, List<Index> indices) {
        requireNonNull(group);
        requireNonNull(indices);
        this.group = group;
        this.indices = indices;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.hasGroup(group)) {
            throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
        }

        // Get the centrally tracked Group object
        Group centralGroup = model.getGroup(group);

        List<Person> lastShownList = model.getFilteredPersonList();
        int successCount = 0;

        // Validate all indices first
        for (Index index : indices) {
            if (index.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
        }

        // Unassign group from each person
        for (Index index : indices) {
            Person personToEdit = lastShownList.get(index.getZeroBased());

            // Remove the central group from person's groups
            Set<Group> updatedGroups = new HashSet<>(personToEdit.getGroups());

            // Only update if person actually has this group
            if (updatedGroups.contains(centralGroup)) {
                updatedGroups.remove(centralGroup);

                Person updatedPerson = new Person(
                        personToEdit.getName(),
                        personToEdit.getPhone(),
                        personToEdit.getEmail(),
                        personToEdit.getAddress(),
                        personToEdit.getTags(),
                        updatedGroups
                );

                model.setPerson(personToEdit, updatedPerson);
                successCount++;
            }
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, centralGroup.toString(), successCount));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof GroupUnassignCommand)) {
            return false;
        }

        GroupUnassignCommand otherCommand = (GroupUnassignCommand) other;
        return group.equals(otherCommand.group)
                && indices.equals(otherCommand.indices);
    }
}

