package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.parser.CommandParserTestUtil.assertParseFailure;
import static edutrack.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import edutrack.logic.commands.FindTagCommand;
import edutrack.model.tag.Tag;

public class FindTagCommandParserTest {

    private FindTagCommandParser parser = new FindTagCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindTagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingPrefix_throwsParseException() {
        // no prefix provided
        assertParseFailure(parser, "friends",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindTagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidTagName_throwsParseException() {
        // invalid tag with special characters
        assertParseFailure(parser, " t/friend*",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindTagCommand.MESSAGE_USAGE));

        // invalid tag with spaces
        assertParseFailure(parser, " t/needs help",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindTagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_preamblePresent_throwsParseException() {
        // preamble before the prefix
        assertParseFailure(parser, "some text t/friends",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindTagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_multipleTags_throwsParseException() {
        // multiple tags provided - should reject
        assertParseFailure(parser, " t/friends t/colleagues",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindTagCommand.MESSAGE_USAGE));

        // multiple tags with different values
        assertParseFailure(parser, " t/needs_help t/high-priority",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindTagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindTagCommand() throws Exception {
        // valid tag without whitespace
        Tag expectedTag = new Tag("friends");
        FindTagCommand expectedCommand = new FindTagCommand(expectedTag);
        assertParseSuccess(parser, " t/friends", expectedCommand);

        // valid tag with leading/trailing whitespaces
        assertParseSuccess(parser, " t/  friends  ", expectedCommand);
    }

    @Test
    public void parse_validTagWithUnderscore_returnsFindTagCommand() throws Exception {
        // valid tag with underscore
        Tag expectedTag = new Tag("needs_help");
        FindTagCommand expectedCommand = new FindTagCommand(expectedTag);
        assertParseSuccess(parser, " t/needs_help", expectedCommand);
    }

    @Test
    public void parse_validTagWithHyphen_returnsFindTagCommand() throws Exception {
        // valid tag with hyphen
        Tag expectedTag = new Tag("high-priority");
        FindTagCommand expectedCommand = new FindTagCommand(expectedTag);
        assertParseSuccess(parser, " t/high-priority", expectedCommand);
    }

    @Test
    public void parse_validTagWithSlash_returnsFindTagCommand() throws Exception {
        // valid tag with slash
        Tag expectedTag = new Tag("CS2103T/W13");
        FindTagCommand expectedCommand = new FindTagCommand(expectedTag);
        assertParseSuccess(parser, " t/CS2103T/W13", expectedCommand);
    }

    @Test
    public void parse_caseInsensitiveTag_returnsFindTagCommand() throws Exception {
        // tags are case-insensitive
        Tag expectedTag = new Tag("Friends");
        FindTagCommand expectedCommand = new FindTagCommand(expectedTag);
        assertParseSuccess(parser, " t/Friends", expectedCommand);
    }
}
