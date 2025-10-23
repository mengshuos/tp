package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.parser.CliSyntax.PREFIX_TAG;

import edutrack.commons.core.index.Index;
import edutrack.logic.commands.TagUnassignCommand;
import edutrack.logic.parser.exceptions.ParseException;
import edutrack.model.tag.Tag;

/**
 * Parses input arguments and creates a new TagUnassignCommand object
 */
public class TagUnassignCommandParser implements Parser<TagUnassignCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the TagUnassignCommand
     * and returns a TagUnassignCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public TagUnassignCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TAG);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    TagUnassignCommand.MESSAGE_USAGE), pe);
        }

        if (!argMultimap.getValue(PREFIX_TAG).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    TagUnassignCommand.MESSAGE_USAGE));
        }

        Tag tag = ParserUtil.parseTag(argMultimap.getValue(PREFIX_TAG).get());
        return new TagUnassignCommand(index, tag);
    }
}

