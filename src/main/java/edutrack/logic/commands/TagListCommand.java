package edutrack.logic.commands;

import static edutrack.model.Model.PREDICATE_SHOW_ALL_TAGS;
import static java.util.Objects.requireNonNull;

import java.util.stream.Collectors;

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

        String tagsList = model.getFilteredTagList().stream()
                .map(tag -> tag.tagName)
                .collect(Collectors.joining(", "));

        if (tagsList.isEmpty()) {
            return new CommandResult("No tags found. Create tags using: tag/create t/TAG_NAME");
        }

        return new CommandResult(MESSAGE_SUCCESS + ":\n" + tagsList);
    }
}

