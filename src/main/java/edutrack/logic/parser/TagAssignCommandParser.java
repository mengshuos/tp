package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.parser.CliSyntax.PREFIX_TAG;

import edutrack.commons.core.index.Index;
import edutrack.logic.commands.TagAssignCommand;
import edutrack.logic.parser.exceptions.ParseException;
import edutrack.model.tag.Tag;

/**
 * Parses input arguments and creates a new TagAssignCommand object
 */
public class TagAssignCommandParser implements Parser<TagAssignCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the TagAssignCommand
     * and returns a TagAssignCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public TagAssignCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TAG);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    TagAssignCommand.MESSAGE_USAGE), pe);
        }

        if (!argMultimap.getValue(PREFIX_TAG).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    TagAssignCommand.MESSAGE_USAGE));
        }

        Tag tag = ParserUtil.parseTag(argMultimap.getValue(PREFIX_TAG).get());
        return new TagAssignCommand(index, tag);
    }
}

