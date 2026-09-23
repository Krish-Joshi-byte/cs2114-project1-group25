import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Plain data holder for one night's sleep. Holds data only —
 * no validation and no statistics logic belong here.
 *
 * Callers (SleepTrackerApp) are responsible for validating inputs
 * with InputValidator BEFORE calling this constructor — this class
 * assumes wakeTime is already known to be after bedtime.
 */
public class SleepEntry {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd  h:mm a");

    private final LocalDateTime bedtime;
    private final LocalDateTime wakeTime;
    private final double durationHours;
    private final int qualityRating;   // 1-5
    private final int wakeCount;
    private final String notes;        // optional, may be null/empty

    /**
     * Constructs a SleepEntry with the given data. Assumes all inputs
     * have already been validated (bedtime < wakeTime, qualityRating
     * in [1,5], wakeCount >= 0).
     * @param bedtime the time the user went to bed
     * @param wakeTime the time the user woke up
     * @param qualityRating the self-reported quality rating (1-5)
     * @param wakeCount the number of times the user woke up during the night
     * @param notes optional notes about the sleep session (may be null/empty)
     */
    public SleepEntry(LocalDateTime bedtime, LocalDateTime wakeTime,
                       int qualityRating, int wakeCount, String notes) {
        this.bedtime = bedtime;
        this.wakeTime = wakeTime;
        this.qualityRating = qualityRating;
        this.wakeCount = wakeCount;
        this.notes = notes;

        long minutes = Duration.between(bedtime, wakeTime).toMinutes();
        this.durationHours = minutes / 60.0;
    }

    /**
     * Returns the time the user went to bed.
     * @return the bedtime
     */
    public LocalDateTime getBedtime() {
        return bedtime;
    }

    /** Returns the time the user woke up. 
     * @return the wake time
    */
    public LocalDateTime getWakeTime() {
        return wakeTime;
    }

    /** Returns the pre-computed sleep duration in hours. 
     * @return the sleep duration in hours
    */
    public double getDurationHours() {
        return durationHours;
    }

    /** Returns the 1-5 self-reported quality rating. 
     * @return the quality rating
    */
    public int getQualityRating() {
        return qualityRating;
    }

    /** Returns how many times the user reported waking up. 
     * @return the number of times the user woke up
    */
    public int getWakeCount() {
        return wakeCount;
    }

    /** Returns the optional notes about the sleep session. 
     * @return the notes (may be null/empty)
    */
    public String getNotes() {
        return notes;
    }

    /**
     * Returns the calendar date this sleep session is attributed to
     * (the bedtime's date) — used for weekday/weekend classification.
     * @return the date of the sleep session (bedtime's date)
     */
    public LocalDate getDate() {
        return bedtime.toLocalDate();
    }

    /** Returns true if getDate() falls on Saturday or Sunday. 
     * @return true if the sleep session is on a weekend, false otherwise
    */
    public boolean isWeekend() {
        DayOfWeek day = getDate().getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    /**
     * Returns a human-readable string representation of this SleepEntry.
     * @return a string representation of this SleepEntry
     */
    @Override
    public String toString() {
        String noteText = (notes == null || notes.isBlank()) ? "" : "  notes: " + notes;
        return String.format("%s -> %s  (%.1f hrs, quality %d/5, woke %d time%s)%s",
                bedtime.format(DISPLAY_FORMAT),
                wakeTime.format(DISPLAY_FORMAT),
                durationHours,
                qualityRating,
                wakeCount,
                wakeCount == 1 ? "" : "s",
                noteText);
    }
}
