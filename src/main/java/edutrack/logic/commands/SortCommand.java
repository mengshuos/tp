package edutrack.logic.commands;

import static java.util.Objects.requireNonNull;

import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;

/**
 * Sorts all persons shown in the address book by name alphabetically.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";

    public static final String MESSAGE_SUCCESS = "List sorted alphabetically by name";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        model.sortPersonList();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
