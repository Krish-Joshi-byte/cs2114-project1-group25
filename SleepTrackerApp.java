import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Front end for the Sleep Tracker. This class controls program flow, it
 * sets up the user, shows the menu, prompts for sleep entries and displays
 * history, statistics and recommendations
 *
 * It does not do calculations or store data itself. Instead it coordinates
 * the other classes
 *
 * - InputValidator checks every value the user types. When a value is
 * rejected, this class shows an error and asks again, so invalid data is
 * never stored.
 * - User / SleepJournal store the entries.
 * - SleepStatsCalculator computes the statistics.
 * - RecommendationEngine turns the statistics into advice.
 *
 * @author Alper
 * @version 2026.09.22
 */
public class SleepTrackerApp {

    /** Accepts "2026-09-21 22:30" or "2026-09-22 6:15". */
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd H:mm");

    private static final String DATE_TIME_EXAMPLE = "2026-09-21 22:30";

    /** More wake-ups than this in one night is treated as unreasonable. */
    private static final int MAX_WAKE_COUNT = 50;

    /** The scope asks users to log at least a week of sleep. */
    private static final int RECOMMENDED_MIN_ENTRIES = 7;

    /** Length of the "last week" and "last month" averages, in days. */
    private static final int WEEK_DAYS = 7;
    private static final int MONTH_DAYS = 30;

    private static final int MENU_LOG_ENTRY = 1;
    private static final int MENU_HISTORY = 2;
    private static final int MENU_STATS = 3;
    private static final int MENU_CHANGE_GOAL = 4;
    private static final int MENU_QUIT = 5;

    private User currentUser;
    private SleepStatsCalculator stats;
    private RecommendationEngine recommender;
    private Scanner scanner;

    /**
     * Creates an app that reads from the keyboard (System.in).
     */
    public SleepTrackerApp() {
        this(new Scanner(System.in));
    }

    /**
     * Creates an app that reads from the given scanner. run() will ask for
     * the user's name and sleep goal before showing the menu.
     *
     * @param scanner
     *            where user input is read from
     */
    public SleepTrackerApp(Scanner scanner) {
        this.scanner = scanner;
        this.recommender = new RecommendationEngine();
    }

    /**
     * Creates an app for a user that already exists. Useful for testing the
     * individual screens without going through the setup prompts.
     *
     * @param scanner
     *            where user input is read from
     * @param user
     *            the user whose journal the app works with
     */
    public SleepTrackerApp(Scanner scanner, User user) {
        this(scanner);
        setCurrentUser(user);
    }

    /**
     * Starts the Sleep Tracker from the command line.
     *
     * @param args
     *            not used
     */
    public static void main(String[] args) {
        new SleepTrackerApp().run();
    }

    /**
     * Returns the user the app is currently working with.
     *
     * @return the current user, or null if no user has been set up yet
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Starts the application and controls the main program flow: sets up
     * the user if needed, then shows the menu until the user quits or the
     * input runs out.
     */
    public void run() {
        System.out.println("=== Sleep Tracker ===");
        try {
            if (currentUser == null) {
                createUser();
            }
            boolean running = true;
            while (running) {
                printMenu();
                int choice = promptForInt(
                        "Choose an option (" + MENU_LOG_ENTRY + "-" + MENU_QUIT
                                + "): ",
                        MENU_LOG_ENTRY,
                        MENU_QUIT);
                switch (choice) {
                    case MENU_LOG_ENTRY:
                        promptForEntry();
                        break;
                    case MENU_HISTORY:
                        displayHistory();
                        break;
                    case MENU_STATS:
                        displayStatsAndRecommendations();
                        break;
                    case MENU_CHANGE_GOAL:
                        promptForGoalChange();
                        break;
                    default:
                        running = false;
                        break;
                }
            }
        }
        catch (InputClosedException e) {
            System.out.println();
            System.out.println("No more input.");
        }
        System.out.println("Goodbye - sleep well!");
    }

