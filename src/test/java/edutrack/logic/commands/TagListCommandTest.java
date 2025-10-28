package edutrack.logic.commands;

import static edutrack.logic.commands.CommandTestUtil.assertCommandSuccess;
import static edutrack.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edutrack.model.Model;
import edutrack.model.ModelManager;
import edutrack.model.UserPrefs;
import edutrack.model.tag.Tag;

/**
 * Contains integration tests (interaction with the Model) for {@code TagListCommand}.
 */
public class TagListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        String expectedMessage = model.getFilteredTagList().isEmpty()
                ? "No tags found. Create tags using: tag/create t/TAG_NAME"
                : TagListCommand.MESSAGE_SUCCESS + ":\n" + getTagsAsString(model);
        assertCommandSuccess(new TagListCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        // Add some tags first
        model.addTag(new Tag("Physics"));
        model.addTag(new Tag("Chemistry"));
        expectedModel.addTag(new Tag("Physics"));
        expectedModel.addTag(new Tag("Chemistry"));

        String expectedMessage = TagListCommand.MESSAGE_SUCCESS + ":\n" + getTagsAsString(expectedModel);
        assertCommandSuccess(new TagListCommand(), model, expectedMessage, expectedModel);
    }

    private String getTagsAsString(Model model) {
        return model.getFilteredTagList().stream()
                .map(tag -> tag.tagName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }
}

