package me.dcc;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    //TODO: Encapsulate functions more
    public static void main(String[] args) throws Exception {

        String userName = System.getProperty("user.name");

        //Copy file to desktop folder
        String driverPath = "C:\\Users\\" + userName + "\\Desktop\\QBStats\\";
        new File(driverPath).mkdirs();
        new File(driverPath + "chromedriver.exe").delete();
        boolean success = copy(Main.class.getResourceAsStream("/chromedriver.exe"), driverPath + "chromedriver.exe");

        if(!success) {
            System.out.println("Failed to copy chrome driver to the desktop!");
            System.out.println("If odd behavior occurs, restart your computer and try again.");
        } else System.out.println("Successfully copied chrome driver to the desktop.");

        Scanner input = new Scanner(System.in);
        System.out.println("Please enter the ID of the question set from hsquizbowl.org");
        int setID = Integer.valueOf(input.nextLine());

        WebController controller = new WebController(setID);

        System.out.println("Starting browser...");

        String setName = controller.openPage(driverPath + "chromedriver.exe");

        System.out.println("Retrieving stats for: " + setName);

        List<TeamStats> stats = controller.getStats();

        List<String[]> outputData = new ArrayList<>();

        outputData.add(new String[]{"Team", "PPG", "Powers/G", "Gets/G", "PPB"});

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

        writeToFile(outputData, driverPath, setName);

        System.out.println("csv file saved successfully!");

        controller.close();

        System.out.println("Chrome window closed. Goodbye.");

        //Delete chromedriver
        new File(driverPath + "chromedriver.exe").delete();
    }

    /**
     * Copy a file from source to destination.
     *
     * @param source
     *        the source
     * @param destination
     *        the destination
     * @return True if succeeded , False if not
     */
    public static boolean copy(InputStream source , String destination) {
        boolean succeess = true;


        try {
            Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            succeess = false;
        }

        return succeess;

    }

    public static String convertToCSV(String[] data) {
        return Stream.of(data)
                .collect(Collectors.joining(","));
    }

    public static void writeToFile(List<String[]> data, String path, String fileName) throws FileNotFoundException {
        String filePath = path + fileName + ".csv";
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
