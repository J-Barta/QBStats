import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    //TODO: Truncate or round stats
    //TODO: Get chrome driver properly
    //TODO: Make sure concatenation across multiple stat sheets works properly
    public static void main(String[] args) throws FileNotFoundException {
        Scanner input = new Scanner(System.in);
        System.out.println("Please enter the ID of the question set from hsquizbowl.org");
        int setID = Integer.valueOf(input.nextLine());

        WebController controller = new WebController(setID);

        System.out.println("Starting browser...");

        String setName = controller.openPage();

        System.out.println("Retrieving stats for: " + setName);

        List<TeamStats> stats = controller.getStats();

        List<String[]> outputData = new ArrayList<>();

        outputData.add(new String[]{"team", "ppg", "powers/game", "gets/game", "ppb"});

        for(TeamStats s : stats) {
            outputData.add(
                    new String[]{
                            s.getName(),
                            String.valueOf(round(s.getPpg(), 1)),
                            String.valueOf(round(s.getPowersPerGame(), 1)),
                            String.valueOf(round(s.getsPerGame(), 1)),
                            String.valueOf(round(s.getPpb(), 1))
                    }
            );
        }

        writeToFile(outputData, setName);

    }

    public static String convertToCSV(String[] data) {
        return Stream.of(data)
                .collect(Collectors.joining(","));
    }

    public static void writeToFile(List<String[]> data, String fileName) throws FileNotFoundException {
        String filePath = "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\" + fileName + ".csv";
        File csvOutputFile = new File(filePath);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            data.stream()
                    .map((e) -> convertToCSV(e))
                    .forEach(pw::println);
        }
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
