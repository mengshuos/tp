package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.parser.CliSyntax.PREFIX_NOTE;
import static java.util.Objects.requireNonNull;

import edutrack.commons.core.index.Index;
import edutrack.logic.commands.NoteCreateCommand;
import edutrack.logic.parser.exceptions.ParseException;
import edutrack.model.person.Note;

/**
 * Parses input arguments and creates a newNoteCreateCommand object
 */

public class NoteCreateCommandParser implements Parser<NoteCreateCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the NoteCreateCommand
     * and returns a NoteCreateCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public NoteCreateCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap map = ArgumentTokenizer.tokenize(args, PREFIX_NOTE);

        Index index;

        try {
            index = ParserUtil.parseIndex(map.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    NoteCreateCommand.MESSAGE_USAGE), pe);
        }

        Note note = ParserUtil.parseNote(map.getValue(PREFIX_NOTE).orElse(""));

        if (note.value.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    NoteCreateCommand.MESSAGE_USAGE));
        }

        return new NoteCreateCommand(index, note);
    }
}
