package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);
  private static final long TIMEOUT_SECONDS = 30;
  private static final Logger logger = LoggerFactory.getLogger(UsersQueueExtension.class);

  public enum Type {
    EMPTY,
    WITH_FRIEND,
    WITH_INCOME_REQUEST,
    WITH_OUTCOME_REQUEST
  }

  public record StaticUser(
          String username,
          String password,
          String friend,
          String income,
          String outcome
  ) {
  }

  private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

  static {
    EMPTY_USERS.add(new StaticUser("bee", "12345", null, null, null));
    WITH_FRIEND_USERS.add(new StaticUser("duck", "12345", "dima", null, null));
    WITH_INCOME_REQUEST_USERS.add(new StaticUser("dima", "12345", null, "bee", null));
    WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("barsik", "12345", null, null, "bill"));
  }

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface UserType {
    Type value() default Type.EMPTY;
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    Map<UserType, StaticUser> userMap = getTestStore(context);

    // Устанавливаем время начала в Allure один раз
    Allure.getLifecycle().updateTestCase(testCase ->
            testCase.setStart(System.currentTimeMillis())
    );

    // Находим и обрабатываем все параметры с аннотацией @UserType
    findUserTypeParameters(context)
            .forEach(userType -> processUserParameter(userType, userMap));
  }

  private List<UserType> findUserTypeParameters(ExtensionContext context) {
    return Arrays.stream(context.getRequiredTestMethod().getParameters())
            .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
            .map(p -> p.getAnnotation(UserType.class))
            .toList();
  }

  private void processUserParameter(UserType userType, Map<UserType, StaticUser> userMap) {
    StaticUser user = waitForAvailableUser(userType.value());
    userMap.put(userType, user);
  }

  private StaticUser waitForAvailableUser(Type type) {
    StopWatch sw = StopWatch.createStarted();
    Optional<StaticUser> user = Optional.empty();

    while (sw.getTime(TimeUnit.SECONDS) < TIMEOUT_SECONDS) {
      user = pollUserByType(type);

      if (user.isPresent()) {
        return user.get();
      }

      long elapsedSeconds = sw.getTime(TimeUnit.SECONDS);
      long remainingSeconds = TIMEOUT_SECONDS - elapsedSeconds;
      logger.debug("Ожидание пользователя типа {}, прошло {} сек, осталось {} сек",
              type, elapsedSeconds, remainingSeconds);

      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Ожидание пользователя было прервано", e);
      }
    }

    throw new IllegalStateException(String.format(
            "Не удалось получить пользователя типа %s после %d секунд",
            type, TIMEOUT_SECONDS));
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    Map<UserType, StaticUser> userMap = getTestStore(context);

    userMap.forEach((ut, user) -> returnUserByType(ut.value(), user));

    // Очищаем хранилище
    context.getStore(NAMESPACE).remove(context.getUniqueId());
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
    return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
            && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
  }

  @Override
  public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
    UserType annotation = parameterContext.getParameter().getAnnotation(UserType.class);
    Map<UserType, StaticUser> userMap = getTestStore(extensionContext);
    StaticUser user = userMap.get(annotation);
    return user;
  }

  // Вспомогательные методы

  @SuppressWarnings("unchecked")
  private Map<UserType, StaticUser> getTestStore(ExtensionContext context) {
    return (Map<UserType, StaticUser>) context.getStore(NAMESPACE)
            .getOrComputeIfAbsent(context.getUniqueId(), k -> new HashMap<>());
  }

  // Метод для возврата очереди по заданному типу
  private Queue<StaticUser> getQueueByType(Type type) {
    return switch (type) {
      case EMPTY -> EMPTY_USERS;
      case WITH_FRIEND -> WITH_FRIEND_USERS;
      case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS;
      case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS;
    };
  }

  // Используется для выбора пользователя из соответствующей очереди
  private Optional<StaticUser> pollUserByType(Type type) {
    return Optional.ofNullable(getQueueByType(type).poll());
  }

  // Пример использования при возврате пользователя в очередь
  private void returnUserByType(Type type, StaticUser user) {
    getQueueByType(type).add(user);
  }
}