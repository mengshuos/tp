package edutrack.logic.parser;

import edutrack.logic.commands.GroupListCommand;
import edutrack.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new GroupListCommand object
 */
public class GroupListCommandParser implements Parser<GroupListCommand> {
    @Override
    public GroupListCommand parse(String args) throws ParseException {
        if (!args.trim().isEmpty()) {
            throw new ParseException(edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT);
        }
        return new GroupListCommand();
    }
}


