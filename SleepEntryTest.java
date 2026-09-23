import java.time.LocalDate;
import java.time.LocalDateTime;

/** Tests for SleepEntry. */
public class SleepEntryTest extends student.TestCase {

    private static final double DELTA = 0.0001;

    /** Tests that the constructor stores fields and calculates duration. */
    public void testConstructorAndGetters() {
        LocalDateTime bedtime = LocalDateTime.of(2026, 9, 21, 22, 0);
        LocalDateTime wakeTime = LocalDateTime.of(2026, 9, 22, 6, 30);
        SleepEntry entry = new SleepEntry(
                bedtime, wakeTime, 4, 2, "Read before bed");

        assertEquals(bedtime, entry.getBedtime());
        assertEquals(wakeTime, entry.getWakeTime());
        assertEquals(8.5, entry.getDurationHours(), DELTA);
        assertEquals(4, entry.getQualityRating());
        assertEquals(2, entry.getWakeCount());
        assertEquals("Read before bed", entry.getNotes());
        assertEquals(LocalDate.of(2026, 9, 21), entry.getDate());
    }

    /** Tests that a weekday bedtime is not classified as a weekend. */
    public void testWeekdayIsNotWeekend() {
        SleepEntry entry = entryAt(2026, 9, 21, 8.0);

        assertFalse(entry.isWeekend());
    }

    /** Tests that Saturday and Sunday bedtimes are classified as weekends. */
    public void testSaturdayAndSundayAreWeekend() {
        SleepEntry saturday = entryAt(2026, 9, 26, 8.0);
        SleepEntry sunday = entryAt(2026, 9, 27, 8.0);

        assertTrue(saturday.isWeekend());
        assertTrue(sunday.isWeekend());
    }

    /** Tests the formatted text for notes and multiple wake-ups. */
    public void testToStringWithNotesAndMultipleWakeUps() {
        SleepEntry entry = new SleepEntry(
                LocalDateTime.of(2026, 9, 21, 22, 0),
                LocalDateTime.of(2026, 9, 22, 6, 30),
                4, 2, "Read before bed");

        assertEquals(
                "2026-09-21  10:00 PM -> 2026-09-22  6:30 AM"
                        + "  (8.5 hrs, quality 4/5, woke 2 times)"
                        + "  notes: Read before bed",
                entry.toString());
    }

    /** Tests the singular wake-up text and omission of blank notes. */
    public void testToStringWithoutNotesAndOneWakeUp() {
        SleepEntry entry = new SleepEntry(
                LocalDateTime.of(2026, 9, 26, 23, 0),
                LocalDateTime.of(2026, 9, 27, 7, 0),
                5, 1, "");

        assertEquals(
                "2026-09-26  11:00 PM -> 2026-09-27  7:00 AM"
                        + "  (8.0 hrs, quality 5/5, woke 1 time)",
                entry.toString());
    }

    /** Creates an entry beginning at 10:00 PM on the specified date. */
    private static SleepEntry entryAt(int year, int month, int day,
            double durationHours) {
        LocalDateTime bedtime = LocalDateTime.of(year, month, day, 22, 0);
        LocalDateTime wakeTime = bedtime.plusMinutes((long) (durationHours * 60));
        return new SleepEntry(bedtime, wakeTime, 4, 0, "");
    }
}
