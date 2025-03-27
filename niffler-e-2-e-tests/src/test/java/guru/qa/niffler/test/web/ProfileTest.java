package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProfileTest {
    private static final Config CFG = Config.getInstance();

    @Category(
        username = "duck",
        archived = true
    )
    @Test
    void archivedCategoryShouldPresentInCategoriesList(CategoryJson category){

    }

    @Category(
            username = "duck",
            archived = true
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(CategoryJson category){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345");
        ProfilePage profilePage = Selenide.open(CFG.profileUrl(), ProfilePage.class);
        assertEquals(category.username(), profilePage.getUsername());
    }
}
