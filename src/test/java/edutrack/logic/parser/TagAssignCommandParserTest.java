package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.parser.CommandParserTestUtil.assertParseFailure;
import static edutrack.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import edutrack.commons.core.index.Index;
import edutrack.logic.commands.TagAssignCommand;
import edutrack.model.tag.Tag;

public class TagAssignCommandParserTest {

    private TagAssignCommandParser parser = new TagAssignCommandParser();

    @Test
    public void parse_validArgs_returnsTagAssignCommand() {
        Tag expectedTag = new Tag("Physics");
        Index expectedIndex = Index.fromOneBased(1);
        assertParseSuccess(parser, " 1 t/Physics", new TagAssignCommand(expectedIndex, expectedTag));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        // negative index
        assertParseFailure(parser, " -1 t/Physics",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagAssignCommand.MESSAGE_USAGE));

        // zero index
        assertParseFailure(parser, " 0 t/Physics",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagAssignCommand.MESSAGE_USAGE));

        // non-numeric index
        assertParseFailure(parser, " abc t/Physics",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagAssignCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingIndex_throwsParseException() {
        assertParseFailure(parser, " t/Physics",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagAssignCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingTagPrefix_throwsParseException() {
        assertParseFailure(parser, " 1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagAssignCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidTagName_throwsParseException() {
        assertParseFailure(parser, " 1 t/has space", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 t/!invalid", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_emptyTagName_throwsParseException() {
        assertParseFailure(parser, " 1 t/", Tag.MESSAGE_CONSTRAINTS);
    }
}

