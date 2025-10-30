package edutrack.logic.commands;

import static edutrack.logic.commands.CommandTestUtil.assertCommandSuccess;
import static edutrack.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edutrack.commons.core.index.Index;
import edutrack.logic.Messages;
import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;
import edutrack.model.ModelManager;
import edutrack.model.UserPrefs;
import edutrack.model.person.Note;
import edutrack.model.person.Person;

public class NoteDeleteCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_validIndexWithNotePresent_success() {
        Index validIndex = Index.fromOneBased(1);
        NoteDeleteCommand command = new NoteDeleteCommand(validIndex);

        Person personToEdit = expectedModel.getFilteredPersonList().get(validIndex.getZeroBased());
        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                personToEdit.getGroups(),
                new Note("") // empty note after deletion
        );

        String expectedMessage = String.format(NoteDeleteCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                Messages.format(editedPerson));
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndexWithNoNote_throwsCommandException() {
        Index validIndex = Index.fromOneBased(3); // Assuming person at index 3 has no note
        NoteDeleteCommand command = new NoteDeleteCommand(validIndex);
        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBounds = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        NoteDeleteCommand command = new NoteDeleteCommand(outOfBounds);
        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void equals_sameAndDifferentBehaviours() {
        NoteDeleteCommand firstCommand = new NoteDeleteCommand(Index.fromOneBased(1));
        NoteDeleteCommand secondCommand = new NoteDeleteCommand(Index.fromOneBased(2));

        // same object -> true
        assertEquals(firstCommand, firstCommand);

        // same values -> true
        assertEquals(firstCommand, new NoteDeleteCommand(Index.fromOneBased(1)));

        // different index -> false
        assertNotEquals(firstCommand, secondCommand);

        // different types -> false
        assertNotEquals(firstCommand, "not a command");

        // null -> false
        assertNotEquals(firstCommand, null);
    }
}
