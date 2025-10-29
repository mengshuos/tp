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

/**
 * Creates a note for a person identified using its displayed index from the address book.
 */
public class NoteDeleteCommand extends Command {

    public static final String COMMAND_WORD = "note/delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes note attached to the student at the specified index.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1 ";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";

    private final Index index;

    /**
     * Creates a NoteCreateCommand to add the specified note to the person at the given index.
     *
     * @param index of the person in the filtered person list to edit the note
     */
    public NoteDeleteCommand(Index index) {
        requireNonNull(index);

        this.index = index;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        int zeroBasedIndex = index.getZeroBased();
        if (zeroBasedIndex < 0 || zeroBasedIndex >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Note emptyNote = new Note("");

        Person target = lastShownList.get(zeroBasedIndex);

        Person editedPerson = new Person(
                target.getName(),
                target.getPhone(),
                target.getEmail(),
                target.getAddress(),
                target.getTags(),
                target.getGroups(),
                emptyNote
        );
        model.setPerson(target, editedPerson);

        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(target)));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NoteDeleteCommand)) {
            return false;
        }
        NoteDeleteCommand otherNoteCreateCommand = (NoteDeleteCommand) other;
        return index.equals(otherNoteCreateCommand.index);
    }
}
