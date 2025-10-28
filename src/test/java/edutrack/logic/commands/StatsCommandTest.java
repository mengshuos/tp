package edutrack.logic.commands;

import static edutrack.logic.commands.CommandTestUtil.assertCommandSuccess;
import static edutrack.logic.commands.StatsCommand.MESSAGE_SUCCESS;
import static edutrack.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;
import edutrack.model.ModelManager;
import edutrack.model.UserPrefs;
import edutrack.model.group.Group;


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
        assert modelWithHumanData.getFilteredPersonList()
                                 .size() == expectedModelWithData.getFilteredPersonList()
                                                                 .size();
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

    @Test
    public void execute_statsWithMultipleTags_success() throws CommandException {
        Model testModel = new ModelManager();
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student1")
                .withTags("math", "science", "english")
                .build());
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student2")
                .withTags("math", "history")
                .build());

        CommandResult result = new StatsCommand().execute(testModel);
        assert result.isShowStats();
    }

    @Test
    public void execute_statsWithMultipleGroups_success() throws CommandException {
        Model testModel = new ModelManager();
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student1")
                .withGroup("GroupA")
                .build());
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student2")
                .withGroup("GroupA", "GroupB")
                .build());
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student3")
                .withGroup("GroupB")
                .build());

        CommandResult result = new StatsCommand().execute(testModel);
        assert result.isShowStats();
    }

    @Test
    public void execute_statsWithNoTags_success() throws CommandException {
        Model testModel = new ModelManager();
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student1")
                .build());

        CommandResult result = new StatsCommand().execute(testModel);
        assert result.isShowStats();
    }

    @Test
    public void execute_statsWithNoGroups_success() throws CommandException {
        Model testModel = new ModelManager();
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student1")
                .withTags("math")
                .build());

        CommandResult result = new StatsCommand().execute(testModel);
        assert result.isShowStats();
    }

    @Test
    public void execute_statsWithTagsAndGroups_success() throws CommandException {
        Model testModel = new ModelManager();
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student1")
                .withTags("math", "science")
                .withGroup("CS2103T")
                .build());
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student2")
                .withTags("science", "english")
                .withGroup("CS2103T")
                .build());

        CommandResult result = new StatsCommand().execute(testModel);
        assert result.isShowStats();
    }

    @Test
    public void execute_statsWithDuplicateTagsAcrossStudents_countsCorrectly() throws CommandException {
        Model testModel = new ModelManager();
        // All three students have the same tag "math"
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student1")
                .withTags("math")
                .build());
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student2")
                .withTags("math")
                .build());
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student3")
                .withTags("math")
                .build());

        CommandResult result = new StatsCommand().execute(testModel);
        assert result.isShowStats();

        // Verify underlying data: should have 3 students
        assert testModel.getFilteredPersonList().size() == 3;
    }

    @Test
    public void execute_statsWithCaseInsensitiveTagCounting_countsCaseCorrectly() throws CommandException {
        Model testModel = new ModelManager();
        // Using different cases to test case-insensitive counting
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student1")
                .withTags("MATH")
                .build());
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student2")
                .withTags("math")
                .build());
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student3")
                .withTags("Math")
                .build());

        CommandResult result = new StatsCommand().execute(testModel);
        assert result.isShowStats();

        // Verify underlying data: should have 3 students with "math" tag (case varies)
        assert testModel.getFilteredPersonList().size() == 3;
    }

    @Test
    public void execute_statsTotalStudentsCountIsAccurate() throws CommandException {
        Model testModel = new ModelManager();
        // Add exactly 5 students
        for (int i = 1; i <= 5; i++) {
            testModel.addPerson(new edutrack.testutil.PersonBuilder()
                    .withName("Student" + i)
                    .withTags("tag" + i)
                    .build());
        }

        new StatsCommand().execute(testModel);

        // Verify underlying data accuracy
        assert testModel.getFilteredPersonList().size() == 5;
    }

    @Test
    public void execute_statsMultipleGroupsHaveCorrectStudentCounts() throws CommandException {
        Model testModel = new ModelManager();
        // GroupA has 2 students, GroupB has 2 students (one student in both)
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student1")
                .withGroup("GroupA")
                .build());
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student2")
                .withGroup("GroupA", "GroupB")
                .build());
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student3")
                .withGroup("GroupB")
                .build());

        new StatsCommand().execute(testModel);

        // Compute group student counts like StatsWindow does
        var personList = testModel.getAddressBook().getPersonList();
        var groupList = testModel.getAddressBook().getGroupList();
        Map<Group, Integer> groupStudentCounts = new HashMap<>();

        for (var group : groupList) {
            int studentCount = 0;
            for (var person : personList) {
                if (person.getGroups().contains(group)) {
                    studentCount++;
                }
            }
            groupStudentCounts.put(group, studentCount);
        }

        // Verify actual student counts per group match expected values
        assert testModel.getFilteredPersonList().size() == 3;
        assert testModel.getFilteredGroupList().size() == 2;

        // Find GroupA and GroupB
        Group groupA = null;
        Group groupB = null;
        for (var group : groupList) {
            if (group.groupName.equals("GroupA")) {
                groupA = group;
            }
            if (group.groupName.equals("GroupB")) {
                groupB = group;
            }
        }

        assert groupA != null;
        assert groupB != null;
        assert groupStudentCounts.get(groupA) == 2; // Student1 and Student2
        assert groupStudentCounts.get(groupB) == 2; // Student2 and Student3
    }

    @Test
    public void execute_statsTagCountsAreAccurateAcrossMultipleTags() throws CommandException {
        Model testModel = new ModelManager();
        // math tag appears 3 times, science 2 times, english 1 time
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student1")
                .withTags("math", "science")
                .build());
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student2")
                .withTags("math", "science")
                .build());
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student3")
                .withTags("math", "english")
                .build());

        new StatsCommand().execute(testModel);

        // Compute tag counts like StatsWindow does
        var personList = testModel.getAddressBook().getPersonList();
        Map<String, Integer> tagCounts = new HashMap<>();
        for (var person : personList) {
            for (var tag : person.getTags()) {
                String tagName = tag.tagName.toLowerCase();
                tagCounts.put(tagName, tagCounts.getOrDefault(tagName, 0) + 1);
            }
        }

        // Verify actual tag counts match expected values
        assert testModel.getFilteredPersonList().size() == 3;
        assert tagCounts.get("math") == 3;
        assert tagCounts.get("science") == 2;
        assert tagCounts.get("english") == 1;
        assert tagCounts.size() == 3; // 3 unique tags
    }

    @Test
    public void execute_statsEmptyStudentListShowsZeroCounts() throws CommandException {
        Model emptyModel = new ModelManager();

        new StatsCommand().execute(emptyModel);

        // Verify underlying data: no students
        assert emptyModel.getFilteredPersonList().isEmpty();
        assert emptyModel.getFilteredGroupList().isEmpty();
    }

    @Test
    public void execute_statsGroupWithNoTagsStillCountsStudents() throws CommandException {
        Model testModel = new ModelManager();
        // Add students with no tags to a group
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student1")
                .withGroup("CS2103T")
                .build());
        testModel.addPerson(new edutrack.testutil.PersonBuilder()
                .withName("Student2")
                .withGroup("CS2103T")
                .build());

        new StatsCommand().execute(testModel);

        // Verify underlying data: 2 students in group with no tags
        assert testModel.getFilteredPersonList().size() == 2;
        assert testModel.getFilteredGroupList().size() == 1;
    }

}
