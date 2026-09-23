import java.util.Scanner;

/**
 * Tests for SleepTrackerApp. Each test feeds the app scripted input and
 * checks what it prints and what ends up in the user's journal. Invalid
 * input must be re-prompted and never stored.
 *
 * @author Alper
 * @version 2026.09.22
 */
public class SleepTrackerAppTest
        extends student.TestCase {

    private static final double DELTA = 0.0001;

    /** Saturday night to Sunday morning, 7.75 hours. */
    private static final String SAT_BED = "2026-09-19 22:30";
    private static final String SUN_WAKE = "2026-09-20 06:15";

    /** Tuesday night to Wednesday morning, 7 hours. */
    private static final String TUE_BED = "2026-09-22 23:00";
    private static final String WED_WAKE = "2026-09-23 06:00";

    private User user;

    /**
     * Creates a user with an 8-hour goal before each test.
     */
    public void setUp() {
        systemOut().clearHistory();
        user = new User("Alex", 8.0);
    }

    /**
     * Joins lines of input the way a user would type them.
     *
     * @param lines
     *            the lines to type
     * @return the lines separated by newlines
     */
    private static String lines(String... lines) {
        return String.join("\n", lines) + "\n";
    }

    /**
     * Builds the input for one valid sleep entry.
     *
     * @param bed
     *            bedtime text
     * @param wake
     *            wake time text
     * @param quality
     *            quality rating
     * @param wakeCount
     *            number of wake-ups
     * @return the input lines for promptForEntry
     */
    private static String night(
            String bed,
            String wake,
            int quality,
            int wakeCount) {
        return lines(bed, wake, String.valueOf(quality),
                String.valueOf(wakeCount), "");
    }

    /**
     * Creates an app for the test user that reads the given input.
     *
     * @param input
     *            what the user will type
     * @return the app
     */
    private SleepTrackerApp app(String input) {
        return new SleepTrackerApp(new Scanner(input), user);
    }

    /**
     * Returns everything printed so far.
     *
     * @return the output history
     */
    private String output() {
        return systemOut().getHistory();
    }

    // run()

    /**
     * Tests a full session: setup, logging a night, viewing history and
     * stats, then quitting.
     */
    public void testRunFullSession() {
        SleepTrackerApp tracker = new SleepTrackerApp(new Scanner(
                lines("Alex", "8", "1") + night(SAT_BED, SUN_WAKE, 4, 1)
                        + lines("2", "3", "5")));
        assertNull(tracker.getCurrentUser());
        tracker.run();

        User created = tracker.getCurrentUser();
        assertEquals("Alex", created.getName());
        assertEquals(8.0, created.getGoalHours(), DELTA);
        assertEquals(1, created.getJournal().size());
        assertTrue(output().contains("Welcome, Alex!"));
        assertTrue(output().contains("Saved 7.75 hours of sleep"));
        assertTrue(output().contains("--- Sleep history (1 entry) ---"));
        assertTrue(output().contains("--- Sleep statistics ---"));
        assertTrue(output().contains("--- Recommendations ---"));
        assertTrue(output().contains("Goodbye - sleep well!"));
    }

    /**
     * Tests that a blank name and invalid goals are re-prompted before the
     * user is created, including a goal of 0.
     */
    public void testRunRejectsBadNameAndGoal() {
        SleepTrackerApp tracker = new SleepTrackerApp(new Scanner(
                lines("", "   ", "Alex", "eight", "0", "-1", "25", "8", "5")));
        tracker.run();

        assertEquals("Alex", tracker.getCurrentUser().getName());
        assertEquals(8.0, tracker.getCurrentUser().getGoalHours(), DELTA);
        assertTrue(output().contains("Please enter a name."));
        assertTrue(output().contains("A number is needed"));
        assertTrue(output().contains("0.00 hours is not a valid goal"));
        assertTrue(output().contains("-1.00 hours is not a valid goal"));
        assertTrue(output().contains("25.00 hours is not a valid goal"));
    }

    /**
     * Tests that invalid menu choices are re-prompted.
     */
    public void testRunInvalidMenuChoice() {
        app(lines("9", "abc", "0", "5")).run();
        assertTrue(output().contains("Please enter a number from 1 to 6."));
        assertTrue(output().contains("A whole number is needed."));
        assertTrue(output().contains("Goodbye - sleep well!"));
    }

    /**
     * Tests that the app stops cleanly when input runs out during setup.
     */
    public void testRunInputEndsDuringSetup() {
        SleepTrackerApp tracker = new SleepTrackerApp(new Scanner(""));
        tracker.run();
        assertNull(tracker.getCurrentUser());
        assertTrue(output().contains("No more input."));
        assertTrue(output().contains("Goodbye - sleep well!"));
    }

    /**
     * Tests changing the goal from the menu; 0 is rejected.
     */
    public void testRunChangeGoal() {
        app(lines("4", "0", "seven", "7.5", "5")).run();
        assertEquals(7.5, user.getGoalHours(), DELTA);
        assertTrue(output().contains("0.00 hours is not a valid goal"));
        assertTrue(output().contains("A number is needed"));
        assertTrue(output().contains("Your goal is now 7.50 hours."));
    }

    /**
     * Tests that the goal is unchanged if input ends while changing it.
     */
    public void testRunChangeGoalInputEnds() {
        app(lines("4")).run();
        assertEquals(8.0, user.getGoalHours(), DELTA);
        assertTrue(output().contains("your goal was not changed"));
        assertTrue(output().contains("No more input."));
    }

    /** Tests changing the user's name without replacing the journal. */
    public void testRunChangeName() {
        app(lines("6", "", "Avery", "5")).run();
        assertEquals("Avery", user.getName());
        assertEquals(0, user.getJournal().size());
        assertTrue(output().contains("Please enter a name."));
        assertTrue(output().contains("Your name is now Avery."));
    }

    /**
     * Tests the command-line entry point.
     */
    public void testMain() {
        setSystemIn("Alex", "8", "5");
        SleepTrackerApp.main(new String[0]);
        assertTrue(output().contains("Welcome, Alex!"));
        assertTrue(output().contains("Goodbye - sleep well!"));
    }

    // promptForEntry()

    /**
     * Tests logging a valid entry.
     */
    public void testPromptForEntryValid() {
        app(night(SAT_BED, SUN_WAKE, 4, 1)).promptForEntry();

        assertEquals(1, user.getJournal().size());
        SleepEntry entry = user.getJournal().getAllEntries().get(0);
        assertEquals(7.75, entry.getDurationHours(), DELTA);
        assertEquals(4, entry.getQualityRating());
        assertEquals(1, entry.getWakeCount());
        assertTrue(output().contains("Saved 7.75 hours of sleep"));
    }

    /**
     * Tests that a wake time before the bedtime is re-prompted.
     */
    public void testPromptForEntryWakeBeforeBed() {
        app(lines("2026-09-19 22:30", "2026-09-19 21:00")
                + night(SAT_BED, SUN_WAKE, 4, 1)).promptForEntry();

        assertTrue(output().contains("Wake time must be after bedtime."));
        assertEquals(1, user.getJournal().size());
        assertEquals(7.75,
                user.getJournal().getAllEntries().get(0).getDurationHours(),
                DELTA);
    }

    /**
     * Tests that a wake time equal to the bedtime is re-prompted.
     */
    public void testPromptForEntrySameTimes() {
        app(lines(SAT_BED, SAT_BED) + night(SAT_BED, SUN_WAKE, 4, 1))
                .promptForEntry();
        assertTrue(output().contains("Wake time must be after bedtime."));
        assertEquals(1, user.getJournal().size());
    }

    /**
     * Tests that a night longer than 24 hours is re-prompted.
     */
    public void testPromptForEntryTooLong() {
        app(lines("2026-09-19 22:30", "2026-09-21 06:00")
                + night(SAT_BED, SUN_WAKE, 4, 1)).promptForEntry();

        assertTrue(output().contains(
                "31.50 hours of sleep is not reasonable"));
        assertEquals(1, user.getJournal().size());
    }

    /**
     * Tests that badly formatted and impossible dates are re-prompted.
     */
    public void testPromptForEntryBadDates() {
        app(lines("2026-09-19", "22:30", "tomorrow", "2026-02-30 22:00",
                "2026-09-19 25:00") + night(SAT_BED, SUN_WAKE, 4, 1))
                .promptForEntry();

        assertTrue(output().contains(
                "\"2026-09-19\" is not a valid date and time"));
        assertTrue(output().contains(
                "\"2026-02-30 22:00\" is not a valid date and time"));
        assertTrue(output().contains(
                "\"2026-09-19 25:00\" is not a valid date and time"));
        assertEquals(1, user.getJournal().size());
    }

    /**
     * Tests that single-digit hours such as 6:15 are accepted.
     */
    public void testPromptForEntrySingleDigitHour() {
        app(night("2026-09-19 22:30", "2026-09-20 6:15", 4, 1))
                .promptForEntry();
        assertEquals(1, user.getJournal().size());
    }

    /**
     * Tests that bad quality ratings and wake counts are re-prompted.
     */
    public void testPromptForEntryBadQualityAndWakeCount() {
        app(lines(SAT_BED, SUN_WAKE, "five", "6", "0", "4", "-1", "51", "abc",
                "2", "caffeine at 9pm")).promptForEntry();

        assertTrue(output().contains("A whole number is needed - please "
                + "enter 1-5."));
        assertTrue(output().contains("Quality must be from 1 to 5."));
        assertTrue(output().contains("Please enter a number from 0 to 50."));
        assertEquals(1, user.getJournal().size());
        SleepEntry entry = user.getJournal().getAllEntries().get(0);
        assertEquals(4, entry.getQualityRating());
        assertEquals(2, entry.getWakeCount());
    }

    /**
     * Tests that nothing is stored when input ends part way through.
     */
    public void testPromptForEntryInputEnds() {
        app(lines(SAT_BED, SUN_WAKE, "4")).promptForEntry();
        assertEquals(0, user.getJournal().size());
        assertTrue(output().contains("Input ended - the entry was not saved."));
    }

    // ----------------------------------------------------------------------
    // No user
    // ----------------------------------------------------------------------

    /**
     * Tests that the screens refuse to run before a user is set up.
     */
    public void testNoUser() {
        SleepTrackerApp tracker = new SleepTrackerApp(new Scanner(""));
        tracker.promptForEntry();
        tracker.displayHistory();
        tracker.displayStatsAndRecommendations();
        assertNull(tracker.getCurrentUser());
        assertTrue(output().contains("Please set up a user first."));
        assertFalse(output().contains("---"));
    }

    // ----------------------------------------------------------------------
    // displayHistory()
    // ----------------------------------------------------------------------

    /**
     * Tests the history screen with no entries.
     */
    public void testDisplayHistoryEmpty() {
        app("").displayHistory();
        assertTrue(output().contains("No sleep entries recorded yet."));
    }

    /**
     * Tests the history screen with a weekend and a weekday night.
     */
    public void testDisplayHistoryWithEntries() {
        SleepTrackerApp tracker = app(night(SAT_BED, SUN_WAKE, 4, 1)
                + night(TUE_BED, WED_WAKE, 3, 0));
        tracker.promptForEntry();
        tracker.promptForEntry();
        systemOut().clearHistory();

        tracker.displayHistory();
        String history = output();
        assertTrue(history.contains("--- Sleep history (2 entries) ---"));
        assertTrue(history.contains("7.75"));
        assertTrue(history.contains("7.00"));
        assertTrue(history.contains("4/5"));
        assertTrue(history.contains("3/5"));
        assertTrue(history.contains("(weekend)"));
    }

    // displayStatsAndRecommendations()

    /**
     * Tests that stats are not calculated when there are no entries.
     */
    public void testDisplayStatsEmpty() {
        app("").displayStatsAndRecommendations();
        assertTrue(output().contains("No sleep entries yet"));
        assertFalse(output().contains("--- Sleep statistics ---"));
    }

    /**
     * Tests the stats screen with 6, 9 and 7 hour nights (one on a
     * weekend), below an 8-hour goal.
     */
    public void testDisplayStatsWithEntries() {
        SleepTrackerApp tracker = app(
                night("2026-09-21 23:00", "2026-09-22 05:00", 2, 3)
                        + night("2026-09-19 22:00", "2026-09-20 07:00", 5, 0)
                        + night("2026-09-22 23:00", "2026-09-23 06:00", 3, 1));
        tracker.promptForEntry();
        tracker.promptForEntry();
        tracker.promptForEntry();
        systemOut().clearHistory();

        tracker.displayStatsAndRecommendations();
        String stats = output();
        assertTrue(stats.contains("Nights logged: 3"));
        assertTrue(stats.contains("Log at least 7 nights"));
        assertTrue(stats.contains("Average sleep (all nights): 7.33 hours"));
        assertTrue(stats.contains("Longest night: 9.00 hours"));
        assertTrue(stats.contains("Shortest night: 6.00 hours"));
        assertTrue(stats.contains("--- Recommendations ---"));
        assertFalse(stats.contains("no data"));
    }

    /**
     * Tests the last-7-days and last-30-days averages, and that a missing
     * weekend category shows as no data.
     */
    public void testDisplayStatsRangesAndWeekdaysOnly() {
        SleepTrackerApp tracker = app(
                night("2026-08-04 23:00", "2026-08-05 07:00", 4, 0)
                        + night(TUE_BED, WED_WAKE, 3, 1));
        tracker.promptForEntry();
        tracker.promptForEntry();
        systemOut().clearHistory();

        tracker.displayStatsAndRecommendations();
        String stats = output();
        assertTrue(stats.contains("Average sleep (all nights): 7.50 hours"));
        assertTrue(stats.contains("Average, last 7 days"));
        assertTrue(stats.contains("Average, last 30 days"));
        assertTrue(stats.contains("7.00 hours over 1 night"));
        assertTrue(stats.contains("no data"));
    }

    /**
     * Tests that meeting the goal reports no sleep debt.
     */
    public void testDisplayStatsGoalMet() {
        SleepTrackerApp tracker =
                app(night("2026-09-22 22:00", "2026-09-23 07:00", 5, 0));
        tracker.promptForEntry();
        tracker.displayStatsAndRecommendations();
        assertTrue(output().contains("none - you are meeting your goal"));
    }
}
// --
