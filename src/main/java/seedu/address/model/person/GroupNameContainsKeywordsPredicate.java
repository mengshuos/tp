package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 */
public class GroupNameContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public GroupNameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        return person.getGroups().stream()
                .anyMatch(group -> keywords.stream()
                        .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(group.groupName, keyword)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof GroupNameContainsKeywordsPredicate)) {
            return false;
        }

        GroupNameContainsKeywordsPredicate otherNameContainsKeywordsPredicate = (GroupNameContainsKeywordsPredicate) other;
        return keywords.equals(otherNameContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
