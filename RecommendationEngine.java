import java.util.ArrayList;
import java.util.List;

public class RecommendationEngine {

    /**
     * Generates recommendations based on the user's sleep statistics.
     *
     * @param stats the user's sleep statistics
     * @return a list of sleep recommendations
     */
    public List<String> generateRecommendations(SleepStatsCalculator stats) {
        List<String> recommendations = new ArrayList<>();

        double averageSleep = stats.weekdayAverage();

        if (averageSleep < 6.0) {
            recommendations.add(
                "You are getting less than 6 hours of sleep on average."
            );

            recommendations.add(
                "Consistently getting too little sleep may affect " +
                "concentration, alertness, and memory."
            );
        }
        else if (averageSleep <= 8.0) {
            recommendations.add(
                "Your average sleep is between 6 and 8 hours."
            );

            recommendations.add(
                "This is within the target range used by this sleep tracker. " +
                "Continue maintaining a consistent sleep schedule."
            );
        }
        else {
            recommendations.add(
                "You are getting more than 8 hours of sleep on average."
            );

            recommendations.add(
                "Consistently sleeping for unusually long periods may sometimes " +
                "be associated with daytime tiredness or other health concerns."
            );
        }

        double weekendSleep = stats.weekendAverage();
        
        if (weekendSleep > 0.0 && weekendSleep < 6.0) {
            recommendations.add(
                "You are getting less than 6 hours of sleep on weekends."
            );
        }
        else if (weekendSleep > 8.0) {
            recommendations.add(
                "You are sleeping more than 8 hours on weekends."
            );
        }
        else if (weekendSleep > 0.0) {
            recommendations.add(
                "Your weekend sleep average is within the target range."
            );
        }

        return recommendations;
    }
}
