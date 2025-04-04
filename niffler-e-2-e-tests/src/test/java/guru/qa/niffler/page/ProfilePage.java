package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage {


    // Локатор для тоггла
    private final SelenideElement showArchivedCategoriesToggle = $("input.MuiSwitch-input");

    // Локатор для категорий
    private final ElementsCollection categoryLabels = $$(".MuiChip-label");

    /**
     * Проверяет, что тоггл отображения архивных категорий включен
     *
     * @return текущий экземпляр страницы
     */
    public ProfilePage checkArchivedCategoriesVisible() {
        showArchivedCategoriesToggle.parent().shouldHave(cssClass("Mui-checked"));
        return this;
    }

    /**
     * Проверяет наличие категории с указанным именем на странице
     *
     * @param categoryName название категории для проверки
     * @return текущий экземпляр страницы
     */
    public ProfilePage checkCategoryPresent(String categoryName) {
        categoryLabels.findBy(text(categoryName)).shouldBe(visible);
        return this;
    }

    /**
     * Проверяет отсутствие категории с указанным именем на странице
     *
     * @param categoryName название категории для проверки
     * @return текущий экземпляр страницы
     */
    public ProfilePage checkCategoryNotPresent(String categoryName) {
        categoryLabels.findBy(text(categoryName)).shouldNotBe(visible);
        return this;
    }

    /**
     * Устанавливает тоггл отображения архивных категорий в определенное состояние
     *
     * @param shouldBeEnabled true - включить отображение архивных категорий, false - выключить
     * @return текущий экземпляр страницы
     */
    public ProfilePage setArchivedCategoriesVisibility(boolean shouldBeEnabled) {
        if (shouldBeEnabled) {
            if (!showArchivedCategoriesToggle.parent().has(cssClass("Mui-checked"))) {
                showArchivedCategoriesToggle.click();
                checkArchivedCategoriesVisible();
            }
        } else {
            if (showArchivedCategoriesToggle.parent().has(cssClass("Mui-checked"))) {
                showArchivedCategoriesToggle.click();
                showArchivedCategoriesToggle.parent().shouldNotHave(cssClass("Mui-checked"));
            }
        }
        return this;
    }

    public FriendsPage goToFriendsList() {
        $("a[href*='/people/friends']").click();
        return new FriendsPage();
    }
}
