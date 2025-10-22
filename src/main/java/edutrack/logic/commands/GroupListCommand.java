package edutrack.logic.commands;

import static edutrack.model.Model.PREDICATE_SHOW_ALL_GROUPS;
import static java.util.Objects.requireNonNull;

import edutrack.model.Model;

/**
 * Lists all groups.
 */

public class GroupListCommand extends Command {
    public static final String COMMAND_WORD = "group/list";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredGroupList(PREDICATE_SHOW_ALL_GROUPS);
        return new CommandResult("Listed all groups");
    }
}


