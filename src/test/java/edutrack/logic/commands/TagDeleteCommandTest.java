package edutrack.logic.commands;

import static edutrack.logic.commands.CommandTestUtil.assertCommandFailure;
import static edutrack.logic.commands.CommandTestUtil.assertCommandSuccess;
import static edutrack.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import edutrack.model.Model;
import edutrack.model.ModelManager;
import edutrack.model.UserPrefs;
import edutrack.model.tag.Tag;

/**
 * Contains integration tests (interaction with the Model) for {@code TagDeleteCommand}.
 */
public class TagDeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_existingTag_success() {
        Tag tagToDelete = new Tag("UniqueDeleteTag123");
        if (!model.hasTag(tagToDelete)) {
            model.addTag(tagToDelete);
        }
        TagDeleteCommand tagDeleteCommand = new TagDeleteCommand(tagToDelete);

        String expectedMessage = String.format(TagDeleteCommand.MESSAGE_SUCCESS, tagToDelete);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        if (!expectedModel.hasTag(tagToDelete)) {
            expectedModel.addTag(tagToDelete);
        }
        expectedModel.deleteTag(tagToDelete);

        assertCommandSuccess(tagDeleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nonExistentTag_throwsCommandException() {
        Tag nonExistentTag = new Tag("NonExistent");
        TagDeleteCommand tagDeleteCommand = new TagDeleteCommand(nonExistentTag);

        assertCommandFailure(tagDeleteCommand, model, TagDeleteCommand.MESSAGE_TAG_NOT_FOUND);
    }

    @Test
    public void execute_tagWithAssignedPersons_cascadeDeletesFromPersons() {
        // Create a tag and assign it to multiple persons
        Tag testTag = new Tag("CascadeTestTag");
        model.addTag(testTag);

        // Get first two persons and assign the tag to them
        edutrack.model.person.Person person1 = model.getFilteredPersonList().get(0);
        edutrack.model.person.Person person2 = model.getFilteredPersonList().get(1);

        java.util.Set<Tag> tags1 = new java.util.HashSet<>(person1.getTags());
        tags1.add(testTag);
        edutrack.model.person.Person updatedPerson1 = new edutrack.model.person.Person(
                person1.getName(), person1.getPhone(), person1.getEmail(),
                person1.getAddress(), tags1, person1.getGroups());
        model.setPerson(person1, updatedPerson1);

        java.util.Set<Tag> tags2 = new java.util.HashSet<>(person2.getTags());
        tags2.add(testTag);
        edutrack.model.person.Person updatedPerson2 = new edutrack.model.person.Person(
                person2.getName(), person2.getPhone(), person2.getEmail(),
                person2.getAddress(), tags2, person2.getGroups());
        model.setPerson(person2, updatedPerson2);

        // Verify persons have the tag
        assert(model.getFilteredPersonList().get(0).getTags().contains(testTag));
        assert(model.getFilteredPersonList().get(1).getTags().contains(testTag));

        // Delete the tag
        TagDeleteCommand tagDeleteCommand = new TagDeleteCommand(testTag);
        try {
            tagDeleteCommand.execute(model);
        } catch (Exception e) {
            throw new AssertionError("Command execution should not fail");
        }

        // Verify tag is removed from both persons
        assert(!model.getFilteredPersonList().get(0).getTags().contains(testTag));
        assert(!model.getFilteredPersonList().get(1).getTags().contains(testTag));

        // Verify tag is removed from central registry
        assert(!model.hasTag(testTag));
    }

    @Test
    public void equals() {
        Tag physics = new Tag("Physics");
        Tag chemistry = new Tag("Chemistry");
        TagDeleteCommand deletePhysicsCommand = new TagDeleteCommand(physics);
        TagDeleteCommand deleteChemistryCommand = new TagDeleteCommand(chemistry);

        // same object -> returns true
        assert(deletePhysicsCommand.equals(deletePhysicsCommand));

        // same values -> returns true
        TagDeleteCommand deletePhysicsCommandCopy = new TagDeleteCommand(physics);
        assert(deletePhysicsCommand.equals(deletePhysicsCommandCopy));

        // different types -> returns false
        assert(!deletePhysicsCommand.equals(1));

        // null -> returns false
        assert(!deletePhysicsCommand.equals(null));

        // different tag -> returns false
        assert(!deletePhysicsCommand.equals(deleteChemistryCommand));
    }
}

