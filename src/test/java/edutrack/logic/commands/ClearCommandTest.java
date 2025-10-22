package edutrack.logic.commands;

import static edutrack.logic.commands.CommandTestUtil.assertCommandFailure;
import static edutrack.logic.commands.CommandTestUtil.assertCommandSuccess;
import static edutrack.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import edutrack.model.AddressBook;
import edutrack.model.Model;
import edutrack.model.ModelManager;
import edutrack.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_emptyAddressBook_failure() {
        Model model = new ModelManager();
        assertCommandFailure(new ClearCommand(true), model, ClearCommand.MESSAGE_CONFIRMATION_REQUEST);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        assertCommandFailure(new ClearCommand(), model, ClearCommand.MESSAGE_CONFIRMATION_REQUEST);
    }

}