    /**
     * Prompts the user for sleep information and creates/stores a valid
     * entry. Each value is re-prompted until it passes validation; if the
     * input ends part way through, nothing is stored.
     */
    public void promptForEntry() {
        if (!hasUser()) {
            return;
        }
        try {
            System.out.println();
            System.out.println("--- Log a sleep entry ---");
            System.out.println(
                    "Enter times as yyyy-MM-dd HH:mm, e.g. " + DATE_TIME_EXAMPLE);

            LocalDateTime bedtime;
            LocalDateTime wakeTime;
            do {
                bedtime = promptForDateTime("Bedtime: ");
                wakeTime = promptForDateTime("Wake time: ");
            }
            while (!isValidSleepWindow(bedtime, wakeTime));

            int quality = promptForQuality();
            int wakeCount = promptForInt(
                    "How many times did you wake up during the night? ",
                    0,
                    MAX_WAKE_COUNT);
            System.out.print("Notes (caffeine, exercise, screen time, naps)"
                    + " - press Enter to skip: ");
            String notes = readLine().trim();

            SleepEntry entry =
                    new SleepEntry(bedtime, wakeTime, quality, wakeCount, notes);
            currentUser.getJournal().addEntry(entry);
            System.out.println("Saved " + formatHours(entry.getDurationHours())
                    + " hours of sleep for " + entry.getDate() + ".");
        }
        catch (InputClosedException e) {
            System.out.println();
            System.out.println("Input ended - the entry was not saved.");
        }
    }

    /**
     * Displays the user's recorded sleep entries, one line per night.
     */
    public void displayHistory() {
        if (!hasUser()) {
            return;
        }
        List<SleepEntry> entries = currentUser.getJournal().getAllEntries();
        System.out.println();
        if (entries.isEmpty()) {
            System.out.println("No sleep entries recorded yet.");
            return;
        }
        System.out.println("--- Sleep history (" + entries.size()
                + (entries.size() == 1 ? " entry" : " entries") + ") ---");
        System.out.println(String.format("%3s  %-10s  %-3s  %6s  %7s  %8s",
                "#", "Date", "Day", "Hours", "Quality", "Wake-ups"));
        int number = 1;
        for (SleepEntry entry : entries) {
            String day = entry.getDate().getDayOfWeek().toString().substring(0, 3);
            day = day.substring(0, 1)
                + day.substring(1).toLowerCase(Locale.US);
            System.out.println(String.format(
                    "%3d  %-10s  %-3s  %6s  %5d/%d  %8d%s",
                    number,
                    entry.getDate(),
                    day,
                    formatHours(entry.getDurationHours()),
                    entry.getQualityRating(),
                    InputValidator.MAX_QUALITY,
                    entry.getWakeCount(),
                    entry.isWeekend() ? "  (weekend)" : ""));
            number++;
        }
    }

    /**
     * Calculates statistics and displays related recommendations. If no
     * entries have been logged yet, tells the user more entries are needed
     * instead of showing statistics.
     */
    public void displayStatsAndRecommendations() {
        if (!hasUser()) {
            return;
        }
        List<SleepEntry> entries = currentUser.getJournal().getAllEntries();
        System.out.println();
        if (entries.isEmpty()) {
            System.out.println("No sleep entries yet - log at least one "
                    + "night to see statistics and recommendations.");
            return;
        }

        System.out.println("--- Sleep statistics ---");
        System.out.println("Nights logged: " + entries.size());
        if (entries.size() < RECOMMENDED_MIN_ENTRIES) {
            System.out.println("(Log at least " + RECOMMENDED_MIN_ENTRIES
                    + " nights for a reliable weekly picture.)");
        }
        System.out.println("Average sleep (all nights): "
                + describeHours(stats.averageDuration(entries)));
        LocalDate latest = latestDate(entries);
        printRangeAverage("last 7 days", latest, WEEK_DAYS);
        printRangeAverage("last 30 days", latest, MONTH_DAYS);
        System.out.println("Longest night: "
                + describeNight(stats.longestNight()));
        System.out.println("Shortest night: "
                + describeNight(stats.shortestNight()));

        double goal = currentUser.getGoalHours();
        double debt = stats.sleepDebt();
        System.out.print("Sleep debt vs. your " + formatHours(goal)
                + "-hour goal: ");
        // A negative debt means the user sleeps more than the goal, so only
        // NaN counts as "no data" here.
        if (Double.isNaN(debt)) {
            System.out.println("no data");
        }
        else if (debt > 0) {
            System.out.println(formatHours(debt) + " hours");
        }
        else {
            System.out.println("none - you are meeting your goal");
        }

        // Count the nights ourselves so a category with no nights always
        // shows "no data", whatever value the calculator returns for it.
        int weekendNights = 0;
        for (SleepEntry entry : entries) {
            if (entry.isWeekend()) {
                weekendNights++;
            }
        }
        int weekdayNights = entries.size() - weekendNights;
        System.out.println("weekday average: "
            + (weekdayNights == 0
                ? "no data"
                : describeHours(stats.weekdayAverage())));
        System.out.println("weekend average: "
            + (weekendNights == 0
                ? "no data"
                : describeHours(stats.weekendAverage())));

        System.out.println();
        System.out.println("--- Recommendations ---");
        List<String> recommendations =
                recommender.generateRecommendations(stats);
        if (recommendations == null || recommendations.isEmpty()) {
            System.out.println("No recommendations right now.");
        }
        else {
            for (String recommendation : recommendations) {
                System.out.println("- " + recommendation);
            }
        }
    }

