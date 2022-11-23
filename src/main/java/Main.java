import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {


    public static void main(String[] args) throws FileNotFoundException {
        WebController controller = new WebController(2816);

        controller.openPage();

        List<TeamStats> stats = controller.getStats();

        List<String[]> outputData = new ArrayList<>();

        outputData.add(new String[]{"team", "ppg", "powers/game", "gets/game", "ppb"});

        for(TeamStats s : stats) {
            outputData.add(
                    new String[]{
                            s.getName(),
                            String.valueOf(s.getPpg()),
                            String.valueOf(s.getPowersPerGame()),
                            String.valueOf(s.getsPerGame()),
                            String.valueOf(s.getPpb())
                    }
            );
        }

        writeToFile(outputData);

    }

    public static String convertToCSV(String[] data) {
        return Stream.of(data)
                .collect(Collectors.joining(","));
    }

    public static void writeToFile(List<String[]> data) throws FileNotFoundException {
        File csvOutputFile = new File("C:\\Users\\barta\\test.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            data.stream()
                    .map((e) -> convertToCSV(e))
                    .forEach(pw::println);
        }
    }

}
