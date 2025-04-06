package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {
  // Локаторы для вкладок
  private final SelenideElement friendsTab = $("a[href*='/people/friends']");
  private final SelenideElement allPeopleTab = $("a[href*='/people/all']");

  // Локаторы для списков
  private final ElementsCollection friendsList = $$("#friends tr");
  private final ElementsCollection incomingRequestsList = $$("#requests tr");

  public FriendsPage() {
    // Проверка загрузки страницы
    friendsTab.shouldBe(visible, Duration.ofSeconds(10));
  }

  @Step("Открытие вкладки друзей")
  public FriendsPage openFriendsTab() {
    friendsTab.click();
    return this;
  }

  @Step("Открытие вкладки всех людей")
  public FriendsPage openAllPeopleTab() {
    allPeopleTab.click();
    return this;
  }

  @Step("Проверка наличия друга с именем пользователя: {0}")
  public FriendsPage assertFriendPresent(String friendUsername) {
    openFriendsTab();
    friendsList.findBy(Condition.text(friendUsername)).shouldBe(visible);
    return this;
  }

  @Step("Проверка, что список друзей пуст")
  public FriendsPage assertFriendsListEmpty() {
    openFriendsTab();
    friendsList.shouldHave(CollectionCondition.size(0));
    return this;
  }

  @Step("Проверка наличия входящего запроса от пользователя: {0}")
  public FriendsPage assertIncomingRequestPresent(String username) {
    openFriendsTab();
    incomingRequestsList.findBy(Condition.text(username)).shouldBe(visible);
    return this;
  }
}
