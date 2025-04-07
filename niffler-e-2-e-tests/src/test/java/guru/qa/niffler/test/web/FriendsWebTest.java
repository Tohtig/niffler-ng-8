package guru.qa.niffler.test.web;


import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.Type;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import guru.qa.niffler.page.LoginPage;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsWebTest {

  private final Config CFG = Config.getInstance();

  @Test
  @Step("Проверка наличия друзей в таблице друзей")
  void friendsShouldBePresentInFriendsTable(@UserType(Type.WITH_FRIEND) StaticUser user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .login(user.username(), user.password())
            .goToFriendsList()
            .assertFriendPresent(user.friend());
  }

  @Test
  @Step("Проверка пустой таблицы друзей для нового пользователя")
  void friendsTableShouldBeEmptyForNewUser(@UserType(Type.EMPTY) StaticUser user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .login(user.username(), user.password())
            .goToFriendsList()
            .assertFriendsListEmpty();
  }

  @Test
  @Step("Проверка наличия входящих приглашений в таблице друзей")
  void incomeInvitationShouldBePresentInFriendsTable(@UserType(Type.WITH_INCOME_REQUEST) StaticUser user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .login(user.username(), user.password())
            .goToFriendsList()
            .assertIncomingRequestPresent(user.income());
  }

  @Test
  @Step("Проверка наличия исходящих приглашений в таблице всех пользователей")
  void outcomeInvitationShouldBePresentInAllPeopleTable(@UserType(Type.WITH_OUTCOME_REQUEST) StaticUser user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .login(user.username(), user.password())
            .allPeoplesPage()
            .checkInvitationSentToUser(user.outcome());
  }
}
