package edutrack.logic.commands;

import static java.util.Objects.requireNonNull;

import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;
import edutrack.model.tag.Tag;

/**
 * Creates a tag.
 */
public class TagCreateCommand extends Command {
    public static final String COMMAND_WORD = "tag/create";
    public static final String MESSAGE_SUCCESS = "New tag created: %1$s";
    public static final String MESSAGE_DUPLICATE_TAG = "This tag already exists in the address book";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Creates a new tag.\n"
            + "Parameters: t/TAG\n"
            + "Example: " + COMMAND_WORD + " t/Physics";

    private final Tag toAdd;

    /**
     * Creates a TagCreateCommand to add the specified {@code Tag}
     */
    public TagCreateCommand(Tag toAdd) {
        requireNonNull(toAdd);
        this.toAdd = toAdd;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.hasTag(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_TAG);
        }
        model.addTag(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TagCreateCommand)) {
            return false;
        }

        TagCreateCommand otherCommand = (TagCreateCommand) other;
        return toAdd.equals(otherCommand.toAdd);
    }
}

