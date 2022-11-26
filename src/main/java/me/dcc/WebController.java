package me.dcc;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WebController {
    private JavascriptExecutor js;
    private ChromeDriver driver;

    private int setID;

    private int[] currentIndices;

    public WebController(int setID) {
        this.setID = setID;
    }

    public List<TeamStats> getStats() {
        List<TeamStats> teamStats = new ArrayList<>();

        Scanner inputScanner = new Scanner(System.in);  // Create a Scanner object

        List<WebElement> links = getLinks();

        for(int tournamentId = 0; tournamentId<links.size(); tournamentId++) {
            links = getLinks();
            WebElement link = links.get(tournamentId);
            String linkText = link.getText();
            openLink(link);


            WebElement statTable = ((WebElement) js.executeScript("return document.querySelector(\"#Stats > div > ul\")"));

            if(statTable != null) {
                List<WebElement> stats = statTable.findElements(By.tagName("li"));

                System.out.println("Tournament " + (tournamentId+1) + " / " + links.size() + ": Select stats for: " + linkText);
                System.out.println("Separate by commas to use multiple");
                int index = 0;
                for (WebElement e : stats) {
                    index++;
                    System.out.println(index + ": " + e.getText());
                }

                String statChoice;
                if(stats.size() > 1) {
                    statChoice = inputScanner.nextLine();  // Read user input
                } else {
                    statChoice = "1";
                }

                int[] parsedChoices = parseStatChoice(statChoice);
                List<WebElement> selectedStatsList = new ArrayList<>();
                for(int choice : parsedChoices) {
                    selectedStatsList.add(stats.get(choice -1)); //-1 because zero indexed
                }

                Map<String, TeamStats> tournamentStats = new HashMap<>();


                for(WebElement selectedStats : selectedStatsList) {
                    openLink(selectedStats.findElement(By.tagName("a")));


                    List<WebElement> statTables = ((WebElement) js.executeScript("return document.querySelector(\"body > div > div.OverallBody > div.ContentContainer\")"))
                            .findElements(By.tagName("table"));

                    //Reject index 0 in the list because it's a header
                    for (int i = 1; i < statTables.size(); i++) {
                        List<WebElement> rows = statTables.get(i).findElement(By.tagName("tbody")).findElements(By.tagName("tr"));

                        currentIndices = indices(rows.get(0).findElements(By.tagName("td")));

                        //Reject index 0 because it's a header again
                        for (int t = 1; t < rows.size(); t++) {
                            List<WebElement> rowData = rows.get(t).findElements(By.tagName("td"));
                            TeamStats currentStats = getStats(rowData);

                            //Either add this team's stats to the tournament stats, or append this stat sheet's team data to the existing team data
                            if(tournamentStats.get(currentStats.getName()) != null) {
                                tournamentStats.get(currentStats.getName()).combineWith(currentStats);
                            }else tournamentStats.put(currentStats.getName(), currentStats);
                        }
                    }

                    backClick();
                }

                for(TeamStats team : tournamentStats.values()) {
                    teamStats.add(team);
                }
            } else {
                System.out.println("Tournament " + (tournamentId+1) + " / " + links.size() + ": NO STATS for: " + linkText);
            }

            backClick();
        }

        //Sort the stats of the various teams
        Collections.sort(teamStats, (o1, o2) -> ((Double)o2.getPpg()).compareTo(o1.getPpg()));

        return teamStats;
    }

    private int[] parseStatChoice(String input) {
        int count = 0;

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ',') {
                count++;
            }
        }
        count++;

        String sub = input;

        int[] parsed = new int[count];
        for(int i=0; i<count; i++) {
            parsed[i] = Integer.valueOf(sub.substring(0, 1));
            if(sub.length() >= 2) sub = sub.substring(2);
        }

        return parsed;
    }

    private TeamStats getStats(List<WebElement> teamData) {
        try {
            return new TeamStats(
                    teamData.get(currentIndices[0]).findElement(By.tagName("a")).getText(),
                    Double.valueOf(teamData.get(currentIndices[1]).getText()),
                    Double.valueOf(teamData.get(currentIndices[2]).getText()),
                    Double.valueOf(teamData.get(currentIndices[3]).getText()),
                    Double.valueOf(teamData.get(currentIndices[4]).getText()),
                    Double.valueOf(teamData.get(currentIndices[5]).getText()) +
                            Double.valueOf(teamData.get(currentIndices[6]).getText())
            );

        } catch (Exception e) {

//            e.printStackTrace();

            System.out.println("Automatic stat collection failed!");
            System.out.println("Please enter indices manually (starting from zero)");

            getStatIndicesManually();

            return getStats(teamData);
        }
    }

    /**
     * Mutates the array of indices based on inputs from the terminal
     */
    private void getStatIndicesManually() {

        Scanner input = new Scanner(System.in);

        currentIndices = new int[7];
        System.out.println("Index of team name");
        currentIndices[0] = input.nextInt();
        System.out.println("Index of PPG");
        currentIndices[1] = input.nextInt();
        System.out.println("Index of Powers");
        currentIndices[2] = input.nextInt();
        System.out.println("Index of Regs");
        currentIndices[3] = input.nextInt();
        System.out.println("Index of PPB");
        currentIndices[4] = input.nextInt();
        System.out.println("Index of Wins");
        currentIndices[5] = input.nextInt();
        System.out.println("Index of Losses");
        currentIndices[6] = input.nextInt();
    }


    private int[] indices(List<WebElement> header) {
        int[] indices = new int[7];
        for(int i = 0; i< header.size(); i++) {
            String text = header.get(i).getText();

            switch (text) {
                case "Team":
                    indices[0] = i;
                    break;
                case "PPG":
                    indices[1] = i;
                    break;
                case "15":
                    indices[2] = i;
                    break;
                case "10":
                    indices[3] = i;
                    break;
                case "PPB":
                case "P/B":
                    indices[4] = i;
                    break;
                case "W":
                    indices[5] = i;
                    break;
                case "L":
                    indices[6] = i;
                    break;
            }
        }

        return indices;
    }

    private List<WebElement> getLinks() {
        WebElement mainColumn = (WebElement) js.executeScript("return document.querySelector(\"body > div > div.OverallBody > div.ContentContainer > div.MainColumn > div.Subsequent\")");

        List<List<WebElement>> tournaments = mainColumn.findElements(By.className("Tournaments")).stream().map((e) -> e.findElements(By.tagName("li"))).collect(Collectors.toList());

        List<WebElement> links = new ArrayList<>();
        for(List<WebElement> tournamentList : tournaments) {
            for(WebElement t : tournamentList) {
                links.add(t.findElement(By.className("Name")).findElement(By.tagName("a")));
            }
        }

        return links;
    }

    private void openLink(WebElement link) {
        new Actions(driver).click(link).pause(Duration.ofMillis(250)).perform();
    }


    private void backClick() {
        PointerInput mouse = new PointerInput(PointerInput.Kind.MOUSE, "default mouse");

        Sequence actions = new Sequence(mouse, 0)
                .addAction(mouse.createPointerDown(PointerInput.MouseButton.BACK.asArg()))
                .addAction(mouse.createPointerUp(PointerInput.MouseButton.BACK.asArg()));

        driver.perform(Collections.singletonList(actions));
    }

    public String openPage(String driverPath) {
        System.setProperty("webdriver.chrome.driver", driverPath);
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        js = driver;

        driver.get("https://hsquizbowl.org/db/questionsets/" + setID + "/");

        return ((WebElement) js.executeScript("return document.querySelector(\"body > div > div.OverallBody > div.ContentContainer > div.MainColumn > div.First > h2\")")).getText();
    }

    public void close() {
        driver.quit();
    }
}
