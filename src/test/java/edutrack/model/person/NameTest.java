package edutrack.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import static edutrack.testutil.Assert.assertThrows;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains non-alphanumeric characters
        assertFalse(Name.isValidName("/")); // slash only, first char must be alphanumeric
        assertFalse(Name.isValidName("/LeadingSlash")); // first char cannot be slash
        assertFalse(Name.isValidName(".")); // dot only, first char must be alphanumeric
        assertFalse(Name.isValidName(".LeadingDot")); // first char cannot be dot

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        assertTrue(Name.isValidName("A/B")); // slash allowed after first char
        assertTrue(Name.isValidName("Arunasalam S/O Dorugutham")); // common S/O format
        assertTrue(Name.isValidName("A / B")); // spaces around slash
        assertTrue(Name.isValidName("J.K. Rowling")); // dots allowed after first char
        assertTrue(Name.isValidName("J. K. Rowling")); // dots with spaces
        assertTrue(Name.isValidName("J.K.R.")); // trailing dot allowed
        assertTrue(Name.isValidName("José Álvarez")); // accented characters
        assertTrue(Name.isValidName("O'Connor")); // apostrophe
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
