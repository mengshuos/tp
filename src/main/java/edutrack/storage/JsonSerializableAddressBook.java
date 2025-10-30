package edutrack.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import edutrack.commons.core.LogsCenter;
import edutrack.commons.exceptions.IllegalValueException;
import edutrack.model.AddressBook;
import edutrack.model.ReadOnlyAddressBook;
import edutrack.model.group.Group;
import edutrack.model.person.Person;
import edutrack.model.tag.Tag;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";

    private static final Logger logger = LogsCenter.getLogger(JsonSerializableAddressBook.class);

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedGroup> groups = new ArrayList<>();
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons, groups, and tags.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
                                       @JsonProperty("groups") List<JsonAdaptedGroup> groups,
                                       @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
        if (groups != null) {
            this.groups.addAll(groups);
        }
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        groups.addAll(source.getGroupList().stream().map(JsonAdaptedGroup::new).collect(Collectors.toList()));
        tags.addAll(source.getTagList().stream().map(JsonAdaptedTag::new).collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     * Ensures backward compatibility by auto-creating groups and tags referenced by persons
     * that don't exist in the central lists.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();

        // Step 1: Load all groups into central list first
        Set<Group> centralGroups = new HashSet<>();
        for (JsonAdaptedGroup jsonAdaptedGroup : groups) {
            Group group = jsonAdaptedGroup.toModelType();
            if (centralGroups.stream().anyMatch(group::equals)) {
                throw new IllegalValueException("This group already exists.");
            }
            centralGroups.add(group);
            addressBook.addGroup(group);
        }

        // Step 1.5: Load all tags into central list
        Set<Tag> centralTags = new HashSet<>();
        for (JsonAdaptedTag jsonAdaptedTag : tags) {
            Tag tag = jsonAdaptedTag.toModelType();
            if (centralTags.stream().anyMatch(tag::equals)) {
                throw new IllegalValueException("This tag already exists.");
            }
            centralTags.add(tag);
            addressBook.addTag(tag);
        }

        // Step 2: Load persons and migrate their groups and tags to central references
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();

            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }

            // Step 3: Ensure all person's groups exist in central list (backward compatibility)
            Set<Group> personGroupsWithCentralRefs = new HashSet<>();
            for (Group personGroup : person.getGroups()) {
                Group centralGroup;

                if (centralGroups.stream().anyMatch(personGroup::equals)) {
                    // Group exists - get the central reference
                    centralGroup = centralGroups.stream()
                            .filter(personGroup::equals)
                            .findFirst()
                            .get();
                } else {
                    // Group doesn't exist - auto-create for backward compatibility
                    logger.warning("Group '" + personGroup.groupName
                            + "' not found in central list. Auto-creating for backward compatibility.");
                    centralGroup = personGroup;
                    centralGroups.add(centralGroup);
                    addressBook.addGroup(centralGroup);
                }

                personGroupsWithCentralRefs.add(centralGroup);
            }

            // Step 4: Ensure all person's tags exist in central list (backward compatibility)
            Set<Tag> personTagsWithCentralRefs = new HashSet<>();
            for (Tag personTag : person.getTags()) {
                Tag centralTag;

                if (centralTags.stream().anyMatch(personTag::equals)) {
                    // Tag exists - get the central reference
                    centralTag = centralTags.stream()
                            .filter(personTag::equals)
                            .findFirst()
                            .get();
                } else {
                    // Tag doesn't exist - auto-create for backward compatibility
                    logger.warning("Tag '" + personTag.tagName
                            + "' not found in central list. Auto-creating for backward compatibility.");
                    centralTag = personTag;
                    centralTags.add(centralTag);
                    addressBook.addTag(centralTag);
                }

                personTagsWithCentralRefs.add(centralTag);
            }

            // Create person with central group and tag references
            Person personWithCentralRefs = new Person(
                    person.getName(),
                    person.getPhone(),
                    person.getEmail(),
                    person.getAddress(),
                    personTagsWithCentralRefs,
                    personGroupsWithCentralRefs,
                    person.getNote()
            );

            addressBook.addPerson(personWithCentralRefs);
        }

        return addressBook;
    }

}
