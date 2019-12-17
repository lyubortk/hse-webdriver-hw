package ru.hse.lyubortk.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class Page {
    protected final WebDriverWait wait;
    protected final WebDriver driver;

    protected Page(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 10);
    }

    public void quit() {
        driver.quit();
    }
}
