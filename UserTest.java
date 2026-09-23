/**
 * Tests for the User class.
 */
public class UserTest extends student.TestCase {

    private static final double DELTA = 0.0001;

    /** Tests that the constructor stores the user's name and goal. */
    public void testConstructorStoresUserDetails() {
        User user = new User("Alex", 8.0);

        assertEquals("Alex", user.getName());
        assertEquals(8.0, user.getGoalHours(), DELTA);
        assertNotNull(user.getJournal());
        assertEquals(0, user.getJournal().size());
    }

    /** Tests that the sleep goal can be updated. */
    public void testSetGoalHours() {
        User user = new User("Alex", 8.0);

        user.setGoalHours(7.5);

        assertEquals(7.5, user.getGoalHours(), DELTA);
    }

    /** Tests that getJournal returns the same journal owned by the user. */
    public void testGetJournalReturnsUsersJournal() {
        User user = new User("Alex", 8.0);

        assertSame(user.getJournal(), user.getJournal());
    }

    /** Tests that each user receives a separate journal. */
    public void testUsersHaveSeparateJournals() {
        User firstUser = new User("Alex", 8.0);
        User secondUser = new User("Jordan", 7.5);

        firstUser.getJournal().addEntry(new SleepEntry(
                java.time.LocalDateTime.of(2026, 9, 21, 22, 0),
                java.time.LocalDateTime.of(2026, 9, 22, 6, 0),
                4, 0, ""));

        assertEquals(1, firstUser.getJournal().size());
        assertEquals(0, secondUser.getJournal().size());
        assertNotSame(firstUser.getJournal(), secondUser.getJournal());
    }
}
