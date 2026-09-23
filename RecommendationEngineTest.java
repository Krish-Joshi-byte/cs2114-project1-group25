import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class RecommendationEngineTest {

    @Test
    public void testUnderSixHours() {
        SleepStatsCalculator stats = new SleepStatsCalculator();
        stats.setAverageSleep(5.5);

        RecommendationEngine engine = new RecommendationEngine();

        List<String> result = engine.generateRecommendations(stats);

        assertEquals(2, result.size());

        assertEquals(
            "You are getting less than 6 hours of sleep on average.",
            result.get(0)
        );

        assertEquals(
            "Consistently getting too little sleep may affect " +
            "concentration, alertness, and memory.",
            result.get(1)
        );
    }

    @Test
    public void testExactlySixHours() {
        SleepStatsCalculator stats = new SleepStatsCalculator();
        stats.setAverageSleep(6.0);

        RecommendationEngine engine = new RecommendationEngine();

        List<String> result = engine.generateRecommendations(stats);

        assertEquals(2, result.size());

        assertEquals(
            "Your average sleep is between 6 and 8 hours.",
            result.get(0)
        );

        assertEquals(
            "This is within the target range used by this sleep tracker. " +
            "Continue maintaining a consistent sleep schedule.",
            result.get(1)
        );
    }

    @Test
    public void testBetweenSixAndEightHours() {
        SleepStatsCalculator stats = new SleepStatsCalculator();
        stats.setAverageSleep(7.0);

        RecommendationEngine engine = new RecommendationEngine();

        List<String> result = engine.generateRecommendations(stats);

        assertEquals(2, result.size());

        assertEquals(
            "Your average sleep is between 6 and 8 hours.",
            result.get(0)
        );

        assertEquals(
            "This is within the target range used by this sleep tracker. " +
            "Continue maintaining a consistent sleep schedule.",
            result.get(1)
        );
    }

    @Test
    public void testExactlyEightHours() {
        SleepStatsCalculator stats = new SleepStatsCalculator();
        stats.setAverageSleep(8.0);

        RecommendationEngine engine = new RecommendationEngine();

        List<String> result = engine.generateRecommendations(stats);

        assertEquals(2, result.size());

        assertEquals(
            "Your average sleep is between 6 and 8 hours.",
            result.get(0)
        );

        assertEquals(
            "This is within the target range used by this sleep tracker. " +
            "Continue maintaining a consistent sleep schedule.",
            result.get(1)
        );
    }

    @Test
    public void testOverEightHours() {
        SleepStatsCalculator stats = new SleepStatsCalculator();
        stats.setAverageSleep(9.0);

        RecommendationEngine engine = new RecommendationEngine();

        List<String> result = engine.generateRecommendations(stats);

        assertEquals(2, result.size());

        assertEquals(
            "You are getting more than 8 hours of sleep on average.",
            result.get(0)
        );

        assertEquals(
            "Consistently sleeping for unusually long periods may sometimes " +
            "be associated with daytime tiredness or other health concerns.",
            result.get(1)
        );
    }
}