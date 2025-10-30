package edutrack.logic.parser;

import static edutrack.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static edutrack.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edutrack.commons.core.LogsCenter;
import edutrack.logic.commands.AddCommand;
import edutrack.logic.commands.ClearCommand;
import edutrack.logic.commands.Command;
import edutrack.logic.commands.DeleteCommand;
import edutrack.logic.commands.EditCommand;
import edutrack.logic.commands.ExitCommand;
import edutrack.logic.commands.FindCommand;
import edutrack.logic.commands.FindTagCommand;
import edutrack.logic.commands.GroupAssignCommand;
import edutrack.logic.commands.GroupCreateCommand;
import edutrack.logic.commands.GroupDeleteCommand;
import edutrack.logic.commands.GroupListCommand;
import edutrack.logic.commands.GroupUnassignCommand;
import edutrack.logic.commands.HelpCommand;
import edutrack.logic.commands.ListCommand;
import edutrack.logic.commands.NoteCreateCommand;
import edutrack.logic.commands.SortCommand;
import edutrack.logic.commands.StatsCommand;
import edutrack.logic.commands.TagAssignCommand;
import edutrack.logic.commands.TagCreateCommand;
import edutrack.logic.commands.TagDeleteCommand;
import edutrack.logic.commands.TagListCommand;
import edutrack.logic.commands.TagUnassignCommand;
import edutrack.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

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
    public Command parseCommand(String userInput) throws ParseException {

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

        switch (commandWord) {

        case AddCommand.COMMAND_WORD:
            return new AddCommandParser().parse(arguments);

        case EditCommand.COMMAND_WORD:
            return new EditCommandParser().parse(arguments);

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser().parse(arguments);

        case FindCommand.COMMAND_WORD:
            return new FindCommandParser().parse(arguments);

        case GroupCreateCommand.COMMAND_WORD:
            return new GroupCreateCommandParser().parse(arguments);

        case GroupDeleteCommand.COMMAND_WORD:
            return new GroupDeleteCommandParser().parse(arguments);

        case GroupAssignCommand.COMMAND_WORD:
            return new GroupAssignCommandParser().parse(arguments);

        case GroupUnassignCommand.COMMAND_WORD:
            return new GroupUnassignCommandParser().parse(arguments);

        case TagCreateCommand.COMMAND_WORD:
            return new TagCreateCommandParser().parse(arguments);

        case TagDeleteCommand.COMMAND_WORD:
            return new TagDeleteCommandParser().parse(arguments);

        case TagAssignCommand.COMMAND_WORD:
            return new TagAssignCommandParser().parse(arguments);

        case TagUnassignCommand.COMMAND_WORD:
            return new TagUnassignCommandParser().parse(arguments);

        case FindTagCommand.COMMAND_WORD:
            return new FindTagCommandParser().parse(arguments);

        case SortCommand.COMMAND_WORD:
            return new SortCommandParser().parse(arguments);

        case NoteCreateCommand.COMMAND_WORD:
            return new NoteCreateCommandParser().parse(arguments);

        case ClearCommand.COMMAND_WORD:
        case ListCommand.COMMAND_WORD:
        case ExitCommand.COMMAND_WORD:
        case HelpCommand.COMMAND_WORD:
        case GroupListCommand.COMMAND_WORD:
        case TagListCommand.COMMAND_WORD:
        case StatsCommand.COMMAND_WORD:
            return new SingularCommandParser().parse(userInput);

        default:
            logger.finer("This user input caused a ParseException: " + userInput);
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }


}
