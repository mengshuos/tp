package edutrack.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.logging.Logger;

import edutrack.commons.core.GuiSettings;
import edutrack.commons.core.LogsCenter;
import edutrack.logic.commands.ClearCommand;
import edutrack.logic.commands.Command;
import edutrack.logic.commands.CommandResult;
import edutrack.logic.commands.exceptions.CommandException;
import edutrack.logic.parser.AddressBookParser;
import edutrack.logic.parser.exceptions.ParseException;
import edutrack.model.Model;
import edutrack.model.ReadOnlyAddressBook;
import edutrack.model.group.Group;
import edutrack.model.person.Person;
import edutrack.storage.Storage;
import javafx.collections.ObservableList;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        String trimmedCommand = commandText.trim();
        // Reset pending clear confirmation if command is not "clear" or doesn't start with "clear "
        // This handles both valid non-clear commands and invalid commands
        // "clear " (with space) ensures words like "clearness" don't match
        if (!trimmedCommand.equals(ClearCommand.COMMAND_WORD)
                && !trimmedCommand.startsWith(ClearCommand.COMMAND_WORD + " ")) {
            model.setPendingClearConfirmation(false);
        }

        CommandResult commandResult;
        // diagnostic log
        logger.info("Parse start");
        Command command = addressBookParser.parseCommand(commandText);
        logger.info("Parsed command: " + command.getClass().getSimpleName());

        // show model identity before execute
        logger.info("Model identity before execute: " + System.identityHashCode(model));
        logger.info("AddressBook before execute: " + model.getAddressBook());
        commandResult = command.execute(model);

        logger.info("Model identity after execute: " + System.identityHashCode(model));
        logger.info("AddressBook after execute: " + model.getAddressBook());

        try {
            logger.info("Saving address book (model id: " + System.identityHashCode(model) + ")");
            storage.saveAddressBook(model.getAddressBook());
            logger.info("Save successful");
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return model.getAddressBook();
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public ObservableList<Group> getFilteredGroupList() {
        return model.getFilteredGroupList();
    }

    @Override
    public Path getAddressBookFilePath() {
        return model.getAddressBookFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
