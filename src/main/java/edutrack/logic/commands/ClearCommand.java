package edutrack.logic.commands;

import static java.util.Objects.requireNonNull;

import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.AddressBook;
import edutrack.model.Model;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_CONFIRMATION_REQUEST = "Are you sure you want to clear the student list? "
            + "This action cannot be undone. Type 'clear confirm' to confirm.";
    public static final String MESSAGE_CONFIRMATION_PENDING = "Please type 'clear confirm' to proceed.";
    public static final String MESSAGE_NO_SKIP = "Please type 'clear' first.";
    public static final String MESSAGE_SUCCESS = "Student list has been cleared!";

    private final boolean isConfirmed;

    public ClearCommand() {
        this.isConfirmed = false;
    }

    public ClearCommand(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        // if they type clear without confirm
        if (!isConfirmed) {
            // if they have already typed clear once
            if (model.isPendingClearConfirmation()) {
                throw new CommandException(MESSAGE_CONFIRMATION_PENDING);
            }
            // first time typing clear
            model.setPendingClearConfirmation(true);
            return new CommandResult(MESSAGE_CONFIRMATION_REQUEST);
        }

        // if they type clear confirm before typing clear
        if (!model.isPendingClearConfirmation()) {
            throw new CommandException(MESSAGE_NO_SKIP);
        }
        // confirmed clear, reset flag
        model.setPendingClearConfirmation(false);
        model.setAddressBook(new AddressBook());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
