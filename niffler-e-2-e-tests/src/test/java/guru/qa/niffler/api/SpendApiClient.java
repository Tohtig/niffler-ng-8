package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient {

  private final Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(Config.getInstance().spendUrl())
          .addConverterFactory(JacksonConverterFactory.create())
          .build();

  private final SpendApi spendApi = retrofit.create(SpendApi.class);

  public SpendJson addSpend(SpendJson spend) {
    Response<SpendJson> response;
    try {
      response = spendApi.addSpend(spend).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(201, response.code());
    return response.body();
  }

  public SpendJson getSpendById(String id, String username) {
    Response<SpendJson> response;
    try {
      response = spendApi.getSpend(id, username).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public List<SpendJson> getSpends(String username, CurrencyValues filterCurrency, Date from, Date to) {
    Response<List<SpendJson>> response;
    try {
      String currencyName = filterCurrency != null ? filterCurrency.name() : null;
      response = spendApi.getSpends(username, currencyName, from, to).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public void deleteSpends(String username, List<String> ids) {
    Response<Void> response;
    try {
      response = spendApi.deleteSpends(username, ids).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(202, response.code());
  }

  public SpendJson editSpend(SpendJson spend) {
    Response<SpendJson> response;
    try {
      response = spendApi.editSpend(spend).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public List<CategoryJson> getCategories(String username, boolean excludeArchived) {
    Response<List<CategoryJson>> response;
    try {
      response = spendApi.getCategories(username, excludeArchived).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public CategoryJson addCategory(CategoryJson category) {
    Response<CategoryJson> response;
    try {
      response = spendApi.addCategory(category).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public CategoryJson updateCategory(CategoryJson category) {
    Response<CategoryJson> response;
    try {
      response = spendApi.updateCategory(category).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }
}
