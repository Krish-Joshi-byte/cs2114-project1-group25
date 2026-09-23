import java.time.LocalDateTime;

/**
 * Tests for InputValidator. Covers the normal and bad/boundary cases
 *
 * @author Alper
 * @version 2026.09.22
 */
public class InputValidatorTest
        extends student.TestCase {

    private static final double DELTA = 0.0001;

    private LocalDateTime bedtime;
    private LocalDateTime wakeTime;

    /**
     * Sets up a 10 PM bedtime and a 6 AM wake time the next day.
     */
    public void setUp() {
        bedtime = LocalDateTime.of(2026, 9, 21, 22, 0);
        wakeTime = LocalDateTime.of(2026, 9, 22, 6, 0);
    }

    /**
     * Asserts that parseHours rejects the given input.
     *
     * @param raw
     *            the input that should be rejected
     */
    private void assertRejected(String raw) {
        Exception thrown = null;
        try {
            InputValidator.parseHours(raw);
        }
        catch (NumberFormatException e) {
            thrown = e;
        }
        assertNotNull("expected \"" + raw + "\" to be rejected", thrown);
    }

    /**
     * Tests parseHours with well-formed numbers.
     */
    public void testParseHoursValid() {
        assertEquals(8.5, InputValidator.parseHours("8.5"), DELTA);
        assertEquals(8.0, InputValidator.parseHours("8"), DELTA);
        assertEquals(7.0, InputValidator.parseHours("  7  "), DELTA);
        assertEquals(0.5, InputValidator.parseHours(".5"), DELTA);
        assertEquals(8.0, InputValidator.parseHours("8."), DELTA);
        assertEquals(10.0, InputValidator.parseHours("1e1"), DELTA);
    }

    /**
     * Tests that parseHours only checks the format: negative and huge
     * numbers are parsed, and isValidHours rejects them afterwards.
     */
    public void testParseHoursOutOfRangeStillParses() {
        assertEquals(-2.0, InputValidator.parseHours("-2"), DELTA);
        assertEquals(1000000.0, InputValidator.parseHours("1000000"), DELTA);
        assertEquals(3.0, InputValidator.parseHours("+3"), DELTA);
    }

    /**
     * Tests that parseHours rejects malformed input.
     */
    public void testParseHoursMalformed() {
        assertRejected("eight");
        assertRejected("");
        assertRejected("   ");
        assertRejected(null);
        assertRejected("8 hours");
        assertRejected("7,5");
        assertRejected(".");
        assertRejected("-");
    }

    /**
     * Tests that parseHours rejects special values Double.parseDouble
     * would otherwise accept.
     */
    public void testParseHoursSpecialValues() {
        assertRejected("NaN");
        assertRejected("Infinity");
        assertRejected("8d");
        assertRejected("8f");
        assertRejected("0x1p3");
    }

    /**
     * Tests isValidHours inside, on, and outside the 0-24 range.
     */
    public void testIsValidHours() {
        assertTrue(InputValidator.isValidHours(8.0));
        assertTrue(InputValidator.isValidHours(0.0));
        assertTrue(InputValidator.isValidHours(24.0));
        assertFalse(InputValidator.isValidHours(-2));
        assertFalse(InputValidator.isValidHours(-0.01));
        assertFalse(InputValidator.isValidHours(24.01));
        assertFalse(InputValidator.isValidHours(1000000));
        assertFalse(InputValidator.isValidHours(Double.NaN));
        assertFalse(InputValidator.isValidHours(Double.POSITIVE_INFINITY));
    }

    /**
     * Tests isValidGoalHours: the goal must be above 0 and at most 24.
     */
    public void testIsValidGoalHours() {
        assertTrue(InputValidator.isValidGoalHours(8.0));
        assertTrue(InputValidator.isValidGoalHours(0.5));
        assertTrue(InputValidator.isValidGoalHours(24.0));
        assertFalse(InputValidator.isValidGoalHours(0.0));
        assertFalse(InputValidator.isValidGoalHours(-1.0));
        assertFalse(InputValidator.isValidGoalHours(24.5));
        assertFalse(InputValidator.isValidGoalHours(Double.NaN));
    }

    /**
     * Tests isValidQuality inside, on, and outside the 1-5 scale.
     */
    public void testIsValidQuality() {
        assertTrue(InputValidator.isValidQuality(4));
        assertTrue(InputValidator.isValidQuality(1));
        assertTrue(InputValidator.isValidQuality(5));
        assertFalse(InputValidator.isValidQuality(6));
        assertFalse(InputValidator.isValidQuality(0));
        assertFalse(InputValidator.isValidQuality(-3));
    }

    /**
     * Tests isValidTimeOrder: 10 PM to 6 AM the next day is valid.
     */
    public void testIsValidTimeOrderValid() {
        assertTrue(InputValidator.isValidTimeOrder(bedtime, wakeTime));
        assertTrue(InputValidator.isValidTimeOrder(
                bedtime, bedtime.plusMinutes(1)));
    }

    /**
     * Tests isValidTimeOrder with a wake time before or equal to the
     * bedtime, and with missing times.
     */
    public void testIsValidTimeOrderInvalid() {
        assertFalse(InputValidator.isValidTimeOrder(wakeTime, bedtime));
        assertFalse(InputValidator.isValidTimeOrder(bedtime, bedtime));
        assertFalse(InputValidator.isValidTimeOrder(null, wakeTime));
        assertFalse(InputValidator.isValidTimeOrder(bedtime, null));
        assertFalse(InputValidator.isValidTimeOrder(null, null));
    }
}
// --