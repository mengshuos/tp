package edutrack.logic.commands;

import static java.util.Objects.requireNonNull;

import edutrack.commons.util.ToStringBuilder;
import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;
import edutrack.model.group.Group;

/**
 * Deletes a group identified by group name.
 */
public class GroupDeleteCommand extends Command {

    public static final String COMMAND_WORD = "group/delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the group with the specified name.\n"
            + "Parameters: g/GROUP_NAME\n"
            + "Example: " + COMMAND_WORD + " g/CS2103T";

    public static final String MESSAGE_DELETE_GROUP_SUCCESS = "Deleted Group: %1$s";

    private final Group targetGroup;

    /**
     * Creates a {@code GroupDeleteCommand} to delete the specified {@code Group}.
     *
     * @param group The group to delete.
     */
    public GroupDeleteCommand(Group group) {
        requireNonNull(group);
        this.targetGroup = group;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.hasGroup(targetGroup)) {
            throw new CommandException("This group does not exist in the address book");
        }
        model.deleteGroup(targetGroup);
        return new CommandResult(String.format(MESSAGE_DELETE_GROUP_SUCCESS, targetGroup.groupName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof GroupDeleteCommand)) {
            return false;
        }
        GroupDeleteCommand otherCmd = (GroupDeleteCommand) other;
        return targetGroup.equals(otherCmd.targetGroup);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetGroup", targetGroup)
                .toString();
    }
}
