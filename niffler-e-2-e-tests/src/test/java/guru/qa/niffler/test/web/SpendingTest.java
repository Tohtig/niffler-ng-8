package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Test;

@WebTest
public class SpendingTest {
    private static final Config CFG = Config.getInstance();

    @Test
    @User(
            username = "duck",
            spends = @Spend(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990,
                            currency = CurrencyValues.RUB
                    )

    )
    @Step("Проверка изменения описания траты из таблицы")
    void categoryDescriptionShouldBeChangedFromTable(SpendJson spend) {
        final String newDescription = "Обучение Niffler Next Generation v.1";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345")
                .editSpending(spend.description())
                .setNewSpendingDescription(newDescription)
                .save();

        new MainPage().checkThatTableContainsSpending(newDescription);
    }
}

