package edutrack.logic.commands;

import static edutrack.logic.commands.CommandTestUtil.assertCommandFailure;
import static edutrack.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import edutrack.model.Model;
import edutrack.model.ModelManager;
import edutrack.model.UserPrefs;
import edutrack.model.group.Group;

/**
 * Contains integration tests (interaction with the Model) and unit tests for GroupDeleteCommand.
 */
public class GroupDeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validGroup_success() throws Exception {
        Group groupToDelete = new Group("CS2103T");
        GroupDeleteCommand deleteCommand = new GroupDeleteCommand(groupToDelete);

        // Execute the command
        deleteCommand.execute(model);

        // Verify group is deleted
        assertFalse(model.hasGroup(groupToDelete));

        // Verify group is removed from all persons
        long countAfter = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getGroups().stream()
                        .anyMatch(g -> g.equals(groupToDelete)))
                .count();
        assertEquals(0, countAfter);
    }

    @Test
    public void execute_nonExistentGroup_throwsCommandException() {
        Group nonExistentGroup = new Group("NonExistentGroup");
        GroupDeleteCommand deleteCommand = new GroupDeleteCommand(nonExistentGroup);

        assertCommandFailure(deleteCommand, model, GroupDeleteCommand.MESSAGE_GROUP_NOT_FOUND);
    }

    @Test
    public void execute_deleteGroupRemovesFromPersons() throws Exception {
        Group groupToDelete = new Group("CS2103T");
        GroupDeleteCommand deleteCommand = new GroupDeleteCommand(groupToDelete);

        // Count persons with CS2103T before deletion
        long countBefore = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getGroups().stream()
                        .anyMatch(g -> g.equals(groupToDelete)))
                .count();

        assertTrue(countBefore > 0, "Test data should have persons with CS2103T");

        deleteCommand.execute(model);

        // Verify no person has CS2103T after deletion
        long countAfter = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getGroups().stream()
                        .anyMatch(g -> g.equals(groupToDelete)))
                .count();

        assertEquals(0, countAfter);
    }

    @Test
    public void equals() {
        Group group1 = new Group("CS2103T");
        Group group2 = new Group("CS2101");
        GroupDeleteCommand deleteGroup1Command = new GroupDeleteCommand(group1);
        GroupDeleteCommand deleteGroup2Command = new GroupDeleteCommand(group2);

        // same object -> returns true
        assertTrue(deleteGroup1Command.equals(deleteGroup1Command));

        // same values -> returns true
        GroupDeleteCommand deleteGroup1CommandCopy = new GroupDeleteCommand(group1);
        assertTrue(deleteGroup1Command.equals(deleteGroup1CommandCopy));

        // different types -> returns false
        assertFalse(deleteGroup1Command.equals(1));

        // null -> returns false
        assertFalse(deleteGroup1Command.equals(null));

        // different group -> returns false
        assertFalse(deleteGroup1Command.equals(deleteGroup2Command));
    }
}

