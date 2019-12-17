package ru.hse.lyubortk.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class YouTrackTestFramework {
    static final String USERS_URL = "localhost:8080/users";

    public static LoginPage start() {
        WebDriver driver = new ChromeDriver();
        driver.get(USERS_URL);
        return new LoginPage(driver);
    }
}
