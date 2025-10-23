package edutrack.logic.commands;

import static edutrack.logic.commands.CommandTestUtil.assertCommandFailure;
import static edutrack.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static edutrack.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
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
 * Contains integration tests (interaction with the Model) for GroupAssignCommand.
 */
public class GroupAssignCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validGroupAndIndices_success() throws Exception {
        Group group = new Group("CS2103T");
        List<Index> indices = Arrays.asList(INDEX_FIRST_PERSON);
        GroupAssignCommand assignCommand = new GroupAssignCommand(group, indices);

        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        assignCommand.execute(model);

        // Verify person now has the group
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(editedPerson.getGroups().contains(model.getGroup(group)));
    }

    @Test
    public void execute_nonExistentGroup_throwsCommandException() {
        Group nonExistentGroup = new Group("NonExistentGroup");
        List<Index> indices = Arrays.asList(INDEX_FIRST_PERSON);
        GroupAssignCommand assignCommand = new GroupAssignCommand(nonExistentGroup, indices);

        assertCommandFailure(assignCommand, model, GroupAssignCommand.MESSAGE_GROUP_NOT_FOUND);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Group group = new Group("CS2103T");
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        List<Index> indices = Arrays.asList(outOfBoundIndex);
        GroupAssignCommand assignCommand = new GroupAssignCommand(group, indices);

        assertCommandFailure(assignCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_multipleIndices_success() throws Exception {
        Group group = new Group("CS2103T");
        List<Index> indices = Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);
        GroupAssignCommand assignCommand = new GroupAssignCommand(group, indices);

        assignCommand.execute(model);

        // Verify both persons now have the group
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        assertTrue(firstPerson.getGroups().contains(model.getGroup(group)));
        assertTrue(secondPerson.getGroups().contains(model.getGroup(group)));
    }

    @Test
    public void equals() {
        Group group1 = new Group("CS2103T");
        Group group2 = new Group("CS2101");
        List<Index> indices1 = Arrays.asList(INDEX_FIRST_PERSON);
        List<Index> indices2 = Arrays.asList(INDEX_SECOND_PERSON);

        GroupAssignCommand assignGroup1Command = new GroupAssignCommand(group1, indices1);
        GroupAssignCommand assignGroup2Command = new GroupAssignCommand(group2, indices1);
        GroupAssignCommand assignGroup1DifferentIndices = new GroupAssignCommand(group1, indices2);

        // same object -> returns true
        assertTrue(assignGroup1Command.equals(assignGroup1Command));

        // same values -> returns true
        GroupAssignCommand assignGroup1CommandCopy = new GroupAssignCommand(group1, indices1);
        assertTrue(assignGroup1Command.equals(assignGroup1CommandCopy));

        // different types -> returns false
        assertFalse(assignGroup1Command.equals(1));

        // null -> returns false
        assertFalse(assignGroup1Command.equals(null));

        // different group -> returns false
        assertFalse(assignGroup1Command.equals(assignGroup2Command));

        // different indices -> returns false
        assertFalse(assignGroup1Command.equals(assignGroup1DifferentIndices));
    }
}

