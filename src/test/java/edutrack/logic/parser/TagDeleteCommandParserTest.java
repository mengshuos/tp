package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.parser.CommandParserTestUtil.assertParseFailure;
import static edutrack.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import edutrack.logic.commands.TagDeleteCommand;
import edutrack.model.tag.Tag;

public class TagDeleteCommandParserTest {

    private TagDeleteCommandParser parser = new TagDeleteCommandParser();

    @Test
    public void parse_validArgs_returnsTagDeleteCommand() {
        Tag expectedTag = new Tag("Physics");
        assertParseSuccess(parser, " t/Physics", new TagDeleteCommand(expectedTag));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // no tag prefix
        assertParseFailure(parser, "Physics",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagDeleteCommand.MESSAGE_USAGE));

        // empty tag name
        assertParseFailure(parser, " t/", Tag.MESSAGE_CONSTRAINTS);

        // invalid tag name with special characters
        assertParseFailure(parser, " t/!invalid", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_missingTagPrefix_throwsParseException() {
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagDeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_preamblePresent_throwsParseException() {
        assertParseFailure(parser, "some preamble t/Physics",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagDeleteCommand.MESSAGE_USAGE));
    }
}

