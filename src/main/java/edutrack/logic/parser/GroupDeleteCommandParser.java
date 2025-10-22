package edutrack.logic.parser;

import static edutrack.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.parser.CliSyntax.PREFIX_GROUP;

import edutrack.logic.commands.GroupDeleteCommand;
import edutrack.logic.parser.exceptions.ParseException;
import edutrack.model.group.Group;

/**
 * Parses input arguments and creates a new GroupDeleteCommand object
 */
public class GroupDeleteCommandParser implements Parser<GroupDeleteCommand> {

    @Override
    public GroupDeleteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_GROUP);

        if (!argMultimap.getValue(PREFIX_GROUP).isPresent() || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, GroupDeleteCommand.MESSAGE_USAGE));
        }

        try {
            Group group = ParserUtil.parseGroup(argMultimap.getValue(PREFIX_GROUP).get());
            return new GroupDeleteCommand(group);
        } catch (ParseException e) {
            final String error = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    GroupDeleteCommand.MESSAGE_USAGE);
            throw new ParseException(error, e);
        }
    }
}
