import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Tests for SleepJournal. */
public class SleepJournalTest extends student.TestCase {

    /** Tests adding entries, size, and insertion order. */
    public void testAddEntryAndSize() {
        SleepJournal journal = new SleepJournal();
        SleepEntry first = entryAt(2026, 9, 21, 8.0);
        SleepEntry second = entryAt(2026, 9, 22, 7.0);

        journal.addEntry(first);
        journal.addEntry(second);

        assertEquals(2, journal.size());
        assertSame(first, journal.getAllEntries().get(0));
        assertSame(second, journal.getAllEntries().get(1));
    }

    /** Tests that null entries are rejected. */
    public void testAddNullEntry() {
        SleepJournal journal = new SleepJournal();
        Exception thrown = null;

        try {
            journal.addEntry(null);
        }
        catch (IllegalArgumentException exception) {
            thrown = exception;
        }

        assertNotNull(thrown);
        assertEquals(0, journal.size());
    }

    /** Tests that the all-entries view cannot be modified by callers. */
    public void testAllEntriesIsUnmodifiable() {
        SleepJournal journal = new SleepJournal();
        journal.addEntry(entryAt(2026, 9, 21, 8.0));
        List<SleepEntry> entries = journal.getAllEntries();
        Exception thrown = null;

        try {
            entries.clear();
        }
        catch (UnsupportedOperationException exception) {
            thrown = exception;
        }

        assertNotNull(thrown);
        assertEquals(1, journal.size());
    }

    /** Tests that date ranges include both boundary dates. */
    public void testEntriesInRangeIsInclusive() {
        SleepJournal journal = new SleepJournal();
        SleepEntry before = entryAt(2026, 9, 20, 8.0);
        SleepEntry start = entryAt(2026, 9, 21, 7.0);
        SleepEntry middle = entryAt(2026, 9, 22, 6.0);
        SleepEntry end = entryAt(2026, 9, 23, 9.0);
        SleepEntry after = entryAt(2026, 9, 24, 8.0);
        journal.addEntry(before);
        journal.addEntry(start);
        journal.addEntry(middle);
        journal.addEntry(end);
        journal.addEntry(after);

        List<SleepEntry> result = journal.getEntriesInRange(
                LocalDate.of(2026, 9, 21), LocalDate.of(2026, 9, 23));

        assertEquals(3, result.size());
        assertSame(start, result.get(0));
        assertSame(middle, result.get(1));
        assertSame(end, result.get(2));
    }

    /** Tests invalid and null date ranges. */
    public void testEntriesInRangeWithInvalidDates() {
        SleepJournal journal = new SleepJournal();
        journal.addEntry(entryAt(2026, 9, 21, 8.0));

        assertTrue(journal.getEntriesInRange(
                LocalDate.of(2026, 9, 23),
                LocalDate.of(2026, 9, 21)).isEmpty());
        assertTrue(journal.getEntriesInRange(null,
                LocalDate.of(2026, 9, 21)).isEmpty());
        assertTrue(journal.getEntriesInRange(LocalDate.of(2026, 9, 21),
                null).isEmpty());
    }

    /** Tests that a range with no matching dates returns an empty list. */
    public void testEntriesInRangeWithNoMatches() {
        SleepJournal journal = new SleepJournal();
        journal.addEntry(entryAt(2026, 9, 21, 8.0));

        assertTrue(journal.getEntriesInRange(
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 2)).isEmpty());
    }

    /** Creates an entry beginning at 10:00 PM on the specified date. */
    private static SleepEntry entryAt(int year, int month, int day,
            double durationHours) {
        LocalDateTime bedtime = LocalDateTime.of(year, month, day, 22, 0);
        LocalDateTime wakeTime = bedtime.plusMinutes((long) (durationHours * 60));
        return new SleepEntry(bedtime, wakeTime, 4, 0, "");
    }
}
