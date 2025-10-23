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

