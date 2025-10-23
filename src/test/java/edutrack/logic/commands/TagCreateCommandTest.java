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
 * Contains integration tests (interaction with the Model) for {@code TagCreateCommand}.
 */
public class TagCreateCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_newTag_success() {
        Tag validTag = new Tag("Physics");
        TagCreateCommand tagCreateCommand = new TagCreateCommand(validTag);

        String expectedMessage = String.format(TagCreateCommand.MESSAGE_SUCCESS, validTag);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addTag(validTag);

        assertCommandSuccess(tagCreateCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateTag_throwsCommandException() {
        Tag tag = new Tag("ExistingTag");
        model.addTag(tag);
        TagCreateCommand tagCreateCommand = new TagCreateCommand(tag);

        assertCommandFailure(tagCreateCommand, model, TagCreateCommand.MESSAGE_DUPLICATE_TAG);
    }

    @Test
    public void equals() {
        Tag physics = new Tag("Physics");
        Tag chemistry = new Tag("Chemistry");
        TagCreateCommand createPhysicsCommand = new TagCreateCommand(physics);
        TagCreateCommand createChemistryCommand = new TagCreateCommand(chemistry);

        // same object -> returns true
        assert(createPhysicsCommand.equals(createPhysicsCommand));

        // same values -> returns true
        TagCreateCommand createPhysicsCommandCopy = new TagCreateCommand(physics);
        assert(createPhysicsCommand.equals(createPhysicsCommandCopy));

        // different types -> returns false
        assert(!createPhysicsCommand.equals(1));

        // null -> returns false
        assert(!createPhysicsCommand.equals(null));

        // different tag -> returns false
        assert(!createPhysicsCommand.equals(createChemistryCommand));
    }
}

