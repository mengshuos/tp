package edutrack.logic.commands;

import static edutrack.logic.parser.CliSyntax.PREFIX_NOTE;
import static java.util.Objects.requireNonNull;

import java.util.List;

import edutrack.commons.core.index.Index;
import edutrack.logic.Messages;
import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;
import edutrack.model.person.Note;
import edutrack.model.person.Person;

public class NoteCreateCommand extends Command {

    public static final String COMMAND_WORD = "note/create";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a note to the student at the specified index.\n"
            + "Parameters: INDEX (must be a positive integer) "
            +  PREFIX_NOTE + "NOTE_CONTENT\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_NOTE + "Needs improvement in math.";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";

    private final Index index;
    private final Note noteContent;

    public NoteCreateCommand(Index index, Note note) {
        requireNonNull(index);
        requireNonNull(note);

        this.index = index;
        this.noteContent = note;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        int zeroBasedIndex = index.getZeroBased();
        if (zeroBasedIndex < 0 || zeroBasedIndex >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person target = lastShownList.get(zeroBasedIndex);

        // MODEL UPDATES SHOULD GO HERE

        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(target)));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NoteCreateCommand)) {
            return false;
        }
        NoteCreateCommand otherNoteCreateCommand = (NoteCreateCommand) other;
        return index.equals(otherNoteCreateCommand.index)
                && noteContent.equals(otherNoteCreateCommand.noteContent);
    }
}
