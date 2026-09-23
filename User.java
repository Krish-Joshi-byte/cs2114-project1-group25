/**
 * Represents the person using the app. Owns that person's sleep goal
 * and their SleepJournal. Holds data only — no calculation or
 * validation logic belongs here.
 *
 * Note: journal is composed here (created in the constructor, never
 * shared with another User) rather than passed in, since a journal
 * has no meaning outside the user it belongs to.
 * 
 * @author Krish Joshi
 * @version 23.09.2026
 */
public class User {

    private final String name;
    private double goalHours;
    private final SleepJournal journal;

    public User(String name, double goalHours) {
        this.name = name;
        this.goalHours = goalHours;
        this.journal = new SleepJournal();
    }

    public String getName() {
        return name;
    }

    /** 
     * @return the user's sleep goal in hours (e.g. 8.0).
     */
    public double getGoalHours() {
        return goalHours;
    }

    /**
     * Updates the user's sleep goal. Caller (SleepTrackerApp) should
     * validate the new value with InputValidator.isValidHours(...)
     * before calling this.
     */
    public void setGoalHours(double goalHours) {
        this.goalHours = goalHours;
    }

    /** Returns this user's SleepJournal. */
    public SleepJournal getJournal() {
        return journal;
    }
}
