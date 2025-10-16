package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;

import edutrack.logic.commands.FindCommand;
import edutrack.logic.parser.exceptions.ParseException;
import edutrack.model.person.GroupNameContainsKeywordsPredicate;
import edutrack.model.person.NameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_NAME_USAGE));
        }

        // Determine if searching by group or by name
        if (trimmedArgs.startsWith("g/")) {
            String groupName = trimmedArgs.substring(2).trim();
            if (groupName.isEmpty()) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_GROUP_USAGE));
            }
            return new FindCommand(new GroupNameContainsKeywordsPredicate(
                    Arrays.asList(groupName.split("\\s+"))));

        } else if (trimmedArgs.startsWith("n/")) {
            String name = trimmedArgs.substring(2).trim();
            if (name.isEmpty()) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_GROUP_USAGE));
            }
            String[] nameKeywords = name.split("\\s+");
            return new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList(nameKeywords)));

        } else {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }
}
