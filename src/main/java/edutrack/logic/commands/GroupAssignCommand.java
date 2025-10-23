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
 * Assigns a group to one or more persons in the address book.
 */
public class GroupAssignCommand extends Command {
    public static final String COMMAND_WORD = "group/assign";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Assigns a group to one or more persons "
            + "identified by their index numbers in the displayed person list. "
            + "Parameters: INDEX [INDEX]... "
            + PREFIX_GROUP + "GROUP_NAME\n"
            + "Example: " + COMMAND_WORD + " 1 2 3 "
            + PREFIX_GROUP + "CS2103T";
    public static final String MESSAGE_SUCCESS = "Assigned group %s to %d person(s)";
    public static final String MESSAGE_GROUP_NOT_FOUND = "This group does not exist. Create it first using "
            + "group/create.";

    private final Group group;
    private final List<Index> indices;

    /**
     * Creates a GroupAssignCommand to assign the specified group to persons at the given indices.
     */
    public GroupAssignCommand(Group group, List<Index> indices) {
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

        // Assign group to each person
        for (Index index : indices) {
            Person personToEdit = lastShownList.get(index.getZeroBased());

            // Add the central group to person's groups
            Set<Group> updatedGroups = new HashSet<>(personToEdit.getGroups());

            // Only update if person doesn't already have this group
            if (!updatedGroups.contains(centralGroup)) {
                updatedGroups.add(centralGroup);

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

        if (!(other instanceof GroupAssignCommand)) {
            return false;
        }

        GroupAssignCommand otherCommand = (GroupAssignCommand) other;
        return group.equals(otherCommand.group)
                && indices.equals(otherCommand.indices);
    }
}

