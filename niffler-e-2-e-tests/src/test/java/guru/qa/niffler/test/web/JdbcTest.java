package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class JdbcTest {

  // Проверяет создание траты с категорией через транзакцию (SpendDbClient.createSpend)
  @Test
  void txTest() {
    SpendDbClient spendDbClient = new SpendDbClient();

    SpendJson spend = spendDbClient.createSpend(
            new SpendJson(
                    null,
                    new Date(),
                    new CategoryJson(
                            null,
                            "cat-name-tx-2",
                            "duck",
                            false
                    ),
                    CurrencyValues.RUB,
                    1000.0,
                    "spend-name-tx",
                    null
            )
    );

    System.out.println(spend);
  }

  // Проверяет создание пользователя через Spring JDBC с транзакцией (UsersDbClient.createUser)
  @Test
  void springJdbcTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUser(
            new UserJson(
                    null,
                    "valentin-4",
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null
            )
    );
    System.out.println(user);
  }

  // Проверяет создание пользователя через JDBC с XA-транзакцией (UsersDbClient.xaCreateUserJdbc)
  @Test
  void xaCreateUserJdbcTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.xaCreateUserJdbc(
            new UserJson(
                    null,
                    "xaCreateUserJdbcTest",
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null
            )
    );
    System.out.println(user);
  }

  // Проверяет создание пользователя через JDBC без транзакций (UsersDbClient.createUserJdbc)
  @Test
  void createUserJdbcTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUserJdbc(
            new UserJson(
                    null,
                    "createUserJdbcTest",
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null
            )
    );
    System.out.println(user);
  }

  // Проверяет создание пользователя через Spring JDBC с XA-транзакцией (UsersDbClient.xaCreateUserSpringJdbc)
  @Test
  void xaCreateUserSpringJdbcTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.xaCreateUserSpringJdbc(
            new UserJson(
                    null,
                    "xaCreateUserSpringJdbcTest",
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null
            )
    );
    System.out.println(user);
  }

  // Проверяет создание пользователя через Spring JDBC без транзакций (UsersDbClient.createUserSpringJdbc)
  @Test
  void createUserSpringJdbcTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUserSpringJdbc(
            new UserJson(
                    null,
                    "createUserSpringJdbcTest",
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null
            )
    );
    System.out.println(user);
  }

  // Проверяет создание пользователя через Spring JDBC с ChainedTransactionManager (UsersDbClient.createUserChainedTxManager)
  @Test
  void createUserChainedTxManagerTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUserChainedTxManager(
            new UserJson(
                    null,
                    "createUserChainedTxManagerTest",
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null
            )
    );
    System.out.println(user);
  }
}