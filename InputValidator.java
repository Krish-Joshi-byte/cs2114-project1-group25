import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Performs various types of checks for values the user types into the Sleep
 * Tracker. This is a utility class, hence every method is static. Class is
 * used briefly without creating an Object.
 *
 * InputValidator only answers whether the a value is acceptable or not. All
 * other parts are handled by other classes.
 *
 * @author Alper
 * @version 2026.09.22
 */
public class InputValidator {

    // Smallest accepted number of sleep hours
    public static final double MIN_HOURS = 0.0;

    // Largest accepted number of sleep hours
    public static final double MAX_HOURS = 24.0;

    // Lowest sleep quality rating on the 1-5 scale
    public static final int MIN_QUALITY = 1;

    // Highest sleep quality rating on the 1-5 scale
    public static final int MAX_QUALITY = 5;

    /**
     * A decimal number such as "8", "7.5", ".5" or "-2". Rejects words
     * ("eight") and other forms
     */
    private static final Pattern NUMBER = Pattern.compile(
            "[+-]?(\\d+(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?");

    /**
     * Comverts raw hours input into a numeric value. Whitespace ignored.
     * This only checks format, not the number is in range
     *
     * @param raw
     *            the text the user typed, e.g. "8.5"
     * @return the numeric value, e.g. 8.5
     * @throws NumberFormatException
     *             if raw is null, empty, or not a number (e.g. "eight")
     */
    public static double parseHours(String raw) {
        if (raw == null) {
            throw new NumberFormatException("No hours value was entered.");
        }
        String trimmed = raw.trim();
        if (!NUMBER.matcher(trimmed).matches()) {
            throw new NumberFormatException(
                    "\"" + trimmed + "\" is not a number.");
        }
        return Double.parseDouble(trimmed);
    }

    /**
     * Checks whether a sleep-hours value is within the accepted range of
     * 0 to 24 hours inclusive
     *
     * @param hours
     *            the number of hours to check
     * @return true if 0 <= hours <= 24, false otherwise
     */
    public static boolean isValidHours(double hours) {
        return hours >= MIN_HOURS && hours <= MAX_HOURS;
    }

    /**
     * Checks whether a nightly sleep goal is acceptable. A goal must be more
     * than 0 hours (a goal of 0 is not meaningful) and at most 24 hours.
     *
     * @param goalHours
     *            the goal to check
     * @return true if 0 < goalHours <= 24, false otherwise
     */
    public static boolean isValidGoalHours(double goalHours) {
        return goalHours > MIN_HOURS && goalHours <= MAX_HOURS;
    }

    /**
     * Checks whether a quality rating is within the accepted 1-5 scale
     *
     * @param rating
     *            the rating to check
     * @return true if 1 <= rating <= 5, false otherwise
     */
    public static boolean isValidQuality(int rating) {
        return rating >= MIN_QUALITY && rating <= MAX_QUALITY;
    }

    /**
     * Checks whether the recorded bedtime and wake time are in a valid
     * order, meaning the wake time is strictly after the bedtime.
     *
     * @param bedtime
     *            when the user went to bed
     * @param wakeTime
     *            when the user woke up
     * @return true if both times are present and wakeTime is after bedtime,
     *         false otherwise
     */
    public static boolean isValidTimeOrder(
            LocalDateTime bedtime,
            LocalDateTime wakeTime) {
        if (bedtime == null || wakeTime == null) {
            return false;
        }
        return wakeTime.isAfter(bedtime);
    }
}
// --
