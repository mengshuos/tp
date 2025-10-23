package edutrack.logic.commands;

import static edutrack.logic.commands.CommandTestUtil.assertCommandFailure;
import static edutrack.logic.commands.CommandTestUtil.assertCommandSuccess;
import static edutrack.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static edutrack.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import edutrack.commons.core.index.Index;
import edutrack.logic.Messages;
import edutrack.model.Model;
import edutrack.model.ModelManager;
import edutrack.model.UserPrefs;
import edutrack.model.person.Person;
import edutrack.model.tag.Tag;

/**
 * Contains integration tests (interaction with the Model) for {@code TagUnassignCommand}.
 */
public class TagUnassignCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validTagAndIndex_success() throws Exception {
        Model freshModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Tag tag = new Tag("UniqueUnassignTag123");
        if (!freshModel.hasTag(tag)) {
            freshModel.addTag(tag);
        }

        // First assign the tag
        TagAssignCommand assignCommand = new TagAssignCommand(INDEX_FIRST_PERSON, tag);
        assignCommand.execute(freshModel);

        // Get the person with the assigned tag
        Person personToEdit = freshModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String expectedMessage = String.format(TagUnassignCommand.MESSAGE_SUCCESS,
                personToEdit.getName(), tag);

        // Create expected model and manually unassign the tag
        Model expectedModel = new ModelManager(freshModel.getAddressBook(), new UserPrefs());
        java.util.Set<Tag> updatedTags = new java.util.HashSet<>(personToEdit.getTags());
        updatedTags.remove(tag);
        Person expectedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                updatedTags,
                personToEdit.getGroups());
        expectedModel.setPerson(personToEdit, expectedPerson);

        // Then unassign it
        TagUnassignCommand unassignCommand = new TagUnassignCommand(INDEX_FIRST_PERSON, tag);
        assertCommandSuccess(unassignCommand, freshModel, expectedMessage, expectedModel);
    }

    @Test
    public void execute_tagNotFound_throwsCommandException() {
        Tag nonExistentTag = new Tag("NonExistent");
        TagUnassignCommand unassignCommand = new TagUnassignCommand(INDEX_FIRST_PERSON, nonExistentTag);

        assertCommandFailure(unassignCommand, model, TagUnassignCommand.MESSAGE_TAG_NOT_FOUND);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Tag tag = new Tag("UniqueUnassignTag456");
        if (!model.hasTag(tag)) {
            model.addTag(tag);
        }

        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        TagUnassignCommand unassignCommand = new TagUnassignCommand(outOfBoundIndex, tag);

        assertCommandFailure(unassignCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_tagNotAssigned_throwsCommandException() {
        Tag tag = new Tag("UniqueUnassignTag789");
        if (!model.hasTag(tag)) {
            model.addTag(tag);
        }

        TagUnassignCommand unassignCommand = new TagUnassignCommand(INDEX_FIRST_PERSON, tag);
        assertCommandFailure(unassignCommand, model, TagUnassignCommand.MESSAGE_TAG_NOT_ASSIGNED);
    }

    @Test
    public void equals() {
        Tag physics = new Tag("Physics");
        Tag chemistry = new Tag("Chemistry");
        TagUnassignCommand unassignPhysicsFromFirst = new TagUnassignCommand(INDEX_FIRST_PERSON, physics);
        TagUnassignCommand unassignChemistryFromFirst = new TagUnassignCommand(INDEX_FIRST_PERSON, chemistry);

        // same object -> returns true
        assertTrue(unassignPhysicsFromFirst.equals(unassignPhysicsFromFirst));

        // same values -> returns true
        TagUnassignCommand unassignPhysicsFromFirstCopy = new TagUnassignCommand(INDEX_FIRST_PERSON, physics);
        assertTrue(unassignPhysicsFromFirst.equals(unassignPhysicsFromFirstCopy));

        // different types -> returns false
        assertFalse(unassignPhysicsFromFirst.equals(1));

        // null -> returns false
        assertFalse(unassignPhysicsFromFirst.equals(null));

        // different tag -> returns false
        assertFalse(unassignPhysicsFromFirst.equals(unassignChemistryFromFirst));

        // different index -> returns false
        Index secondIndex = Index.fromOneBased(2);
        TagUnassignCommand unassignPhysicsFromSecond = new TagUnassignCommand(secondIndex, physics);
        assertFalse(unassignPhysicsFromFirst.equals(unassignPhysicsFromSecond));
    }
}

