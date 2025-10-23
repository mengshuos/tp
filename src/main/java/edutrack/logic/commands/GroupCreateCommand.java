package edutrack.logic.commands;

import static edutrack.logic.parser.CliSyntax.PREFIX_GROUP;
import static java.util.Objects.requireNonNull;

import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;
import edutrack.model.group.Group;

/**
 * Creates a group.
 */

public class GroupCreateCommand extends Command {
    public static final String COMMAND_WORD = "group/create";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Creates a new group. "
            + "Parameters: "
            + PREFIX_GROUP + "GROUP_NAME\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_GROUP + "CS2103T";
    public static final String MESSAGE_SUCCESS = "New group created: %s";
    public static final String MESSAGE_DUPLICATE = "This group already exists.";

    private final Group toAdd;

    public GroupCreateCommand(Group toAdd) {
        this.toAdd = toAdd;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.hasGroup(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE);
        }
        model.addGroup(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd.toString()));
    }
}


