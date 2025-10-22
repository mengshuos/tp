package edutrack.logic.commands;

import static edutrack.model.Model.PREDICATE_SHOW_ALL_TAGS;
import static java.util.Objects.requireNonNull;

import edutrack.model.Model;

/**
 * Lists all tags.
 */
public class TagListCommand extends Command {
    public static final String COMMAND_WORD = "tag/list";
    public static final String MESSAGE_SUCCESS = "Listed all tags";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredTagList(PREDICATE_SHOW_ALL_TAGS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}

