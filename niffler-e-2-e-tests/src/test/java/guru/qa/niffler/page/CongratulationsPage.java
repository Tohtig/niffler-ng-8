package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class CongratulationsPage {

    private final SelenideElement signInButton = $("a.form_sign-in");
    private final SelenideElement congratulationsText = $("p.form__paragraph_success");

    @Step("Нажатие на кнопку Sign In")
    public LoginPage clickSignInButton() {
        signInButton.click();
        return new LoginPage();
    }

    @Step("Проверка наличия поздравления о успешной регистрации")
    public void checkThatPageContainsCongratulations() {
        congratulationsText.shouldHave(text("Congratulations! You've registered!"));
    }
}