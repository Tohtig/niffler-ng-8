package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.*;

@ExtendWith(UsersQueueExtension.class)
public class ProfileTest {
    private static final Config CFG = Config.getInstance();

    @Category(
            username = "duck",
            archived = true
    )
    @Disabled
    @Test
    void archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345");
        ProfilePage profilePage = Selenide.open(CFG.profileUrl(), ProfilePage.class);

        // Включаем отображение архивных категорий и проверяем видимость категории
        profilePage.setArchivedCategoriesVisibility(true)
                .checkArchivedCategoriesVisible()
                .checkCategoryPresent(category.name());

        // Выключаем отображение и проверяем отсутствие категории
        profilePage.setArchivedCategoriesVisibility(false)
                .checkCategoryNotPresent(category.name());
    }

    @Category(
            username = "duck",
            archived = false
    )
    @Disabled
    @Test
    void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345");
        ProfilePage profilePage = Selenide.open(CFG.profileUrl(), ProfilePage.class);

        // Выключаем отображение архивных и проверяем видимость активной категории
        profilePage.setArchivedCategoriesVisibility(false)
                .checkCategoryPresent(category.name());
    }

    @Test
    void testWithEmptyUser0(@UserType(empty = true) StaticUser user) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println(user);
    }

    @Test
    void testWithEmptyUser1(@UserType(empty = false) StaticUser user) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println(user);
    }

    @Test
    void testWithEmptyUser2(@UserType(empty = false) StaticUser user) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println(user);
    }
}
