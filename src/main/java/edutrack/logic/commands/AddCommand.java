package edutrack.logic.commands;

import static edutrack.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static edutrack.logic.parser.CliSyntax.PREFIX_EMAIL;
import static edutrack.logic.parser.CliSyntax.PREFIX_GROUP;
import static edutrack.logic.parser.CliSyntax.PREFIX_NAME;
import static edutrack.logic.parser.CliSyntax.PREFIX_PHONE;
import static edutrack.logic.parser.CliSyntax.PREFIX_TAG;
import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import edutrack.commons.util.ToStringBuilder;
import edutrack.logic.Messages;
import edutrack.logic.commands.exceptions.CommandException;
import edutrack.model.Model;
import edutrack.model.group.Group;
import edutrack.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the address book. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + "[" + PREFIX_PHONE + "PHONE" + "] "
            + "[" + PREFIX_EMAIL + "EMAIL" + "] "
            + "[" + PREFIX_ADDRESS + "ADDRESS" + "] "
            + "[" + PREFIX_TAG + "TAG]... "
            + "[" + PREFIX_GROUP + "GROUP]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney "
            + PREFIX_GROUP + "CS2103T "
            + PREFIX_GROUP + "CS2101";


    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";
    public static final String MESSAGE_GROUP_NOT_FOUND =
            "Groups do not exist: %s. Please create them first using group/create.";
    public static final String MESSAGE_TAG_NOT_FOUND =
            "Tags do not exist: %s. Please create them first using tag/create.";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        // Validate all tags exist in the model
        Set<String> nonExistentTags = new HashSet<>();
        for (edutrack.model.tag.Tag tag : toAdd.getTags()) {
            if (!model.hasTag(tag)) {
                nonExistentTags.add(tag.tagName);
            }
        }

        if (!nonExistentTags.isEmpty()) {
            String tagNames = String.join(", ", nonExistentTags);
            throw new CommandException(String.format(MESSAGE_TAG_NOT_FOUND, tagNames));
        }

        // Validate all groups exist in the model and get central references
        Set<Group> nonExistentGroups = new HashSet<>();
        Set<Group> centralGroups = new HashSet<>();

        for (Group group : toAdd.getGroups()) {
            if (!model.hasGroup(group)) {
                nonExistentGroups.add(group);
            } else {
                // Get the centrally tracked Group object
                centralGroups.add(model.getGroup(group));
            }
        }

        if (!nonExistentGroups.isEmpty()) {
            String groupNames = nonExistentGroups.stream()
                    .map(g -> g.groupName)
                    .collect(Collectors.joining(", "));
            throw new CommandException(String.format(MESSAGE_GROUP_NOT_FOUND, groupNames));
        }

        // Create person with centrally tracked groups
        Person personWithCentralGroups = new Person(
                toAdd.getName(),
                toAdd.getPhone(),
                toAdd.getEmail(),
                toAdd.getAddress(),
                toAdd.getTags(),
                centralGroups
        );

        model.addPerson(personWithCentralGroups);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(personWithCentralGroups)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand otherAddCommand = (AddCommand) other;
        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
