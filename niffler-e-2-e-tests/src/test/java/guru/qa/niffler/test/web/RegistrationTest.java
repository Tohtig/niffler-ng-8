package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.Test;

public class RegistrationTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void shouldRegisterNewUser() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegister();

        new RegisterPage()
                .setUsername("newuser8")
                .setPassword("password123")
                .setConfirmPassword("password123")
                .submitRegistration()
                .checkThatPageContainsCongratulations();
    }

    @Test
    void shouldNotRegisterNewUserWithExistingUsername() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegister();

        new RegisterPage()
                .setUsername("duck")
                .setPassword("password123")
                .setConfirmPassword("password123")
                .submitRegistrationExpectingError()
                .checkThatPageContainsError("Username `duck` already exists");
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegister();

        new RegisterPage()
                .setUsername("newuser8")
                .setPassword("password123")
                .setConfirmPassword("121password1234")
                .submitRegistrationExpectingError()
                .checkThatPageContainsError("Passwords should be equal");
    }

}
