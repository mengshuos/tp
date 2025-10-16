package edutrack.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import edutrack.commons.util.ToStringBuilder;
import edutrack.logic.Messages;
import edutrack.model.Model;
import edutrack.model.person.Person;

/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_NAME_USAGE = COMMAND_WORD + ": Finds all persons whose names contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: n/ KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " alice bob charlie";

    public static final String MESSAGE_GROUP_USAGE = COMMAND_WORD + ": Finds all persons who are in the specified "
            + "group and displays them as a list with index numbers.\n"
            + "Parameters: g/ GROUP_NAME\n"
            + "Example: " + COMMAND_WORD + " g/ CS2103T";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose names contain any of "
            + "the specified keywords (case-insensitive) OR finds all persons who are in the specified "
            + "group and displays them as a list with index numbers.\n"
            + "Parameters: g/ KEYWORD [MORE_KEYWORDS]... OR /g GROUP_NAME\n"
            + "Examples " + COMMAND_WORD + " n/ alice bob charlie"
            + "Example: " + COMMAND_WORD + " g/ CS2103T";

    private final Predicate<Person> predicate;

    public FindCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
