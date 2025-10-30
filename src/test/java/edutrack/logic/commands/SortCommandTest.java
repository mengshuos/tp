package edutrack.logic.commands;

import static edutrack.logic.commands.CommandTestUtil.assertCommandSuccess;
import static edutrack.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;
import edutrack.model.ModelManager;
import edutrack.model.UserPrefs;
import edutrack.model.person.Person;
import edutrack.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code SortCommand}.
 */
public class SortCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_emptyAddressBook_success() {
        Model emptyModel = new ModelManager();
        assertCommandSuccess(new SortCommand(false), emptyModel, SortCommand.MESSAGE_SUCCESS, emptyModel);
    }

    @Test
    public void execute_typicalAddressBook_success() {
        assertCommandSuccess(new SortCommand(false), model, SortCommand.MESSAGE_SUCCESS, expectedModel);
        expectedModel.sortPersonList();
        assertEquals(model.getFilteredPersonList(), expectedModel.getFilteredPersonList());
    }

    @Test
    public void execute_withParameters_throwsCommandException() {
        assertThrows(CommandException.class, () -> new SortCommand(true).execute(model));
    }

    @Test
    public void execute_mixedCaseNames_sortedCorrectly() throws CommandException {
        // Add persons with mixed case names
        Person alice = new PersonBuilder().withName("alice").build();
        Person bob = new PersonBuilder().withName("BOB").build();
        Person charlie = new PersonBuilder().withName("Charlie").build();

        Model testModel = new ModelManager();
        testModel.addPerson(charlie); // Add in reverse order to test sorting
        testModel.addPerson(bob);
        testModel.addPerson(alice);

        Model expectedModel = new ModelManager();
        expectedModel.addPerson(alice);
        expectedModel.addPerson(bob);
        expectedModel.addPerson(charlie);
        expectedModel.sortPersonList();

        assertCommandSuccess(new SortCommand(false), testModel, SortCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_duplicateNameDifferentCase_throwsDuplicatePersonException() {
        Person aaronLowerCase = new PersonBuilder().withName("aaron").build();
        Person aaronUpperCase = new PersonBuilder().withName("Aaron").build();

        Model testModel = new ModelManager();
        testModel.addPerson(aaronUpperCase);
        assertThrows(edutrack.model.person.exceptions.DuplicatePersonException.class, (
                ) -> testModel.addPerson(aaronLowerCase));
    }
}
