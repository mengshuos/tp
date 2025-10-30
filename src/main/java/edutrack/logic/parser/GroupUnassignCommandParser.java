package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.parser.CliSyntax.PREFIX_GROUP;

import java.util.ArrayList;
import java.util.List;

import edutrack.commons.core.index.Index;
import edutrack.logic.commands.GroupUnassignCommand;
import edutrack.logic.parser.exceptions.ParseException;
import edutrack.model.group.Group;

/**
 * Parses input arguments and creates a new GroupUnassignCommand object
 */
public class GroupUnassignCommandParser implements Parser<GroupUnassignCommand> {
    @Override
    public GroupUnassignCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_GROUP);

        // Verify only one group is specified
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GROUP);

        // Parse group name
        String groupName = argMultimap.getValue(PREFIX_GROUP).orElse("").trim();
        if (groupName.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    GroupUnassignCommand.MESSAGE_USAGE));
        }

        // Parse indices from preamble
        String preamble = argMultimap.getPreamble().trim();
        if (preamble.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    GroupUnassignCommand.MESSAGE_USAGE));
        }

        String[] indexStrings = preamble.split("\\s+");
        List<Index> indices = new ArrayList<>();

        try {
            for (String indexString : indexStrings) {
                indices.add(ParserUtil.parseIndex(indexString));
            }
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    GroupUnassignCommand.MESSAGE_USAGE), pe);
        }

        // Create group
        try {
            Group group = new Group(groupName);
            return new GroupUnassignCommand(group, indices);
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage());
        }
    }
}

