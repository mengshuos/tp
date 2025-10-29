package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.parser.CliSyntax.PREFIX_NOTE;
import static java.util.Objects.requireNonNull;

import edutrack.commons.core.index.Index;
import edutrack.logic.commands.NoteDeleteCommand;
import edutrack.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new NoteCreateCommand object
 */

public class NoteDeleteCommandParser implements Parser<NoteDeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the NoteCreateCommand
     * and returns a NoteCreateCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public NoteDeleteCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap map = ArgumentTokenizer.tokenize(args, PREFIX_NOTE);

        Index index;

        try {
            index = ParserUtil.parseIndex(map.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    NoteDeleteCommand.MESSAGE_USAGE), pe);
        }

        return new NoteDeleteCommand(index);
    }
}
