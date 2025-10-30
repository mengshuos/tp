package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import edutrack.logic.commands.ClearCommand;
import edutrack.logic.commands.ExitCommand;
import edutrack.logic.commands.GroupListCommand;
import edutrack.logic.commands.HelpCommand;
import edutrack.logic.commands.ListCommand;
import edutrack.logic.commands.StatsCommand;
import edutrack.logic.commands.TagListCommand;
import edutrack.logic.parser.exceptions.ParseException;

public class SingularCommandParserTest {

    private final SingularCommandParser parser = new SingularCommandParser();

    @Test
    public void parse_clear_success() throws Exception {
        assertTrue(parser.parse(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
    }

    @Test
    public void parse_clearExtraArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(ClearCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parse_exit_success() throws Exception {
        assertTrue(parser.parse(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
    }

    @Test
    public void parse_exitExtraArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(ExitCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parse_help_success() throws Exception {
        assertTrue(parser.parse(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
    }

    @Test
    public void parse_helpExtraArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(HelpCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parse_list_success() throws Exception {
        assertTrue(parser.parse(ListCommand.COMMAND_WORD) instanceof ListCommand);
    }

    @Test
    public void parse_listExtraArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(ListCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parse_groupList_success() throws Exception {
        assertTrue(parser.parse(GroupListCommand.COMMAND_WORD) instanceof GroupListCommand);
    }

    @Test
    public void parse_groupListExtraArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(GroupListCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parse_tagList_success() throws Exception {
        assertTrue(parser.parse(TagListCommand.COMMAND_WORD) instanceof TagListCommand);
    }

    @Test
    public void parse_tagListExtraArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(TagListCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parse_stats_success() throws Exception {
        assertTrue(parser.parse(StatsCommand.COMMAND_WORD) instanceof StatsCommand);
    }

    @Test
    public void parse_statsExtraArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(StatsCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parse_emptyInput_throwsParseExceptionWithUsageMessage() {
        ParseException e = assertThrows(ParseException.class, () -> parser.parse(""));
        assertEquals(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), e.getMessage());
    }

    @Test
    public void parse_unknownCommand_throwsUnknownCommandMessage() {
        ParseException e = assertThrows(ParseException.class, () -> parser.parse("unknownCommand"));
        assertEquals(MESSAGE_UNKNOWN_COMMAND, e.getMessage());
    }
}
