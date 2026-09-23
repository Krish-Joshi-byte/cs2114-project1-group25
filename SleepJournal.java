import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Owns the collection of SleepEntry objects. Responsible only for
 * storing and retrieving entries — no calculation logic belongs here
 * (see SleepStatsCalculator).
 *
 * Core structure: ArrayList<SleepEntry>. Entries are appended in
 * chronological order and read far more often than removed/reordered,
 * so ArrayList's cheap iteration and indexed access fit better than
 * a LinkedList or a Map keyed by date.
 */
public class SleepJournal {

    private final List<SleepEntry> entries = new ArrayList<>();

    /**
     * Appends a validated entry to the internal list.
     * @param entry the SleepEntry to add
     * @throws IllegalArgumentException if entry is null.
     */
    public void addEntry(SleepEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("entry cannot be null");
        }
        entries.add(entry);
    }

    /**
     * Returns an unmodifiable view of all logged entries, oldest first.
     * @return an unmodifiable list of all logged entries, oldest first
    */
    public List<SleepEntry> getAllEntries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Returns entries whose date falls within [start, end], inclusive.
     * If start is after end, returns an empty list.
     * @param start the first date to include (inclusive)
     * @param end the last date to include (inclusive)
     * @return a list of entries in the specified date range, oldest first
     */
    public List<SleepEntry> getEntriesInRange(LocalDate start, LocalDate end) {
        List<SleepEntry> result = new ArrayList<>();
        if (start == null || end == null || start.isAfter(end)) {
            return result;
        }
        for (SleepEntry e : entries) {
            LocalDate d = e.getDate();
            if (!d.isBefore(start) && !d.isAfter(end)) {
                result.add(e);
            }
        }
        return result;
    }

    /** Returns the number of entries logged so far. */
    public int size() {
        return entries.size();
    }
}
