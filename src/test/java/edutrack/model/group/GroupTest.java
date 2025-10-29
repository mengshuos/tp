package edutrack.model.group;

import static edutrack.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class GroupTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Group(null));
    }

    @Test
    public void constructor_invalidGroupName_throwsIllegalArgumentException() {
        String invalidGroupName = "";
        assertThrows(IllegalArgumentException.class, () -> new Group(invalidGroupName));
    }

    @Test
    public void isValidGroupName() {
        // null group name
        assertThrows(NullPointerException.class, () -> Group.isValidGroupName(null));

        // invalid group names
        assertFalse(Group.isValidGroupName("")); // empty string
        assertFalse(Group.isValidGroupName(" ")); // spaces only
        assertFalse(Group.isValidGroupName("CS 2103T")); // contains space
        assertFalse(Group.isValidGroupName("CS@2103T")); // contains @
        assertFalse(Group.isValidGroupName("CS#2103T")); // contains #
        assertFalse(Group.isValidGroupName("CS!2103T")); // contains !
        assertFalse(Group.isValidGroupName("CS.2103T")); // contains .

        // valid group names
        assertTrue(Group.isValidGroupName("CS2103T"));
        assertTrue(Group.isValidGroupName("cs2103t")); // lowercase
        assertTrue(Group.isValidGroupName("CS-2103T")); // contains hyphen
        assertTrue(Group.isValidGroupName("CS_2103T")); // contains underscore
        assertTrue(Group.isValidGroupName("CS2103T/G01")); // contains forward slash
        assertTrue(Group.isValidGroupName("Group-A_2024/S1")); // combination
        assertTrue(Group.isValidGroupName("123456")); // numbers only
    }

    @Test
    public void isValidGroupName_lengthConstraints() {
        // exactly 100 characters - valid
        String exactly100 = "a".repeat(100);
        assertTrue(Group.isValidGroupName(exactly100));

        // 99 characters - valid
        String under100 = "a".repeat(99);
        assertTrue(Group.isValidGroupName(under100));

        // 101 characters - invalid
        String over100 = "a".repeat(101);
        assertFalse(Group.isValidGroupName(over100));

        // 150 characters - invalid
        String wayOver100 = "a".repeat(150);
        assertFalse(Group.isValidGroupName(wayOver100));
    }

    @Test
    public void constructor_groupNameExceeds100Characters_throwsIllegalArgumentException() {
        String tooLongGroupName = "a".repeat(101);
        assertThrows(IllegalArgumentException.class, () -> new Group(tooLongGroupName));
    }

    @Test
    public void constructor_groupNameExactly100Characters_success() {
        String exactly100 = "a".repeat(100);
        Group group = new Group(exactly100);
        assertEquals(exactly100, group.groupName);
    }

    @Test
    public void equals() {
        Group group = new Group("CS2103T");

        // same object -> returns true
        assertTrue(group.equals(group));

        // same values -> returns true
        Group groupCopy = new Group("CS2103T");
        assertTrue(group.equals(groupCopy));

        // different case, same letters -> returns true (case-insensitive)
        Group groupDifferentCase = new Group("cs2103t");
        assertTrue(group.equals(groupDifferentCase));

        // null -> returns false
        assertFalse(group.equals(null));

        // different type -> returns false
        assertFalse(group.equals(5));

        // different group -> returns false
        Group differentGroup = new Group("CS2101");
        assertFalse(group.equals(differentGroup));
    }

    @Test
    public void hashCode_sameGroup_sameHashCode() {
        Group group1 = new Group("CS2103T");
        Group group2 = new Group("CS2103T");
        assertEquals(group1.hashCode(), group2.hashCode());
    }

    @Test
    public void hashCode_caseInsensitive_sameHashCode() {
        Group group1 = new Group("CS2103T");
        Group group2 = new Group("cs2103t");
        assertEquals(group1.hashCode(), group2.hashCode());
    }

    @Test
    public void toStringMethod() {
        Group group = new Group("CS2103T");
        assertEquals("[CS2103T]", group.toString());
    }
}