    /**
     * Finds the date of the most recent entry.
     *
     * @param entries
     *            a non-empty list of entries
     * @return the latest entry date
     */
    private static LocalDate latestDate(List<SleepEntry> entries) {
        LocalDate latest = entries.get(0).getDate();
        for (SleepEntry entry : entries) {
            if (entry.getDate().isAfter(latest)) {
                latest = entry.getDate();
            }
        }
        return latest;
    }

    /**
     * Prints the average sleep for the given number of days ending on (and
     * including) the end date, using the journal's date-range lookup.
     *
     * @param label
     *            e.g. "last 7 days"
     * @param end
     *            the last day of the range
     * @param days
     *            how many days the range covers
     */
    private void printRangeAverage(String label, LocalDate end, int days) {
        LocalDate start = end.minusDays(days - 1);
        List<SleepEntry> inRange =
                currentUser.getJournal().getEntriesInRange(start, end);
        String average = "no data";
        if (inRange != null && !inRange.isEmpty()) {
            average = describeHours(stats.averageDuration(inRange))
                    + " over " + inRange.size()
                    + (inRange.size() == 1 ? " night" : " nights");
        }
        System.out.println("Average, " + label + " (" + start + " to " + end
                + "): " + average);
    }

    // Setup and menu helpers

    /**
     * Asks for a name and sleep goal, then creates the user. Validation
     * happens here, before the User is created.
     */
    private void createUser() {
        String name = "";
        while (name.isEmpty()) {
            System.out.print("What is your name? ");
            name = readLine().trim();
            if (name.isEmpty()) {
                System.out.println("Please enter a name.");
            }
        }
        double goal = promptForGoal(
                "How many hours of sleep do you aim for each night? (e.g. 8) ");
        setCurrentUser(new User(name, goal));
        System.out.println("Welcome, " + name + "! Your goal is "
                + formatHours(goal) + " hours per night.");
    }

    /**
     * Points the app, and the statistics calculator, at the given user.
     *
     * @param user
     *            the user to work with
     */
    private void setCurrentUser(User user) {
        currentUser = user;
        stats = new SleepStatsCalculator(user);
    }

    /**
     * Prints a message if there is no user yet.
     *
     * @return true if a user has been set up
     */
    private boolean hasUser() {
        if (currentUser == null) {
            System.out.println("Please set up a user first.");
            return false;
        }
        return true;
    }

    /**
     * Prints the main menu.
     */
    private void printMenu() {
        System.out.println();
        System.out.println(MENU_LOG_ENTRY + ") Log a night of sleep");
        System.out.println(MENU_HISTORY + ") View sleep history");
        System.out.println(MENU_STATS + ") View statistics and recommendations");
        System.out.println(MENU_CHANGE_GOAL + ") Change sleep goal (currently "
                + formatHours(currentUser.getGoalHours()) + " hours)");
        System.out.println(MENU_QUIT + ") Quit");
    }

    /**
     * Asks for a new nightly sleep goal and updates the user.
     */
    private void promptForGoalChange() {
        try {
            double goal = promptForGoal("New nightly sleep goal in hours: ");
            currentUser.setGoalHours(goal);
            System.out.println("Your goal is now "
                    + formatHours(currentUser.getGoalHours()) + " hours.");
        }
        catch (InputClosedException e) {
            System.out.println();
            System.out.println("Input ended - your goal was not changed.");
        }
    }

    // Prompting and re-prompting

    /**
     * Reads one line of input.
     *
     * @return the line the user typed
     * @throws InputClosedException
     *             if there is no more input
     */
    private String readLine() {
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        return scanner.nextLine();
    }

