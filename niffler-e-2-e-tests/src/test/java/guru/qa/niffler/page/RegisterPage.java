package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement confirmPasswordInput = $("#passwordSubmit");
    private final SelenideElement registerButton = $("button[type='submit']");

    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public RegisterPage setConfirmPassword(String confirmPassword) {
        confirmPasswordInput.setValue(confirmPassword);
        return this;
    }

    public CongratulationsPage submitRegistration() {
        registerButton.click();
        return new CongratulationsPage();
    }

    // New method for handling error scenario
    public RegisterPage submitRegistrationExpectingError() {
        registerButton.click();
        return this;
    }

    public void checkThatPageContainsError(String errorMessage) {
        $("span.form__error").shouldHave(text(errorMessage));
    }
}