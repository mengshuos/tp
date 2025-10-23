package edutrack.logic.commands;

import static java.util.Objects.requireNonNull;

import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;
import edutrack.model.tag.Tag;

/**
 * Deletes a tag.
 */
public class TagDeleteCommand extends Command {
    public static final String COMMAND_WORD = "tag/delete";
    public static final String MESSAGE_SUCCESS = "Deleted tag: %1$s";
    public static final String MESSAGE_TAG_NOT_FOUND = "This tag does not exist in the address book";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes an existing tag.\n"
            + "Parameters: t/TAG\n"
            + "Example: " + COMMAND_WORD + " t/Physics";

    private final Tag toDelete;

    /**
     * Creates a TagDeleteCommand to delete the specified {@code Tag}
     */
    public TagDeleteCommand(Tag toDelete) {
        requireNonNull(toDelete);
        this.toDelete = toDelete;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.hasTag(toDelete)) {
            throw new CommandException(MESSAGE_TAG_NOT_FOUND);
        }
        model.deleteTag(toDelete);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toDelete));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TagDeleteCommand)) {
            return false;
        }

        TagDeleteCommand otherCommand = (TagDeleteCommand) other;
        return toDelete.equals(otherCommand.toDelete);
    }
}

