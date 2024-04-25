package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.List;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestCases {

    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        // Setup WebDriverManager for Chrome
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @Test
    public void fullScrren() {
        driver.manage().window().maximize();
    }

    @Test
    public void testOpenBrowser() throws InterruptedException {
        // Open a website
        driver.get("https://www.scrapethissite.com/pages/");
    }

    @Test
    public void clickHockyTeams() throws InterruptedException {
        WebElement HockyTeams = driver.findElement(By.xpath("//*[@id='pages']/section/div/div/div/div[2]/h3/a"));
        Thread.sleep(1000);
        HockyTeams.click();
    }

    @Test
    public void collectColumn() throws InterruptedException {
        // Initialize ArrayList to store HashMaps
        ArrayList<HashMap<String, String>> teamsData = new ArrayList<>();

        // Loop through pagination (up to 4 pages)
        for (int i = 1; i <= 4; i++) {
            // Find all page links within the pagination area
            List<WebElement> pageLinks = driver.findElements(By.cssSelector(".pagination a[href*='page_num=" + i + "']"));

            // Click on the current page link
            pageLinks.get(0).click();
            Thread.sleep(500);

            // Find all rows (tr elements) with class "team"
            List<WebElement> rows = driver.findElements(By.cssSelector("tr.team"));

            // Iterate over rows
            for (WebElement row : rows) {
                // Find all cells (td elements) in the row
                List<WebElement> cells = row.findElements(By.tagName("td"));

                // Extract data from cells
                String teamName = cells.get(0).getText().trim(); // Team Name
                String year = cells.get(1).getText().trim(); // Year
                String winPercentage = cells.get(5).getText().trim(); // Win Percentage

                // Convert win percentage string to double
                double winPercentageValue = Double.parseDouble(winPercentage);

                // Check if win percentage is less than 40%
                if (winPercentageValue < 0.40) {
                    // Create a HashMap to store team data
                    HashMap<String, String> teamData = new HashMap<>();
                    teamData.put("TeamName", teamName);
                    teamData.put("Year", year);
                    teamData.put("WinPercentage", winPercentage);

                    // Add team data to the ArrayList
                    teamsData.add(teamData);
                }
            }
        }

        // Write data to JSON file
        writeJsonToFile(teamsData, "hockey-team-data.json");

        // Assert JSON file existence and non-emptiness
        assertJsonFileExistsAndNotEmpty("hockey-team-data.json");
    }

    private void writeJsonToFile(ArrayList<HashMap<String, String>> data, String fileName) {
        String projectRoot = System.getProperty("user.dir");
        String filePath = Paths.get(projectRoot, "output", fileName).toString();
        try {
            // Create directories if they don't exist
            Files.createDirectories(Paths.get(projectRoot, "output"));
            
            // Write data to JSON file
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(filePath), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void assertJsonFileExistsAndNotEmpty(String fileName) {
        String projectRoot = System.getProperty("user.dir");
        String filePath = Paths.get(projectRoot, "output", fileName).toString();
        File file = new File(filePath);
        Assert.assertTrue(file.exists(), "JSON file should exist");
        Assert.assertTrue(file.length() > 0, "JSON file should not be empty");
    }

    // Add more test methods for additional test cases if needed

    // @AfterClass
    // public void tearDown() {
    // // Close the browser
    // if (driver != null) {
    // driver.quit();
    // }
    // }

}
