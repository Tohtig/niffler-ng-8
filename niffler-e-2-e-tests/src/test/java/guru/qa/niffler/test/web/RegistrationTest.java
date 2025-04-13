package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegisterPage;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Test;

@WebTest
public class RegistrationTest {

    private static final Config CFG = Config.getInstance();

    @Test
    @Step("Проверка успешной регистрации нового пользователя")
    void shouldRegisterNewUser() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegister();

        new RegisterPage()
                .setUsername(RandomDataUtils.randomUsername())
                .setPassword("12345")
                .setConfirmPassword("12345")
                .submitRegistration()
                .checkThatPageContainsCongratulations();
    }

    @Test
    @Step("Проверка ошибки при регистрации с существующим именем пользователя")
    void shouldNotRegisterNewUserWithExistingUsername() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegister();

        new RegisterPage()
                .setUsername("duck")
                .setPassword("12345")
                .setConfirmPassword("12345")
                .submitRegistrationExpectingError()
                .checkThatPageContainsError("Username `duck` already exists");
    }

    @Test
    @Step("Проверка ошибки при несовпадении пароля и подтверждения пароля")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegister();

        new RegisterPage()
                .setUsername(RandomDataUtils.randomUsername())
                .setPassword("password123")
                .setConfirmPassword("121password1234")
                .submitRegistrationExpectingError()
                .checkThatPageContainsError("Passwords should be equal");
    }

}
