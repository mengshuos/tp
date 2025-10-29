package edutrack.logic.commands;

import static java.util.Objects.requireNonNull;

import edutrack.commons.core.Messages;
import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;
import edutrack.model.person.PersonHasTagPredicate;
import edutrack.model.tag.Tag;

/**
 * Finds and lists all persons who have a specified tag.
 * Example usage: findtag t/needs_help
 */
public class FindTagCommand extends Command {

    public static final String COMMAND_WORD = "findtag";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds all persons who have the specified tag (case-insensitive) and displays them. " +
            "Only one tag is allowed.\n"
            + "Parameters: t/TAG\n"
            + "Example: " + COMMAND_WORD + " t/needs_help";

    private final PersonHasTagPredicate predicate;

    /**
     * Creates a FindTagCommand to find persons with the specified {@code Tag}.
     *
     * @param tag The tag to search for.
     */

    public FindTagCommand(Tag tag) {
        this.predicate = new PersonHasTagPredicate(tag);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        int listedSize = model.getFilteredPersonList().size();
        return new CommandResult(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, listedSize));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof FindTagCommand)) {
            return false;
        }
        FindTagCommand otherCmd = (FindTagCommand) other;
        return predicate.equals(otherCmd.predicate);
    }
}
