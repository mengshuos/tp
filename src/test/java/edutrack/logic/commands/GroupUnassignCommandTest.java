package edutrack.logic.commands;

import static edutrack.logic.commands.CommandTestUtil.assertCommandFailure;
import static edutrack.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static edutrack.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import edutrack.commons.core.index.Index;
import edutrack.logic.Messages;
import edutrack.model.Model;
import edutrack.model.ModelManager;
import edutrack.model.UserPrefs;
import edutrack.model.group.Group;
import edutrack.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) for GroupUnassignCommand.
 */
public class GroupUnassignCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validGroupAndIndices_success() throws Exception {
        // ALICE has CS2103T in typical data
        Group group = new Group("CS2103T");
        List<Index> indices = Arrays.asList(INDEX_FIRST_PERSON);
        GroupUnassignCommand unassignCommand = new GroupUnassignCommand(group, indices);

        Person personBefore = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(personBefore.getGroups().stream().anyMatch(group::equals));

        unassignCommand.execute(model);

        // Verify person no longer has the group
        Person personAfter = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertFalse(personAfter.getGroups().stream().anyMatch(group::equals));
    }

    @Test
    public void execute_nonExistentGroup_throwsCommandException() {
        Group nonExistentGroup = new Group("NonExistentGroup");
        List<Index> indices = Arrays.asList(INDEX_FIRST_PERSON);
        GroupUnassignCommand unassignCommand = new GroupUnassignCommand(nonExistentGroup, indices);

        assertCommandFailure(unassignCommand, model, GroupUnassignCommand.MESSAGE_GROUP_NOT_FOUND);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Group group = new Group("CS2103T");
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        List<Index> indices = Arrays.asList(outOfBoundIndex);
        GroupUnassignCommand unassignCommand = new GroupUnassignCommand(group, indices);

        assertCommandFailure(unassignCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        Group group1 = new Group("CS2103T");
        Group group2 = new Group("CS2101");
        List<Index> indices1 = Arrays.asList(INDEX_FIRST_PERSON);
        List<Index> indices2 = Arrays.asList(Index.fromOneBased(2));

        GroupUnassignCommand unassignGroup1Command = new GroupUnassignCommand(group1, indices1);
        GroupUnassignCommand unassignGroup2Command = new GroupUnassignCommand(group2, indices1);
        GroupUnassignCommand unassignGroup1DifferentIndices = new GroupUnassignCommand(group1, indices2);

        // same object -> returns true
        assertTrue(unassignGroup1Command.equals(unassignGroup1Command));

        // same values -> returns true
        GroupUnassignCommand unassignGroup1CommandCopy = new GroupUnassignCommand(group1, indices1);
        assertTrue(unassignGroup1Command.equals(unassignGroup1CommandCopy));

        // different types -> returns false
        assertFalse(unassignGroup1Command.equals(1));

        // null -> returns false
        assertFalse(unassignGroup1Command.equals(null));

        // different group -> returns false
        assertFalse(unassignGroup1Command.equals(unassignGroup2Command));

        // different indices -> returns false
        assertFalse(unassignGroup1Command.equals(unassignGroup1DifferentIndices));
    }
}

