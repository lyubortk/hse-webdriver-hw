import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.lyubortk.webdriver.LoginPage;
import ru.hse.lyubortk.webdriver.Page;
import ru.hse.lyubortk.webdriver.UsersPage;
import ru.hse.lyubortk.webdriver.UsersPage.CreateUserPanel;
import ru.hse.lyubortk.webdriver.YouTrackTestFramework;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoginNameTest {
    UsersPage usersPage;

    @BeforeEach
    public void logIn() {
        LoginPage page = YouTrackTestFramework.start();
        page.typeLogin("root");
        page.typePassword("root");
        usersPage = page.logIn();
    }

    @AfterEach
    public void logOut() {
        Page page = usersPage.logOut();
        page.quit();
    }

    @Test
    public void testBasicLogin() {
        performTestWithCorrectLogin("user1", false, true);
    }

    @Test
    public void testLoginWithUnicode() {
        performTestWithCorrectLogin("Привет!", false, true);
    }

    @Test
    public void testLoginWithNumbers() {
        performTestWithCorrectLogin("874", false, true);
    }

    @Test
    public void testLoginWithOneSymbol() {
        performTestWithCorrectLogin("a", false, true);
    }

    @Test
    public void testMaxLengthLogin() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            builder.append("a");
        }
        performTestWithCorrectLogin(builder.toString(), false, true);
    }

    @Test
    public void testSameLogin() {
        performTestWithCorrectLogin("hello", false, false);
        performTestWithTopError("hello", "Value should be unique: login");
        usersPage.deleteUser("hello");
    }

    @Test
    public void testLoginWithTabs() {
        performTestWithCorrectLogin("\tus\ter\t", true, true);
    }

    @Test
    public void testLoginWithSlash() {
        performTestWithTopError(
                "/", "login shouldn't contain characters \"<\", \"/\", \">\": login"
        );
    }

    @Test
    public void testLoginWithDot() {
        performTestWithTopError(".", "Can't use \"..\", \".\" for login: login");
    }

    @Test
    public void testLoginWithSpaceInside() {
        performTestWithTopError("a a", "Restricted character ' ' in the name");
    }

    @Test
    public void testLoginWithSpaceBegin() {
        performTestWithTopError(" aa", "Restricted character ' ' in the name");
    }

    @Test
    public void testLoginWithSpaceEnd() {
        performTestWithTopError("aa ", "Restricted character ' ' in the name");
    }

    @Test
    public void testEmptyLogin() {
        CreateUserPanel panel = usersPage.createUser();
        panel.typeLogin("");
        panel.typePassword("dummypswd");
        panel.create();

        assertEquals(panel.getBulbErrorString(), "Login is required!");
        panel.cancel();
    }

    @Test
    public void testLoginWhichExceedsMaxLength() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            builder.append("a");
        }
        String login50characters = builder.toString();
        String login51characters = builder.append("a").toString();

        List<String> initialUsers = usersPage.listUsers();
        CreateUserPanel panel = usersPage.createUser();
        panel.typeLogin(login51characters);
        panel.typePassword("dummypswd");
        panel.create();

        usersPage.refresh();
        List<String> newUsers = usersPage.listUsers();

        assertEquals(initialUsers.size() + 1, newUsers.size());
        assertFalse(initialUsers.contains(login50characters));
        assertTrue(newUsers.contains(login50characters));
        usersPage.deleteUser(login50characters);
    }

    private void performTestWithTopError(String login, String expectedError) {
        CreateUserPanel panel = usersPage.createUser();
        panel.typeLogin(login);
        panel.typePassword("dummypswd");
        panel.create();

        assertEquals(usersPage.getTopErrorString(), expectedError);
        panel.cancel();
    }

    private void performTestWithCorrectLogin(String login, boolean paste, boolean deleteUser) {
        List<String> initialUsers = usersPage.listUsers();

        CreateUserPanel panel = usersPage.createUser();
        if (paste) {
            panel.pasteLogin(login);
        } else {
            panel.typeLogin(login);
        }
        panel.typePassword("dummypswd");
        panel.create();

        usersPage.refresh();
        List<String> newUsers = usersPage.listUsers();

        assertEquals(initialUsers.size() + 1, newUsers.size());
        assertFalse(initialUsers.contains(login));
        assertTrue(newUsers.contains(login));
        if (deleteUser) {
            usersPage.deleteUser(login);
        }
    }
}
