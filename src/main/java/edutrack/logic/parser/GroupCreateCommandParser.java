package edutrack.logic.parser;

import static edutrack.logic.parser.CliSyntax.PREFIX_GROUP;

import edutrack.logic.commands.GroupCreateCommand;
import edutrack.logic.parser.exceptions.ParseException;
import edutrack.model.group.Group;

/**
 * Parses input arguments and creates a new GroupCreateCommand object
 */

public class GroupCreateCommandParser implements Parser<GroupCreateCommand> {
    @Override
    public GroupCreateCommand parse(String args) throws ParseException {
        ArgumentMultimap map = ArgumentTokenizer.tokenize(args, PREFIX_GROUP);
        String raw = map.getValue(PREFIX_GROUP).orElse("").trim();
        if (raw.isEmpty()) {
            throw new ParseException("You have to input a group name!");
        }
        try {
            Group g = new Group(raw);
            return new GroupCreateCommand(g);
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage());
        }
    }
}


