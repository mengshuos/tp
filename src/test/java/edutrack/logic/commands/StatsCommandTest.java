package edutrack.logic.commands;

import static edutrack.logic.commands.CommandTestUtil.assertCommandSuccess;
import static edutrack.logic.commands.StatsCommand.MESSAGE_SUCCESS;
import static edutrack.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;
import edutrack.model.ModelManager;
import edutrack.model.UserPrefs;


public class StatsCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_stats_success() {
        CommandResult expectedCommandResult = new CommandResult(MESSAGE_SUCCESS, false, false, true);
        assertCommandSuccess(new StatsCommand(), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_statsWithEmptyAddressBook_success() {
        Model emptyModel = new ModelManager();
        Model expectedEmptyModel = new ModelManager();
        CommandResult expectedCommandResult = new CommandResult(MESSAGE_SUCCESS, false, false, true);
        assertCommandSuccess(new StatsCommand(), emptyModel, expectedCommandResult, expectedEmptyModel);
    }

    @Test
    public void execute_statsWithNonEmptyAddressBook_success() {
        Model modelWithData = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModelWithData = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        CommandResult expectedCommandResult = new CommandResult(MESSAGE_SUCCESS, false, false, true);
        assertCommandSuccess(new StatsCommand(), modelWithData, expectedCommandResult, expectedModelWithData);
    }

    @Test
    public void execute_statsDoesNotModifyModel() throws CommandException {
        Model modelWithHumanData = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModelWithData = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        new StatsCommand().execute(modelWithHumanData);

        // Verify the model state remains unchanged
        assert modelWithHumanData.getFilteredPersonList().size() == expectedModelWithData.getFilteredPersonList().size();
        assert modelWithHumanData.getAddressBook().equals(expectedModelWithData.getAddressBook());
    }

    @Test
    public void execute_commandResultFlagsSetCorrectly() throws CommandException {
        CommandResult result = new StatsCommand().execute(model);

        assert !result.isShowHelp();
        assert !result.isExit();
        assert result.isShowStats();
    }

    @Test
    public void execute_statsTwice_success() throws CommandException {
        Model modelWithData = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        // Execute stats twice
        new StatsCommand().execute(modelWithData);
        CommandResult secondResult = new StatsCommand().execute(modelWithData);

        assert secondResult.getFeedbackToUser().equals(MESSAGE_SUCCESS);
        assert secondResult.isShowStats();
    }

    @Test
    public void execute_statsCorrectMessage() throws CommandException {
        CommandResult result = new StatsCommand().execute(model);
        assert result.getFeedbackToUser().equals(MESSAGE_SUCCESS);
    }

    @Test
    public void execute_statsWithSinglePerson_success() throws CommandException {
        Model singlePersonModel = new ModelManager();
        singlePersonModel.addPerson(
            new edutrack.testutil.PersonBuilder().withName("Test Person").build());

        CommandResult result = new StatsCommand().execute(singlePersonModel);
        assert result.isShowStats();
        assert result.getFeedbackToUser().equals(MESSAGE_SUCCESS);
    }

    @Test
    public void execute_statsWithFilteredModel_success() throws CommandException {
        Model filteredModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        filteredModel.updateFilteredPersonList(person -> false); // Filter out all persons

        CommandResult result = new StatsCommand().execute(filteredModel);
        assert result.isShowStats();
        assert result.getFeedbackToUser().equals(MESSAGE_SUCCESS);
    }

    @Test
    public void execute_statsMultipleConsecutiveTimes_success() throws CommandException {
        Model modelWithData = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        // Execute stats multiple times in a row
        for (int i = 0; i < 5; i++) {
            CommandResult result = new StatsCommand().execute(modelWithData);
            assert result.isShowStats();
            assert result.getFeedbackToUser().equals(MESSAGE_SUCCESS);
        }
    }

    @Test
    public void execute_statsAfterAddingPerson_success() throws CommandException {
        Model testModel = new ModelManager();

        // First stats call with empty model
        CommandResult result1 = new StatsCommand().execute(testModel);
        assert result1.isShowStats();

        // Add a person
        testModel.addPerson(new edutrack.testutil.PersonBuilder().withName("Added Person").build());

        // Second stats call with one person
        CommandResult result2 = new StatsCommand().execute(testModel);
        assert result2.isShowStats();
        assert result2.getFeedbackToUser().equals(MESSAGE_SUCCESS);
    }

    @Test
    public void execute_statsAfterDeletingPerson_success() throws CommandException {
        Model testModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        // First stats call with all persons
        CommandResult result1 = new StatsCommand().execute(testModel);
        assert result1.isShowStats();

        // Delete a person
        testModel.deletePerson(testModel.getFilteredPersonList().get(0));

        // Second stats call after deletion
        CommandResult result2 = new StatsCommand().execute(testModel);
        assert result2.isShowStats();
        assert result2.getFeedbackToUser().equals(MESSAGE_SUCCESS);
    }

    @Test
    public void execute_statsCommandResultImmutable() throws CommandException {
        Model testModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        CommandResult result1 = new StatsCommand().execute(testModel);
        CommandResult result2 = new StatsCommand().execute(testModel);

        // Verify both results have same properties
        assert result1.getFeedbackToUser().equals(result2.getFeedbackToUser());
        assert result1.isShowStats() == result2.isShowStats();
        assert result1.isShowHelp() == result2.isShowHelp();
        assert result1.isExit() == result2.isExit();
    }

    @Test
    public void execute_statsNoExceptionThrown() {
        try {
            new StatsCommand().execute(model);
        } catch (CommandException e) {
            assert false : "Stats command should not throw CommandException";
        }
    }

}