    /**
     * Prompts for a nightly sleep goal until the user enters a number that
     * is more than 0 and at most 24. Words and out-of-range numbers get
     * different messages.
     *
     * @param prompt
     *            the question to show
     * @return a valid sleep goal in hours
     */
    private double promptForGoal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = readLine();
            double goal;
            try {
                goal = InputValidator.parseHours(raw);
            }
            catch (NumberFormatException e) {
                System.out.println(
                        "A number is needed - please enter hours like 8 or 7.5.");
                continue;
            }
            if (InputValidator.isValidGoalHours(goal)) {
                return goal;
            }
            System.out.println(formatHours(goal) + " hours is not a valid "
                    + "goal - please enter a number greater than "
                    + formatHours(InputValidator.MIN_HOURS) + " and no more than "
                    + formatHours(InputValidator.MAX_HOURS) + ".");
        }
    }

    /**
     * Prompts for a date and time until one in the expected format is
     * entered.
     *
     * @param prompt
     *            the question to show
     * @return the date and time entered
     */
    private LocalDateTime promptForDateTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = readLine().trim();
            try {
                return LocalDateTime.parse(raw, DATE_TIME_FORMAT);
            }
            catch (DateTimeParseException e) {
                System.out.println("\"" + raw + "\" is not a valid date and "
                        + "time - use yyyy-MM-dd HH:mm, e.g. " + DATE_TIME_EXAMPLE
                        + ".");
            }
        }
    }

    /**
     * Checks that the wake time is after the bedtime and that the night is
     * no longer than 24 hours, printing an error if not.
     *
     * @param bedtime
     *            when the user went to bed
     * @param wakeTime
     *            when the user woke up
     * @return true if the times can be stored
     */
    private boolean isValidSleepWindow(
            LocalDateTime bedtime,
            LocalDateTime wakeTime) {
        if (!InputValidator.isValidTimeOrder(bedtime, wakeTime)) {
            System.out.println("Wake time must be after bedtime. "
                    + "Please enter both times again.");
            return false;
        }
        double hours = Duration.between(bedtime, wakeTime).toMinutes() / 60.0;
        if (!InputValidator.isValidHours(hours)) {
            System.out.println(formatHours(hours) + " hours of sleep is not "
                    + "reasonable (maximum " + formatHours(InputValidator.MAX_HOURS)
                    + "). Please enter both times again.");
            return false;
        }
        return true;
    }

    /**
     * Prompts for a sleep quality rating until a whole number from 1 to 5 is
     * entered.
     *
     * @return a valid quality rating
     */
    private int promptForQuality() {
        String range =
                InputValidator.MIN_QUALITY + "-" + InputValidator.MAX_QUALITY;
        while (true) {
            System.out.print("Sleep quality (" + range + ", "
                    + InputValidator.MAX_QUALITY + " = best): ");
            String raw = readLine().trim();
            int rating;
            try {
                rating = Integer.parseInt(raw);
            }
            catch (NumberFormatException e) {
                System.out.println("A whole number is needed - please enter "
                        + range + ".");
                continue;
            }
            if (InputValidator.isValidQuality(rating)) {
                return rating;
            }
            System.out.println(
                    "Quality must be from " + InputValidator.MIN_QUALITY + " to "
                            + InputValidator.MAX_QUALITY + ". Please try again.");
        }
    }

    /**
     * Prompts for a whole number until one between min and max (inclusive)
     * is entered.
     *
     * @param prompt
     *            the question to show
     * @param min
     *            smallest accepted value
     * @param max
     *            largest accepted value
     * @return a valid whole number
     */
    private int promptForInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String raw = readLine().trim();
            int value;
            try {
                value = Integer.parseInt(raw);
            }
            catch (NumberFormatException e) {
                System.out.println("A whole number is needed.");
                continue;
            }
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Please enter a number from " + min + " to "
                    + max + ".");
        }
    }

    // Formatting

    /**
     * Formats a number of hours with two decimal places.
     *
     * @param hours
     *            the value to format
     * @return e.g. "7.50"
     */
    private static String formatHours(double hours) {
        return String.format("%.2f", hours);
    }

    /**
     * Whether a statistic is a "no data" result (null, NaN or negative).
     *
     * @param value
     *            the statistic
     * @return true if there is no usable value
     */
    private static boolean isNoData(Double value) {
        return value == null || value.isNaN() || value < 0;
    }

    /**
     * Describes an average number of hours, or "no data".
     *
     * @param hours
     *            the average, possibly a no-data value
     * @return e.g. "7.50 hours"
     */
    private static String describeHours(Double hours) {
        if (isNoData(hours)) {
            return "no data";
        }
        return formatHours(hours) + " hours";
    }

    /**
     * Describes a single night, or "no data" if there is none.
     *
     * @param entry
     *            the entry, possibly null
     * @return e.g. "9.00 hours on 2026-09-20"
     */
    private static String describeNight(SleepEntry entry) {
        if (entry == null) {
            return "no data";
        }
        return formatHours(entry.getDurationHours()) + " hours on "
                + entry.getDate();
    }

    /**
     * Signals that the input stream has ended, so the app can stop cleanly
     * instead of looping forever or crashing.
     */
    private static class InputClosedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
// --
