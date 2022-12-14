package me.dcc;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Main {

    //TODO: Encapsulate functions more
    public static void main(String[] args) throws Exception {

        String userName = System.getProperty("user.name");

        //Copy file to desktop folder
        String driverPath = "C:\\Users\\" + userName + "\\Desktop\\QBStats\\";
        new File(driverPath).mkdirs(); //Create the directory for the path if needed
        new File(driverPath + "chromedriver.exe").delete(); //Delete a chromedriver if present
        boolean success = FileUtils.copy(Main.class.getResourceAsStream("/chromedriver.exe"), driverPath + "chromedriver.exe");

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

        FileUtils.writeToFile(outputData, driverPath, setName);

        System.out.println("csv file saved successfully!");

        controller.quit();

        System.out.println("Chrome window closed. Goodbye.");

        //Delete chromedriver
        new File(driverPath + "chromedriver.exe").delete();
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
