package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ProfilePage {
    // Элемент для поля username (только для отображения)
    private final SelenideElement usernameInput = $("input[name='username']");

    // Элемент для поля name
    private final SelenideElement nameInput = $("input[name='name']");

    // Элемент для кнопки сохранения изменений
    private final SelenideElement saveButton = $("button[type='submit']");

    // Элемент для загрузки нового фото (кнопка, связанная с input[type='file'])
    private final SelenideElement uploadPictureButton = $("label[for='image__input']");

    // Метод для получения значения поля username
    public String getUsername() {
        return usernameInput.shouldBe(visible).getValue();
    }

    // Метод для ввода нового имени
    public ProfilePage setName(String newName) {
        nameInput.shouldBe(visible).clear();
        nameInput.setValue(newName);
        return this;
    }

    // Метод для нажатия на кнопку сохранения изменений
    public ProfilePage clickSaveChanges() {
        saveButton.shouldBe(visible).click();
        return this;
    }

    // Метод для запуска диалога загрузки нового фото (если понадобится)
    public ProfilePage clickUploadPicture() {
        uploadPictureButton.shouldBe(visible).click();
        return this;
    }
}
