public class Sleep {
    public static void main(String[] args) {
        try {
            System.out.println("Sleeping for 5 seconds...");
            Thread.sleep(5000); // Sleep for 5 seconds
            System.out.println("Awake now!");
        } catch (InterruptedException e) {
            System.out.println("Sleep was interrupted.");
        }
    }
}
