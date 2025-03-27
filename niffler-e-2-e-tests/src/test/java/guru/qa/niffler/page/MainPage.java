package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {
  private final ElementsCollection tableRows = $("#spendings tbody").$$("tr");
  private final SelenideElement statisticsHeader = $("h2.MuiTypography-root.MuiTypography-h5.css-giaux5");
  private final SelenideElement historyOfSpendingsHeader = $("h2.MuiTypography-root.MuiTypography-h5.css-uxhuts");

  public EditSpendingPage editSpending(String spendingDescription) {
    tableRows.find(text(spendingDescription)).$$("td").get(5).click();
    return new EditSpendingPage();
  }

  public MainPage checkThatTableContainsSpending(String spendingDescription) {
    tableRows.find(text(spendingDescription)).should(visible);
    return this;
  }

  public MainPage checkThatStatisticsIsDisplayed() {
    statisticsHeader.shouldBe(visible);
    return this;
  }

  public MainPage checkThatHistoryOfSpendingsIsDisplayed() {
    historyOfSpendingsHeader.shouldBe(visible);
    return this;
  }
}
