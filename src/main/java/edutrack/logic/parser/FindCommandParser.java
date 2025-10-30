package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.parser.CliSyntax.PREFIX_GROUP;
import static edutrack.logic.parser.CliSyntax.PREFIX_NAME;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;

import edutrack.logic.commands.FindCommand;
import edutrack.logic.parser.exceptions.ParseException;
import edutrack.model.person.GroupNameContainsKeywordsPredicate;
import edutrack.model.person.NameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    static final String MESSAGE_SINGLE_PARAMETER_ONLY =
            "Only one parameter block is allowed. Use either n/… or g/… (not both, not repeated).\n\n"
            + FindCommand.MESSAGE_USAGE;

    @Override
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);

        // Ensure a leading space so ArgumentTokenizer recognises the first prefix
        final String paddedArgs = args.startsWith(" ") ? args : " " + args;

        final ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(paddedArgs, PREFIX_NAME, PREFIX_GROUP);

        final String preamble = argMultimap.getPreamble().trim();
        if (paddedArgs.trim().isEmpty() || !preamble.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        final var names = argMultimap.getAllValues(PREFIX_NAME);
        final var groups = argMultimap.getAllValues(PREFIX_GROUP);
        final boolean hasName = !names.isEmpty();
        final boolean hasGroup = !groups.isEmpty();

        if (hasName && hasGroup) {
            throw new ParseException(MESSAGE_SINGLE_PARAMETER_ONLY);
        }
        if (!hasName && !hasGroup) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        if (names.size() > 1 || groups.size() > 1) {
            throw new ParseException(MESSAGE_SINGLE_PARAMETER_ONLY);
        }

        if (hasName) {
            String keywords = names.get(0).trim();
            if (keywords.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
            }
            return new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList(keywords.split("\\s+"))));
        }

        String groupKeywords = groups.get(0).trim();
        if (groupKeywords.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        return new FindCommand(new GroupNameContainsKeywordsPredicate(Arrays.asList(groupKeywords.split("\\s+"))));
    }

}
