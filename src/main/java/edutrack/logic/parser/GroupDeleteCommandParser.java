package edutrack.logic.parser;

import static edutrack.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.parser.CliSyntax.PREFIX_GROUP;

import java.util.Objects;
import java.util.Optional;

import edutrack.logic.commands.GroupDeleteCommand;
import edutrack.logic.parser.exceptions.ParseException;
import edutrack.model.group.Group;

/** Parses input arguments and creates a new GroupDeleteCommand object */
public class GroupDeleteCommandParser implements Parser<GroupDeleteCommand> {

    @Override
    public GroupDeleteCommand parse(String args) throws ParseException {
        Objects.requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_GROUP);

        Optional<String> tokenValue = argMultimap.getValue(PREFIX_GROUP);
        boolean usedFallback = false;
        String groupArg = tokenValue.orElse("").trim();

        if (groupArg.isEmpty()) {
            String trimmed = args.trim();
            String prefix = PREFIX_GROUP.getPrefix();
            if (trimmed.startsWith(prefix)) {
                groupArg = trimmed.substring(prefix.length()).trim();
                usedFallback = true;
            }
        }

        // if no group found or (we relied on tokenizer and there's a non-empty preamble) -> invalid
        if (groupArg.isEmpty() || (!usedFallback && !argMultimap.getPreamble().trim().isEmpty())) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    GroupDeleteCommand.MESSAGE_USAGE));
        }

        Group group = ParserUtil.parseGroup(groupArg);
        return new GroupDeleteCommand(group);
    }
}
