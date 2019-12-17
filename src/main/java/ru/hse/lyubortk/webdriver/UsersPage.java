package ru.hse.lyubortk.webdriver;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.openqa.selenium.support.ui.ExpectedConditions.and;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static ru.hse.lyubortk.webdriver.YouTrackTestFramework.USERS_URL;

public class UsersPage extends Page {
    private static final String TOP_RIGHT_USER_MENU_X_PATH =
            "//*[@id=\"id_l.HeaderNew.header\"]/div[1]/div/div/a[1]/span";
    private static final String LOG_OUT_BUTTON_CSS_SELECTOR =
            "body > div.ring-dropdown > div > a.ring-dropdown__item.yt-header__login-link.ring-link";
    private static final String TABLE_BODY_X_PATH = "//*[@id=\"id_l.U.usersList.usersList\"]/table/tbody";
    private static final String USER_NAMES_XPATH_SUFFIX = ".//td[1]/a";
    private static final String CREATE_USER_BUTTON_ID = "id_l.U.createNewUser";
    private static final String FROM_USER_NAME_TO_DELETE_XPATH = ".//../../td[6]/a[1]";
    private static final String ERROR_SEVERITY_CLASSNAME = "errorSeverity";

    private WebElement topRightUserMenu;
    private WebElement tableBody;
    private WebElement createUserButton;

    UsersPage(WebDriver driver) {
        super(driver);
        loadElements();
    }

    private void loadElements() {
        wait.until(and(
                visibilityOfElementLocated(By.xpath(TOP_RIGHT_USER_MENU_X_PATH)),
                visibilityOfElementLocated(By.xpath(TABLE_BODY_X_PATH)),
                visibilityOfElementLocated(By.id(CREATE_USER_BUTTON_ID))
        ));
        topRightUserMenu = driver.findElement(By.xpath(TOP_RIGHT_USER_MENU_X_PATH));
        tableBody = driver.findElement(By.xpath(TABLE_BODY_X_PATH));
        createUserButton = driver.findElement(By.id(CREATE_USER_BUTTON_ID));
    }

    public LoginPage logOut() {
        topRightUserMenu.click();
        wait.until(visibilityOfElementLocated(By.cssSelector(LOG_OUT_BUTTON_CSS_SELECTOR)));
        driver.findElement(By.cssSelector(LOG_OUT_BUTTON_CSS_SELECTOR)).click();
        return new LoginPage(driver);
    }

    public List<String> listUsers() {
        return tableBody.findElements(By.xpath(USER_NAMES_XPATH_SUFFIX)).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public CreateUserPanel createUser() {
        createUserButton.click();
        return new CreateUserPanel();
    }

    public void refresh() {
        driver.get(USERS_URL);
        loadElements();
    }

    public String getTopErrorString() {
        return driver.findElement(By.className(ERROR_SEVERITY_CLASSNAME)).getText();
    }

    public void deleteUser(String name) {
        List<WebElement> userNames = tableBody.findElements(By.xpath(USER_NAMES_XPATH_SUFFIX));
        for (WebElement userName : userNames) {
            if (!userName.getText().equals(name)) {
                continue;
            }
            userName.findElement(By.xpath(FROM_USER_NAME_TO_DELETE_XPATH)).click();
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            return;
        }
        throw new NoSuchElementException("user " + name + " not found");
    }

    public class CreateUserPanel {
        private final static String LOGIN_FIELD_ID = "id_l.U.cr.login";
        private final static String PASSWORD_FIELD_ID = "id_l.U.cr.password";
        private final static String CONFIRM_PASSWORD_FIELD_ID = "id_l.U.cr.confirmPassword";
        private final static String OK_BUTTON_ID = "id_l.U.cr.createUserOk";
        private final static String CANCEL_BUTTON_ID = "id_l.U.cr.createUserCancel";
        private static final String ERROR_BULB_CLASSNAME = "error-bulb2";
        private static final String ERROR_BULB_MESSAGE_CSS_SELECTOR = "body > div.error-tooltip.tooltip";
        private static final String EDIT_USER_URL_PART = "editUser";

        private final WebElement loginField;
        private final WebElement passwordField;
        private final WebElement confirmPasswordField;
        private final WebElement okButton;
        private final WebElement cancelButton;

        private CreateUserPanel() {
            wait.until(and(
                    visibilityOfElementLocated(By.id(LOGIN_FIELD_ID)),
                    visibilityOfElementLocated(By.id(PASSWORD_FIELD_ID)),
                    visibilityOfElementLocated(By.id(CONFIRM_PASSWORD_FIELD_ID)),
                    visibilityOfElementLocated(By.id(OK_BUTTON_ID)),
                    visibilityOfElementLocated(By.id(CANCEL_BUTTON_ID))
            ));
            loginField = driver.findElement(By.id(LOGIN_FIELD_ID));
            passwordField = driver.findElement(By.id(PASSWORD_FIELD_ID));
            confirmPasswordField = driver.findElement(By.id(CONFIRM_PASSWORD_FIELD_ID));
            okButton = driver.findElement(By.id(OK_BUTTON_ID));
            cancelButton = driver.findElement(By.id(CANCEL_BUTTON_ID));
        }

        public void create() {
            okButton.click();
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains(EDIT_USER_URL_PART),
                    visibilityOfElementLocated(By.className(ERROR_BULB_CLASSNAME)),
                    visibilityOfElementLocated(By.className(ERROR_SEVERITY_CLASSNAME))
            ));
        }

        public void cancel() {
            cancelButton.click();
        }

        public void typePassword(String password) {
            passwordField.clear();
            confirmPasswordField.clear();
            passwordField.sendKeys(password);
            confirmPasswordField.sendKeys(password);
        }

        public void typeLogin(String login) {
            loginField.clear();
            loginField.sendKeys(login);
        }

        public void pasteLogin(String text) {
            loginField.clear();
            StringSelection textToPaste= new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(textToPaste, null);
            loginField.sendKeys(Keys.CONTROL, "v");
        }

        public String getBulbErrorString() {
            driver.findElement(By.className(ERROR_BULB_CLASSNAME)).click();
            wait.until(visibilityOfElementLocated(By.cssSelector(ERROR_BULB_MESSAGE_CSS_SELECTOR)));
            return driver.findElement(By.cssSelector(ERROR_BULB_MESSAGE_CSS_SELECTOR)).getText();
        }
    }
}
