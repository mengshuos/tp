package edutrack.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.Predicate;

import edutrack.model.tag.Tag;

/**
 * Tests whether a {@code Person} has a given {@code Tag}.
 */
public class PersonHasTagPredicate implements Predicate<Person> {

    private final Tag targetTag;

    /**
     * Constructs a {@code PersonHasTagPredicate} with the specified tag.
     *
     * @param targetTag The tag to test for.
     */
    public PersonHasTagPredicate(Tag targetTag) {
        requireNonNull(targetTag);
        this.targetTag = targetTag;
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        return person.getTags().stream().anyMatch(targetTag::equals);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof PersonHasTagPredicate)) {
            return false;
        }
        PersonHasTagPredicate otherPred = (PersonHasTagPredicate) other;
        return Objects.equals(targetTag, otherPred.targetTag);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(targetTag);
    }
}
