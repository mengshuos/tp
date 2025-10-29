package edutrack.logic.commands;

import static edutrack.logic.commands.CommandTestUtil.assertCommandFailure;
import static edutrack.logic.commands.CommandTestUtil.assertCommandSuccess;
import static edutrack.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edutrack.logic.Logic;
import edutrack.logic.LogicManager;
import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.AddressBook;
import edutrack.model.Model;
import edutrack.model.ModelManager;
import edutrack.model.UserPrefs;
import edutrack.storage.JsonAddressBookStorage;
import edutrack.storage.JsonUserPrefsStorage;
import edutrack.storage.StorageManager;

public class ClearCommandTest {

    @Test
    public void execute_emptyAddressBook_confirmationRequest() {
        Model model = new ModelManager();
        model.setPendingClearConfirmation(false); // Ensure no pending confirmation
        assertCommandFailure(new ClearCommand(), model, ClearCommand.MESSAGE_CONFIRMATION_REQUEST);
    }

    @Test
    public void execute_nonEmptyAddressBook_confirmationRequest() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.setPendingClearConfirmation(false); // Ensure no pending confirmation
        assertCommandFailure(new ClearCommand(), model, ClearCommand.MESSAGE_CONFIRMATION_REQUEST);
    }

    @Test
    public void execute_nonEmptyAddressBook_successAfterConfirmation() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.setPendingClearConfirmation(true); // Set pending confirmation
        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());
        assertCommandSuccess(new ClearCommand(true), model,
                ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_emptyAddressBook_successAfterConfirmation() {
        Model model = new ModelManager();
        model.setPendingClearConfirmation(true); // Set pending confirmation
        Model expectedModel = new ModelManager();
        assertCommandSuccess(new ClearCommand(true), model,
                ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_clearTwice_confirmationPending() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.setPendingClearConfirmation(false);

        // First clear call
        assertCommandFailure(new ClearCommand(), model, ClearCommand.MESSAGE_CONFIRMATION_REQUEST);

        // Second clear call without confirm - should fail with pending message
        assertCommandFailure(new ClearCommand(), model, ClearCommand.MESSAGE_CONFIRMATION_PENDING);
    }

    @Test
    public void execute_clearConfirmWithoutClearFirst_noSkip() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.setPendingClearConfirmation(false);

        // Try to confirm without typing clear first
        assertCommandFailure(new ClearCommand(true), model, ClearCommand.MESSAGE_NO_SKIP);
    }

    @Test
    public void execute_fullClearFlow_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.setPendingClearConfirmation(false);

        // Step 1: Type "clear" - should request confirmation
        assertCommandFailure(new ClearCommand(), model, ClearCommand.MESSAGE_CONFIRMATION_REQUEST);

        // Verify that the address book still has persons
        assert !model.getFilteredPersonList().isEmpty();

        // Step 2: Type "clear confirm" - should successfully clear
        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());
        assertCommandSuccess(new ClearCommand(true), model,
                ClearCommand.MESSAGE_SUCCESS, expectedModel);

        // Verify that the address book is now empty
        assert model.getFilteredPersonList().isEmpty();
    }

    @Test
    public void execute_pendingFlagResetAfterSuccessfulClear() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.setPendingClearConfirmation(true);
        assertCommandSuccess(new ClearCommand(true), model, ClearCommand.MESSAGE_SUCCESS,
                new ModelManager(new AddressBook(), new UserPrefs()));

        assert !model.isPendingClearConfirmation();
    }

    @Test
    public void execute_canStartNewClearCycleAfterSuccessfulClear() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        // First clear cycle
        assertCommandFailure(new ClearCommand(), model, ClearCommand.MESSAGE_CONFIRMATION_REQUEST);
        assertCommandSuccess(new ClearCommand(true), model, ClearCommand.MESSAGE_SUCCESS,
                new ModelManager(new AddressBook(), new UserPrefs()));

        // Add some persons back
        Model freshModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        // Should be able to start a new clear cycle
        assertCommandFailure(new ClearCommand(), freshModel, ClearCommand.MESSAGE_CONFIRMATION_REQUEST);
    }

    @Test
    public void execute_multipleClearCalls_confirmationPending() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.setPendingClearConfirmation(false);

        // First call
        assertCommandFailure(new ClearCommand(), model, ClearCommand.MESSAGE_CONFIRMATION_REQUEST);

        // Multiple subsequent calls should all return pending message
        assertCommandFailure(new ClearCommand(), model, ClearCommand.MESSAGE_CONFIRMATION_PENDING);
        assertCommandFailure(new ClearCommand(), model, ClearCommand.MESSAGE_CONFIRMATION_PENDING);
        assertCommandFailure(new ClearCommand(), model, ClearCommand.MESSAGE_CONFIRMATION_PENDING);
    }

    @Test
    public void execute_confirmClearsAllPersons() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        model.setPendingClearConfirmation(true);
        assertCommandSuccess(new ClearCommand(true), model, ClearCommand.MESSAGE_SUCCESS,
                new ModelManager(new AddressBook(), new UserPrefs()));

        // Verify all persons are removed
        assert model.getFilteredPersonList().isEmpty();
    }

    @Test
    public void execute_clearWithoutConfirmPreservesState() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        AddressBook originalAddressBook = new AddressBook(model.getAddressBook());
        model.setPendingClearConfirmation(false);

        // Call clear without confirm
        assertCommandFailure(new ClearCommand(), model, ClearCommand.MESSAGE_CONFIRMATION_REQUEST);

        // Verify that nothing changed
        assert model.getAddressBook().equals(originalAddressBook);
        assert model.getFilteredPersonList().size() == originalAddressBook.getPersonList().size();
    }

    @Test
    public void execute_clearConfirmChangesPendingFlagToFalse() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.setPendingClearConfirmation(true);

        assertCommandSuccess(new ClearCommand(true), model, ClearCommand.MESSAGE_SUCCESS,
                new ModelManager(new AddressBook(), new UserPrefs()));

        // Verify pending flag is reset
        assert !model.isPendingClearConfirmation();
    }

    @Test
    public void execute_clearTwiceThenConfirm_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        // Clear call 1
        assertCommandFailure(new ClearCommand(), model, ClearCommand.MESSAGE_CONFIRMATION_REQUEST);

        // Clear call 2 (should show pending message)
        assertCommandFailure(new ClearCommand(), model, ClearCommand.MESSAGE_CONFIRMATION_PENDING);

        // Now confirm - should work
        assertCommandSuccess(new ClearCommand(true), model, ClearCommand.MESSAGE_SUCCESS,
                new ModelManager(new AddressBook(), new UserPrefs()));
    }

    @Test
    public void execute_pendingFlagSetAfterFirstClear() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.setPendingClearConfirmation(false);

        // Before clear
        assert !model.isPendingClearConfirmation();

        // Call clear (will fail but should set flag)
        try {
            new ClearCommand().execute(model);
        } catch (CommandException e) {
            // Expected to fail
        }

        // After clear
        assert model.isPendingClearConfirmation();
    }

    @Test
    public void execute_clearThenFlagResetThenConfirm_fails(@TempDir Path tempDir) throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(tempDir.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(tempDir.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        Logic logic = new LogicManager(model, storage);

        // Step 1: Type "clear" - should request confirmation
        try {
            logic.execute(ClearCommand.COMMAND_WORD);
            assert false : "Should throw CommandException";
        } catch (CommandException e) {
            assert e.getMessage().equals(ClearCommand.MESSAGE_CONFIRMATION_REQUEST);
        }

        // Step 2: Execute a non-clear command (simulating "help" or any other command)
        logic.execute(HelpCommand.COMMAND_WORD);

        // Step 3: Try to confirm - should fail because flag was reset by the previous command
        try {
            logic.execute(ClearCommand.COMMAND_WORD + " confirm");
            assert false : "Should throw CommandException";
        } catch (CommandException e) {
            assert e.getMessage().equals(ClearCommand.MESSAGE_NO_SKIP);
        }
    }

    @Test
    public void execute_clearThenFlagResetThenClearAgain_works(@TempDir Path tempDir) throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(tempDir.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(tempDir.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        Logic logic = new LogicManager(model, storage);

        // Step 1: Type "clear" - should request confirmation
        try {
            logic.execute(ClearCommand.COMMAND_WORD);
            assert false : "Should throw CommandException";
        } catch (CommandException e) {
            assert e.getMessage().equals(ClearCommand.MESSAGE_CONFIRMATION_REQUEST);
        }

        // Step 2: Execute a non-clear command (simulating "list" or any other command)
        logic.execute(ListCommand.COMMAND_WORD);

        // Step 3: Type "clear" again - should request confirmation again
        try {
            logic.execute(ClearCommand.COMMAND_WORD);
            assert false : "Should throw CommandException";
        } catch (CommandException e) {
            assert e.getMessage().equals(ClearCommand.MESSAGE_CONFIRMATION_REQUEST);
        }

        // Step 4: Now confirm - should work
        logic.execute(ClearCommand.COMMAND_WORD + " confirm");
        assert model.getAddressBook().getPersonList().isEmpty();
    }

    @Test
    public void execute_clearResetClearMultipleTimes_confirmationPending(@TempDir Path tempDir) throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(tempDir.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(tempDir.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        Logic logic = new LogicManager(model, storage);

        // Step 1: Type "clear"
        try {
            logic.execute(ClearCommand.COMMAND_WORD);
            assert false : "Should throw CommandException";
        } catch (CommandException e) {
            assert e.getMessage().equals(ClearCommand.MESSAGE_CONFIRMATION_REQUEST);
        }

        // Step 2: Execute an invalid command (simulating gibberish)
        try {
            logic.execute("gibberish");
            assert false : "Should throw ParseException";
        } catch (edutrack.logic.parser.exceptions.ParseException e) {
            // Expected - invalid command
        }

        // Step 3: Type "clear" again
        try {
            logic.execute(ClearCommand.COMMAND_WORD);
            assert false : "Should throw CommandException";
        } catch (CommandException e) {
            assert e.getMessage().equals(ClearCommand.MESSAGE_CONFIRMATION_REQUEST);
        }

        // Step 4: Type "clear" multiple times without confirm - should show pending
        try {
            logic.execute(ClearCommand.COMMAND_WORD);
            assert false : "Should throw CommandException";
        } catch (CommandException e) {
            assert e.getMessage().equals(ClearCommand.MESSAGE_CONFIRMATION_PENDING);
        }
        try {
            logic.execute(ClearCommand.COMMAND_WORD);
            assert false : "Should throw CommandException";
        } catch (CommandException e) {
            assert e.getMessage().equals(ClearCommand.MESSAGE_CONFIRMATION_PENDING);
        }
    }

    @Test
    public void execute_emptyBookClearResetClearConfirm_success(@TempDir Path tempDir) throws Exception {
        Model model = new ModelManager();
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(tempDir.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(tempDir.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        Logic logic = new LogicManager(model, storage);

        // Step 1: Type "clear" on empty book
        try {
            logic.execute(ClearCommand.COMMAND_WORD);
            assert false : "Should throw CommandException";
        } catch (CommandException e) {
            assert e.getMessage().equals(ClearCommand.MESSAGE_CONFIRMATION_REQUEST);
        }

        // Step 2: Execute a non-clear command (simulating "help")
        logic.execute(HelpCommand.COMMAND_WORD);

        // Step 3: Type "clear" again
        try {
            logic.execute(ClearCommand.COMMAND_WORD);
            assert false : "Should throw CommandException";
        } catch (CommandException e) {
            assert e.getMessage().equals(ClearCommand.MESSAGE_CONFIRMATION_REQUEST);
        }

        // Step 4: Confirm - should succeed even on empty book
        logic.execute(ClearCommand.COMMAND_WORD + " confirm");
        assert model.getAddressBook().getPersonList().isEmpty();
    }

}
