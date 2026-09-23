import java.time.LocalDateTime;
import java.util.List;

/** Tests for SleepStatsCalculator. */
public class SleepStatsCalculatorTest extends student.TestCase {

    private static final double DELTA = 0.0001;

    /** Tests the average duration for a non-empty list. */
    public void testAverageDuration() {
        User user = new User("Alex", 8.0);
        SleepEntry first = entryAt(2026, 9, 21, 7.0);
        SleepEntry second = entryAt(2026, 9, 22, 9.0);
        user.getJournal().addEntry(first);
        user.getJournal().addEntry(second);
        SleepStatsCalculator stats = new SleepStatsCalculator(user);

        assertEquals(8.0, stats.averageDuration(
                user.getJournal().getAllEntries()), DELTA);
    }

    /** Tests that averageDuration returns zero for null and empty lists. */
    public void testAverageDurationWithNoEntries() {
        User user = new User("Alex", 8.0);
        SleepStatsCalculator stats = new SleepStatsCalculator(user);

        assertEquals(0.0, stats.averageDuration(null), DELTA);
        assertEquals(0.0, stats.averageDuration(
                user.getJournal().getAllEntries()), DELTA);
    }

    /** Tests the longest and shortest journal entries. */
    public void testLongestAndShortestNight() {
        User user = new User("Alex", 8.0);
        SleepEntry shortest = entryAt(2026, 9, 21, 5.0);
        SleepEntry longest = entryAt(2026, 9, 22, 9.0);
        SleepEntry middle = entryAt(2026, 9, 23, 7.0);
        user.getJournal().addEntry(shortest);
        user.getJournal().addEntry(longest);
        user.getJournal().addEntry(middle);
        SleepStatsCalculator stats = new SleepStatsCalculator(user);

        assertSame(longest, stats.longestNight());
        assertSame(shortest, stats.shortestNight());
    }

    /** Tests that longest and shortest return null for an empty journal. */
    public void testLongestAndShortestWithNoEntries() {
        SleepStatsCalculator stats = new SleepStatsCalculator(
                new User("Alex", 8.0));

        assertNull(stats.longestNight());
        assertNull(stats.shortestNight());
    }

    /** Tests sleep debt against the user's nightly goal. */
    public void testSleepDebt() {
        User user = new User("Alex", 8.0);
        user.getJournal().addEntry(entryAt(2026, 9, 21, 7.0));
        user.getJournal().addEntry(entryAt(2026, 9, 22, 9.0));
        user.getJournal().addEntry(entryAt(2026, 9, 23, 5.0));
        SleepStatsCalculator stats = new SleepStatsCalculator(user);

        // Average is 7 hours, so the debt is (8 - 7) * 3 = 3 hours.
        assertEquals(3.0, stats.sleepDebt(), DELTA);
    }

    /** Tests that sleep debt is zero when the journal is empty. */
    public void testSleepDebtWithNoEntries() {
        SleepStatsCalculator stats = new SleepStatsCalculator(
                new User("Alex", 8.0));

        assertEquals(0.0, stats.sleepDebt(), DELTA);
    }

    /** Tests weekday and weekend averages independently. */
    public void testWeekdayAndWeekendAverages() {
        User user = new User("Alex", 8.0);
        user.getJournal().addEntry(entryAt(2026, 9, 21, 7.0));
        user.getJournal().addEntry(entryAt(2026, 9, 22, 9.0));
        user.getJournal().addEntry(entryAt(2026, 9, 26, 5.0));
        user.getJournal().addEntry(entryAt(2026, 9, 27, 7.0));
        SleepStatsCalculator stats = new SleepStatsCalculator(user);

        assertEquals(8.0, stats.weekdayAverage(), DELTA);
        assertEquals(6.0, stats.weekendAverage(), DELTA);
    }

    /** Tests that category averages are zero when a category has no entries. */
    public void testWeekdayAndWeekendAveragesWithMissingCategory() {
        User user = new User("Alex", 8.0);
        user.getJournal().addEntry(entryAt(2026, 9, 21, 8.0));
        SleepStatsCalculator stats = new SleepStatsCalculator(user);

        assertEquals(8.0, stats.weekdayAverage(), DELTA);
        assertEquals(0.0, stats.weekendAverage(), DELTA);
    }

    /** Creates an entry beginning at 10:00 PM on the specified date. */
    private static SleepEntry entryAt(int year, int month, int day,
            double durationHours) {
        LocalDateTime bedtime = LocalDateTime.of(year, month, day, 22, 0);
        LocalDateTime wakeTime = bedtime.plusMinutes((long) (durationHours * 60));
        return new SleepEntry(bedtime, wakeTime, 4, 0, "");
    }
}
