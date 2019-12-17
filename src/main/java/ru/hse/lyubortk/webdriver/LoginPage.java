package ru.hse.lyubortk.webdriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.openqa.selenium.support.ui.ExpectedConditions.and;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class LoginPage extends Page{
    private static final String PASSWORD_FIELD_ID = "id_l.L.password";
    private static final String LOGIN_FIELD_ID = "id_l.L.login";
    private static final String LOGIN_BUTTON_ID = "id_l.L.loginButton";

    private final WebElement loginField;
    private final WebElement passwordField;
    private final WebElement logInButton;

    LoginPage(WebDriver driver) {
        super(driver);
        wait.until(and(
                visibilityOfElementLocated(By.id(LOGIN_FIELD_ID)),
                visibilityOfElementLocated(By.id(PASSWORD_FIELD_ID)),
                visibilityOfElementLocated(By.id(LOGIN_BUTTON_ID))
        ));

        loginField = driver.findElement(By.id(LOGIN_FIELD_ID));
        passwordField = driver.findElement(By.id(PASSWORD_FIELD_ID));
        logInButton = driver.findElement(By.id(LOGIN_BUTTON_ID));
    }

    public void typeLogin(String login) {
        loginField.clear();
        loginField.sendKeys(login);
    }

    public void typePassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public UsersPage logIn() {
        logInButton.click();
        return new UsersPage(driver);
    }
}
