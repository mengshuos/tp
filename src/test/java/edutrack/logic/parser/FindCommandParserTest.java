package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.parser.CommandParserTestUtil.assertParseFailure;
import static edutrack.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import edutrack.logic.commands.FindCommand;
import edutrack.model.person.GroupNameContainsKeywordsPredicate;
import edutrack.model.person.NameContainsKeywordsPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // Find by name with multiple keywords
        FindCommand expectedNameCommand =
                new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")));
        assertParseSuccess(parser, "n/Alice Bob", expectedNameCommand);
        // Find by name with whitespace variations
        assertParseSuccess(parser, "n/ \n Alice \n \t Bob  \t", expectedNameCommand);

        // Find by group with single keyword
        FindCommand expectedGroupCommand =
                new FindCommand(new GroupNameContainsKeywordsPredicate(Arrays.asList("CS2103T")));
        assertParseSuccess(parser, "g/CS2103T", expectedGroupCommand);
    }

    @Test
    public void parse_multiplePrefixes_throwsParseException() {
        // name + group
        assertParseFailure(parser, "n/Alice g/CS2103T", FindCommandParser.MESSAGE_SINGLE_PARAMETER_ONLY);

        // group + name
        assertParseFailure(parser, "g/CS2103T n/Alice", FindCommandParser.MESSAGE_SINGLE_PARAMETER_ONLY);

    }

    @Test
    public void parse_repeatedSamePrefix_throwsParseException() {
        // repeated name prefix
        assertParseFailure(parser, "n/Alice n/Bob", FindCommandParser.MESSAGE_SINGLE_PARAMETER_ONLY);

        // repeated group prefix
        assertParseFailure(parser, "g/CS2103T g/CS2101", FindCommandParser.MESSAGE_SINGLE_PARAMETER_ONLY);
    }

    @Test
    public void parse_missingKeywords_throwsParseException() {
        // missing name keywords
        assertParseFailure(parser, "n/", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));

        // missing group name
        assertParseFailure(parser, "g/", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

}
