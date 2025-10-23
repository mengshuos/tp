package edutrack.model.tag;

import static edutrack.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class UniqueTagListTest {

    private final UniqueTagList uniqueTagList = new UniqueTagList();

    @Test
    public void contains_nullTag_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTagList.contains(null));
    }

    @Test
    public void contains_tagNotInList_returnsFalse() {
        assertFalse(uniqueTagList.contains(new Tag("Physics")));
    }

    @Test
    public void contains_tagInList_returnsTrue() {
        Tag tag = new Tag("Physics");
        uniqueTagList.add(tag);
        assertTrue(uniqueTagList.contains(tag));
    }

    @Test
    public void contains_tagWithSameIdentityFieldsInList_returnsTrue() {
        Tag tag = new Tag("Physics");
        uniqueTagList.add(tag);
        Tag tagCopy = new Tag("Physics");
        assertTrue(uniqueTagList.contains(tagCopy));
    }

    @Test
    public void add_nullTag_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTagList.add(null));
    }

    @Test
    public void add_duplicateTag_throwsIllegalArgumentException() {
        Tag tag = new Tag("Physics");
        uniqueTagList.add(tag);
        assertThrows(IllegalArgumentException.class, () -> uniqueTagList.add(tag));
    }

    @Test
    public void remove_nullTag_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTagList.remove(null));
    }

    @Test
    public void remove_tagDoesNotExist_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> uniqueTagList.remove(new Tag("Physics")));
    }

    @Test
    public void remove_existingTag_removesTag() {
        Tag tag = new Tag("Physics");
        uniqueTagList.add(tag);
        uniqueTagList.remove(tag);
        assertFalse(uniqueTagList.contains(tag));
    }

    @Test
    public void setTags_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTagList.setTags(null));
    }

    @Test
    public void setTags_list_replacesOwnListWithProvidedList() {
        Tag physics = new Tag("Physics");
        uniqueTagList.add(physics);

        List<Tag> tagList = Arrays.asList(new Tag("Chemistry"), new Tag("Biology"));
        uniqueTagList.setTags(tagList);

        assertFalse(uniqueTagList.contains(physics));
        assertTrue(uniqueTagList.contains(new Tag("Chemistry")));
        assertTrue(uniqueTagList.contains(new Tag("Biology")));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () ->
            uniqueTagList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void equals() {
        UniqueTagList firstList = new UniqueTagList();
        UniqueTagList secondList = new UniqueTagList();

        // same object -> returns true
        assertTrue(firstList.equals(firstList));

        // null -> returns false
        assertFalse(firstList.equals(null));

        // different types -> returns false
        assertFalse(firstList.equals(5));

        // different tags -> returns false
        firstList.add(new Tag("Physics"));
        assertFalse(firstList.equals(secondList));

        // same tags -> returns true
        secondList.add(new Tag("Physics"));
        assertTrue(firstList.equals(secondList));
    }

    @Test
    public void hashCode_sameContent_sameHashCode() {
        UniqueTagList firstList = new UniqueTagList();
        UniqueTagList secondList = new UniqueTagList();

        firstList.add(new Tag("Physics"));
        secondList.add(new Tag("Physics"));

        assertEquals(firstList.hashCode(), secondList.hashCode());
    }

    @Test
    public void iterator_iteratesThroughList() {
        Tag physics = new Tag("Physics");
        Tag chemistry = new Tag("Chemistry");
        uniqueTagList.add(physics);
        uniqueTagList.add(chemistry);

        int count = 0;
        for (Tag tag : uniqueTagList) {
            count++;
        }
        assertEquals(2, count);
    }
}

