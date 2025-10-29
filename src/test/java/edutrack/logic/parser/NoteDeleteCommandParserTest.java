package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.parser.CommandParserTestUtil.assertParseFailure;
import static edutrack.logic.parser.CommandParserTestUtil.assertParseSuccess;

import edutrack.commons.core.index.Index;
import edutrack.logic.commands.NoteDeleteCommand;
import org.junit.jupiter.api.Test;


public class NoteDeleteCommandParserTest {

    private NoteDeleteCommandParser parser = new NoteDeleteCommandParser();

    @Test
    public void parse_validArgs_returnsNoteDeleteCommand() {
        // Valid index
        assertParseSuccess(parser, "1", new NoteDeleteCommand(Index.fromOneBased(1)));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // Missing index
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteDeleteCommand.MESSAGE_USAGE));

        // Invalid index
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteDeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_negativeIndex_throwsParseException() {
        // Negative index
        assertParseFailure(parser, "-1", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteDeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_zeroIndex_throwsParseException() {
        // Zero index
        assertParseFailure(parser, "0", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteDeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_extraneousArguments_throwsParseException() {
        // Extraneous arguments
        assertParseFailure(parser, "1 extra", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteDeleteCommand.MESSAGE_USAGE));

        // Extraneous arguments but it is another valid index
        assertParseFailure(parser, "1 2", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteDeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_leadingAndTrailingSpaces_returnsNoteDeleteCommand() {
        // Valid index with leading and trailing spaces
        assertParseSuccess(parser, "   2   ", new NoteDeleteCommand(Index.fromOneBased(2)));
    }

    @Test
    public void parse_largeIndex_returnsNoteDeleteCommand() {
        // Valid large index
        assertParseSuccess(parser, "1000", new NoteDeleteCommand(Index.fromOneBased(1000)));
    }
}
