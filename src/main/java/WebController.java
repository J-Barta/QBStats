import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.sql.Array;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WebController {
    private JavascriptExecutor js;
    private ChromeDriver driver;

    private int setID;

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
                int index = 0;
                for (WebElement e : stats) {
                    index++;
                    System.out.println(index + ": " + e.getText());
                }


                String statChoice = inputScanner.nextLine();  // Read user input

                WebElement selectedStats = stats.get(Integer.valueOf(statChoice) - 1).findElement(By.tagName("a"));

                openLink(selectedStats);

                List<WebElement> statTables = ((WebElement) js.executeScript("return document.querySelector(\"body > div > div.OverallBody > div.ContentContainer\")"))
                        .findElements(By.tagName("table"));

                //Reject index 0 in the list because it's a header
                for (int i = 1; i < statTables.size(); i++) {
                    List<WebElement> rows = statTables.get(i).findElement(By.tagName("tbody")).findElements(By.tagName("tr"));

                    //Reject index 0 because it's a header again
                    for (int t = 1; t < rows.size(); t++) {
                        List<WebElement> rowData = rows.get(t).findElements(By.tagName("td"));
                        teamStats.add(getStats(rowData, rows.get(0).findElements(By.tagName("td"))));
                    }
                }

                backClick();
            }

            backClick();
        }

        //Sort the stats of the various teams
        Collections.sort(teamStats, (o1, o2) -> ((Double)o2.getPpg()).compareTo(o1.getPpg()));

        return teamStats;
    }

    private TeamStats getStats(List<WebElement> teamData, List<WebElement> header) {
        int[] indices = indices(header);

        return new TeamStats(
                teamData.get(indices[0]).findElement(By.tagName("a")).getText(),
                Double.valueOf(teamData.get(indices[1]).getText()),
                Double.valueOf(teamData.get(indices[2]).getText()),
                Double.valueOf(teamData.get(indices[3]).getText()),
                Double.valueOf(teamData.get(indices[4]).getText()),
                Double.valueOf(teamData.get(indices[5]).getText()) +
                        Double.valueOf(teamData.get(indices[6]).getText())
        );
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

    public void openPage() {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\WordleBot\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        js =  (JavascriptExecutor) driver;

        driver.get("https://hsquizbowl.org/db/questionsets/" + setID + "/");
    }
}
