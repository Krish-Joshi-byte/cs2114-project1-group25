import java.util.List;

/**
 * Reads a User's SleepJournal and computes derived statistics.
 * Performs no I/O and holds no display logic — SleepTrackerApp is
 * responsible for printing whatever these methods return.
 */
public class SleepStatsCalculator {

    private final User user;

    public SleepStatsCalculator(User user) {
        this.user = user;
    }

    /**
     * Returns the mean durationHours across the given entries.
     * Returns 0 if the list is empty (never divides by zero).
     */
    public double averageDuration(List<SleepEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (SleepEntry e : entries) {
            total += e.getDurationHours();
        }
        return total / entries.size();
    }

    /**
     * Returns the entry with the maximum durationHours across
     * user.getJournal(), or null if the journal is empty.
     */
    public SleepEntry longestNight() {
        List<SleepEntry> all = user.getJournal().getAllEntries();
        SleepEntry longest = null;
        for (SleepEntry e : all) {
            if (longest == null || e.getDurationHours() > longest.getDurationHours()) {
                longest = e;
            }
        }
        return longest;
    }

    /**
     * Returns the entry with the minimum durationHours across
     * user.getJournal(), or null if the journal is empty.
     */
    public SleepEntry shortestNight() {
        List<SleepEntry> all = user.getJournal().getAllEntries();
        SleepEntry shortest = null;
        for (SleepEntry e : all) {
            if (shortest == null || e.getDurationHours() < shortest.getDurationHours()) {
                shortest = e;
            }
        }
        return shortest;
    }

    /**
     * Returns (user.getGoalHours() - averageDuration(all entries)) *
     * number of entries. Positive means behind on sleep overall;
     * negative means ahead of the goal. Returns 0 if the journal is
     * empty.
     */
    public double sleepDebt() {
        List<SleepEntry> all = user.getJournal().getAllEntries();
        if (all.isEmpty()) {
            return 0.0;
        }
        double avg = averageDuration(all);
        return (user.getGoalHours() - avg) * all.size();
    }

    /** Returns the average duration of weekday entries, or 0 if none exist. */
    public double weekdayAverage() {
        return averageForWeekend(false);
    }

    /** Returns the average duration of weekend entries, or 0 if none exist. */
    public double weekendAverage() {
        return averageForWeekend(true);
    }

    private double averageForWeekend(boolean weekend) {
        double total = 0.0;
        int count = 0;
        for (SleepEntry e : user.getJournal().getAllEntries()) {
            if (e.isWeekend() == weekend) {
                total += e.getDurationHours();
                count++;
            }
        }
        return count == 0 ? 0.0 : total / count;
    }
}
