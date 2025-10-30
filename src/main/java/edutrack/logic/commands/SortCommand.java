package edutrack.logic.commands;

import static java.util.Objects.requireNonNull;

import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;

/**
 * Sorts all persons shown in the address book by name alphabetically.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sorts all persons by name alphabetically.\n"
            + "Example: " + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS = "List sorted alphabetically by name";
    public static final String MESSAGE_INVALID_PARAMETERS = "Sort command does not accept any parameters";

    private final boolean hasParameters;

    public SortCommand(boolean hasParameters) {
        this.hasParameters = hasParameters;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (hasParameters) {
            throw new CommandException(MESSAGE_INVALID_PARAMETERS);
        }
        model.sortPersonList();
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof SortCommand)) {
            return false;
        }

        SortCommand otherCommand = (SortCommand) other;
        return hasParameters == otherCommand.hasParameters;
    }
}
