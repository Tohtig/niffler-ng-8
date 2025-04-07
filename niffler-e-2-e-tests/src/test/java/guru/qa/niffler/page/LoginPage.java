package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement registerButton = $("a.form__register");

    @Step("Логин с учетной записью: username - {0}, password - {1}")
    public MainPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new MainPage();
    }

    @Step("Нажатие на кнопку Log in")
    public LoginPage clickRegister() {
        registerButton.click();
        return this;
    }

    @Step("Проверка, что на странице отображается ошибка входа")
    public LoginPage checkThatLoginErrorIsDisplayed() {
        $("p.form__error").shouldHave(text("Неверные учетные данные пользователя"));
        return this;
    }
}
