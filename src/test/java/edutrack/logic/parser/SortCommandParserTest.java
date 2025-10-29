package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.parser.CommandParserTestUtil.assertParseFailure;
import static edutrack.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import edutrack.logic.commands.SortCommand;

public class SortCommandParserTest {

    private SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_emptyArg_returnsValidSortCommand() {
        SortCommand expectedCommand = new SortCommand(false);
        assertParseSuccess(parser, "", expectedCommand);
    }

    @Test
    public void parse_whitespaceArg_returnsValidSortCommand() {
        // Whitespace should be trimmed and treated as empty argument
        SortCommand expectedCommand = new SortCommand(false);
        assertParseSuccess(parser, "   ", expectedCommand);
    }

    @Test
    public void parse_nonEmptyArg_throwsParseException() {
        // Any non-empty argument should be rejected with an error message
        assertParseFailure(parser, "abc", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
            SortCommand.MESSAGE_INVALID_PARAMETERS));
    }
}
