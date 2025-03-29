package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProfileTest {
    private static final Config CFG = Config.getInstance();

    @Category(
            username = "duck",
            archived = true
    )
    @Test
    void archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345");
        ProfilePage profilePage = Selenide.open(CFG.profileUrl(), ProfilePage.class);

        // Включаем отображение архивных категорий
        profilePage.setArchivedCategoriesVisibility(true);

        // Проверяем, что тоггл включен
        assertTrue(profilePage.isArchivedCategoriesVisible(), "Тоггл архивных категорий должен быть включен");

        // Проверяем наличие архивной категории в списке
        assertTrue(profilePage.isCategoryPresent(category.name()),
                "Архивная категория '" + category.name() + "' должна присутствовать в списке");

        // Выключаем отображение архивных категорий
        profilePage.setArchivedCategoriesVisibility(false);

        // Проверяем, что тоггл выключен
        assertFalse(profilePage.isArchivedCategoriesVisible(), "Тоггл архивных категорий должен быть выключен");

        // Проверяем отсутствие архивной категории в списке
        assertFalse(profilePage.isCategoryPresent(category.name()),
                "Архивная категория '" + category.name() + "' не должна присутствовать в списке");
    }

    @Category(
            username = "duck",
            archived = false
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345");
        ProfilePage profilePage = Selenide.open(CFG.profileUrl(), ProfilePage.class);
        // Выключаем отображение архивных категорий для начала
        profilePage.setArchivedCategoriesVisibility(false);

        // Проверяем, что тоггл выключен
        assertFalse(profilePage.isArchivedCategoriesVisible(), "Тоггл архивных категорий должен быть выключен");

        // Проверяем наличие активной категории в списке при выключенном тоггле
        assertTrue(profilePage.isCategoryPresent(category.name()),
                "Активная категория '" + category.name() + "' должна присутствовать в списке с выключенным тогглом");

        // Включаем отображение архивных категорий
        profilePage.setArchivedCategoriesVisibility(true);

        // Проверяем, что тоггл включен
        assertTrue(profilePage.isArchivedCategoriesVisible(), "Тоггл архивных категорий должен быть включен");

        // Проверяем, что активная категория все равно отображается
        assertTrue(profilePage.isCategoryPresent(category.name()),
                "Активная категория '" + category.name() + "' должна присутствовать в списке с включенным тогглом");
    }
}
