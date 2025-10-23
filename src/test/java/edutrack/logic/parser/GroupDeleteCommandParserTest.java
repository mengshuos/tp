package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.parser.CommandParserTestUtil.assertParseFailure;
import static edutrack.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import edutrack.logic.commands.GroupDeleteCommand;
import edutrack.model.group.Group;

public class GroupDeleteCommandParserTest {

    private GroupDeleteCommandParser parser = new GroupDeleteCommandParser();

    @Test
    public void parse_validArgs_returnsGroupDeleteCommand() {
        assertParseSuccess(parser, "g/friends", new GroupDeleteCommand(new Group("friends")));
    }

    @Test
    public void parse_missingPrefix_throwsParseException() {
        assertParseFailure(parser, "friends",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, GroupDeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidGroupName_throwsParseException() {
        assertParseFailure(parser, "g/!invalid", Group.MESSAGE_CONSTRAINTS);
    }
}
