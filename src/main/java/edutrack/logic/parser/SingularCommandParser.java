package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_EXTRA_ARGUMENTS;
import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edutrack.commons.core.LogsCenter;
import edutrack.logic.commands.ClearCommand;
import edutrack.logic.commands.Command;
import edutrack.logic.commands.ExitCommand;
import edutrack.logic.commands.GroupListCommand;
import edutrack.logic.commands.HelpCommand;
import edutrack.logic.commands.ListCommand;
import edutrack.logic.commands.StatsCommand;
import edutrack.logic.commands.TagListCommand;
import edutrack.logic.parser.exceptions.ParseException;

public class SingularCommandParser implements Parser<Command> {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private static final Logger logger = LogsCenter.getLogger(AddressBookParser.class);

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parse(String userInput) throws ParseException {

        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        // Note to developers: Change the log level in config.json to enable lower level (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        if (!commandWord.equals(ClearCommand.COMMAND_WORD) && hasExtraArguments(arguments)) {
            throw new ParseException(String.format(MESSAGE_EXTRA_ARGUMENTS, HelpCommand.MESSAGE_USAGE));
        }

        switch (commandWord) {

        case ClearCommand.COMMAND_WORD:
            String args = arguments.trim();
            if ("confirm".equals(args)) {
                return new ClearCommand(true);
            } else if (hasExtraArguments(args)) {
                logger.finer("This user input's arguments caused a ParseException: " + args);
                throw new ParseException(MESSAGE_EXTRA_ARGUMENTS);
            }
            return new ClearCommand();

        case ListCommand.COMMAND_WORD:
            return new ListCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        case GroupListCommand.COMMAND_WORD:
            return new GroupListCommand();

        case TagListCommand.COMMAND_WORD:
            return new TagListCommand();

        case StatsCommand.COMMAND_WORD:
            return new StatsCommand();

        default:
            logger.finer("This user input caused a ParseException: " + userInput);
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    public boolean hasExtraArguments(String args) {
        return !args.trim().isEmpty();
    }

}
