package guru.qa.niffler.test.web;


import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.Type;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class FriendsWebTest {

  private final Config CFG = Config.getInstance();

  @Test
  @ExtendWith(UsersQueueExtension.class)
  void friendsShouldBePresentInFriendsTable(@UserType(Type.WITH_FRIEND) StaticUser user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .login(user.username(), user.password());

    FriendsPage friendsPage = Selenide.open(CFG.friendsUrl(), FriendsPage.class);
    friendsPage.assertFriendPresent(user.friend());
  }

  @Test
  @ExtendWith(UsersQueueExtension.class)
  void friendsTableShouldBeEmptyForNewUser(@UserType(Type.EMPTY) StaticUser user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .login(user.username(), user.password());

    FriendsPage friendsPage = Selenide.open(CFG.friendsUrl(), FriendsPage.class);
    friendsPage.assertFriendsListEmpty();
  }

  @Test
  @ExtendWith(UsersQueueExtension.class)
  void incomeInvitationShouldBePresentInFriendsTable(@UserType(Type.WITH_INCOME_REQUEST) StaticUser user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .login(user.username(), user.password());

    FriendsPage friendsPage = Selenide.open(CFG.friendsUrl(), FriendsPage.class);
    friendsPage.assertIncomingRequestPresent(user.income());
  }

  @Test
  @ExtendWith(UsersQueueExtension.class)
  void outcomeInvitationShouldBePresentInAllPeopleTable(@UserType(Type.WITH_OUTCOME_REQUEST) StaticUser user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .login(user.username(), user.password());

    FriendsPage friendsPage = Selenide.open(CFG.friendsUrl(), FriendsPage.class);
    friendsPage.assertOutgoingRequestPresent(user.outcome());
  }
}
