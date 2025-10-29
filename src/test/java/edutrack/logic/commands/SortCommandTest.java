package edutrack.logic.commands;

import static edutrack.logic.commands.CommandTestUtil.assertCommandSuccess;
import static edutrack.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertCommandSuccess(new SortCommand(), emptyModel, SortCommand.MESSAGE_SUCCESS, emptyModel);
    }

    @Test
    public void execute_typicalAddressBook_success() {
        assertCommandSuccess(new SortCommand(), model, SortCommand.MESSAGE_SUCCESS, expectedModel);
        expectedModel.sortPersonList();
        assertEquals(model.getFilteredPersonList(), expectedModel.getFilteredPersonList());
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

        assertCommandSuccess(new SortCommand(), testModel, SortCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_duplicateLetterDifferentCase_sortedCorrectly() throws CommandException {
        // Test names starting with same letter in different cases
        Person aaronLowerCase = new PersonBuilder().withName("aaron").build();
        Person aaronUpperCase = new PersonBuilder().withName("Aaron").build();
        Person bob = new PersonBuilder().withName("bob").build();
        Model testModel = new ModelManager();
        testModel.addPerson(bob);
        testModel.addPerson(aaronUpperCase);
        testModel.addPerson(aaronLowerCase);

        // Execute the command and verify the sorted order directly on the filtered list
        CommandResult result = new SortCommand().execute(testModel);
        assertEquals(SortCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());

        // Verify the order is correct and stable (case-insensitive)
        assertEquals(3, testModel.getFilteredPersonList().size());
        assertEquals("aaron", testModel.getFilteredPersonList().get(0).getName().toString().toLowerCase());
        assertEquals("aaron", testModel.getFilteredPersonList().get(1).getName().toString().toLowerCase());
        assertEquals("bob", testModel.getFilteredPersonList().get(2).getName().toString().toLowerCase());
    }
}
