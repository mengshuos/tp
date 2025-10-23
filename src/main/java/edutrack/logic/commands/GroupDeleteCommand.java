package edutrack.logic.commands;

import static edutrack.logic.parser.CliSyntax.PREFIX_GROUP;
import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;
import edutrack.model.group.Group;
import edutrack.model.person.Person;

/**
 * Deletes a group from the address book.
 * Removes the group from all persons that belong to it.
 */
public class GroupDeleteCommand extends Command {
    public static final String COMMAND_WORD = "group/delete";
  
    public static final String MESSAGE_USAGE = COMMAND_WORD 
            + ": Deletes a group "
            + "and removes it from all persons that belong to it. "
            + "Parameters: "
            + PREFIX_GROUP + "GROUP_NAME\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_GROUP + "CS2103T";
  
    public static final String MESSAGE_SUCCESS = "Group deleted: %s (removed from %d person(s))";
  
    public static final String MESSAGE_GROUP_NOT_FOUND = "This group does not exist.";

    private final Group toDelete;

    public GroupDeleteCommand(Group toDelete) {
        requireNonNull(toDelete);
        this.toDelete = toDelete;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.hasGroup(toDelete)) {
            throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
        }

        // Get the central group reference
        Group centralGroup = model.getGroup(toDelete);

        // Remove group from all persons
        List<Person> allPersons = model.getAddressBook().getPersonList();
        int affectedPersonCount = 0;

        for (Person person : allPersons) {
            if (person.getGroups().contains(centralGroup)) {
                // Create new person without this group
                Set<Group> updatedGroups = new HashSet<>(person.getGroups());
                updatedGroups.remove(centralGroup);

                Person updatedPerson = new Person(
                        person.getName(),
                        person.getPhone(),
                        person.getEmail(),
                        person.getAddress(),
                        person.getTags(),
                        updatedGroups
                );

                model.setPerson(person, updatedPerson);
                affectedPersonCount++;
            }
        }

        // Delete the group from the model
        model.deleteGroup(centralGroup);

        return new CommandResult(String.format(MESSAGE_SUCCESS, centralGroup.toString(), affectedPersonCount));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof GroupDeleteCommand)) {
            return false;
        }

        GroupDeleteCommand otherCommand = (GroupDeleteCommand) other;
        return toDelete.equals(otherCommand.toDelete);
    }
}

