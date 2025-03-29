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
     * Проверяет, включен ли тоггл отображения архивных категорий
     *
     * @return true, если тоггл включен
     */
    public boolean isArchivedCategoriesVisible() {
        // Получаем родительский элемент тоггла и проверяем наличие класса Mui-checked
        return showArchivedCategoriesToggle.parent().has(cssClass("Mui-checked"));
    }

    /**
     * Проверяет наличие категории с указанным именем на странице
     *
     * @param categoryName название категории для проверки
     * @return true, если категория найдена
     */
    public boolean isCategoryPresent(String categoryName) {
        return categoryLabels.stream()
                .anyMatch(element -> element.shouldBe(visible).has(text(categoryName)));
    }

    /**
     * Устанавливает тоггл отображения архивных категорий в определенное состояние
     *
     * @param shouldBeEnabled true - включить отображение архивных категорий, false - выключить
     * @return текущий экземпляр страницы
     */
    public ProfilePage setArchivedCategoriesVisibility(boolean shouldBeEnabled) {
        boolean currentState = isArchivedCategoriesVisible();

        // Переключаем тоггл только если его текущее состояние не соответствует желаемому
        if (currentState != shouldBeEnabled) {
            toggleArchivedCategories();
        }

        return this;
    }

    // Метод для переключения отображения архивных категорий
    private ProfilePage toggleArchivedCategories() {
        showArchivedCategoriesToggle.click();
        return this;
    }
}
