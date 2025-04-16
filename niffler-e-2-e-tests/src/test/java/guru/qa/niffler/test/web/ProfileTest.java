package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileTest {
    private static final Config CFG = Config.getInstance();

    @User(
            username = "duck",
            categories = @Category(
                    archived = true
            )
    )
    @Test
    @Step("Проверка наличия архивных категорий в списке категорий")
    void archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345")
                .openProfilePage()
                .setArchivedCategoriesVisibility(true) // Включаем отображение архивных категорий и проверяем видимость категории
                .checkArchivedCategoriesVisible()
                .checkCategoryPresent(category.name())
                .setArchivedCategoriesVisibility(false)// Выключаем отображение и проверяем отсутствие категории
                .checkCategoryNotPresent(category.name());
    }

    @User(
            username = "duck",
            categories = @Category(
                    archived = false
            )
    )
    @Test
    @Step("Проверка наличия активных категорий в списке категорий")
    void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345")
                .openProfilePage()
                .setArchivedCategoriesVisibility(false)
                .checkCategoryPresent(category.name());
    }
}
