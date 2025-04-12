package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Test;

@WebTest
public class LoginTest {
    private static final Config CFG = Config.getInstance();

    @Test
    @Step("Проверка отображения главной страницы после успешного входа")
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345")
                .checkThatPageLoaded();
    }

    @Test
    @Step("Проверка что пользователь остается на странице логина при вводе неверных учетных данных")
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(RandomDataUtils.randomUsername(), "123456");
        new LoginPage()
                .checkThatLoginErrorIsDisplayed();
    }
}
