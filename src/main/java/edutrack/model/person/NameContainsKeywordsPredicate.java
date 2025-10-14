package edutrack.model.person;

import java.util.List;
import java.util.function.Predicate;

import edutrack.commons.util.StringUtil;
import edutrack.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    /**
     * Specifies the matching mode for keywords:
     * NAME - match against person's name,
     * GROUP - match against group names,
     * BOTH - match against either.
     */
    public enum Mode { NAME, GROUP, BOTH };
    private final List<String> keywords;
    private final Mode mode;

    /**
     * Constructs a {@code NameContainsKeywordsPredicate} with the specified keywords and matching mode.
     *
     * @param keywords List of keywords to match.
     * @param mode The mode specifying whether to match against name, group, or both.
     */
    public NameContainsKeywordsPredicate(List<String> keywords, Mode mode) {
        this.keywords = keywords;
        this.mode = mode;
    }

    @Override
    public boolean test(Person person) {
        boolean nameMatch = keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword));
        boolean groupMatch = person.getGroups().stream()
                .anyMatch(group -> keywords.stream()
                        .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(group.groupName, keyword)));
        switch (mode) {
        case NAME:
            return nameMatch;
        case GROUP:
            return groupMatch;
        case BOTH:
            return nameMatch || groupMatch;
        default:
            return false;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsKeywordsPredicate)) {
            return false;
        }

        NameContainsKeywordsPredicate otherNameContainsKeywordsPredicate = (NameContainsKeywordsPredicate) other;
        return keywords.equals(otherNameContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
