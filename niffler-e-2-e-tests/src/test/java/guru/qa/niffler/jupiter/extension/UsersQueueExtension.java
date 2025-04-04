package guru.qa.niffler.jupiter.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

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
    // Получаем или создаем хранилище для текущего теста
    Map<UserType, StaticUser> userMap = getTestStore(context);

    // Обрабатываем параметры метода с аннотацией @UserType
    Arrays.stream(context.getRequiredTestMethod().getParameters())
            .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
            .map(p -> p.getAnnotation(UserType.class))
            .forEach(ut -> {
              Optional<StaticUser> user = Optional.empty();
              StopWatch sw = StopWatch.createStarted();

              // Пытаемся получить пользователя с таймаутом
              while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < TIMEOUT_SECONDS) {
                user = pollUserByType(ut.value());

                if (user.isEmpty()) {
                  logger.debug("Ожидание пользователя типа {}, прошло {} сек",
                          ut.value(), sw.getTime(TimeUnit.SECONDS));
                  try {
                    Thread.sleep(200);
                  } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                  }
                }
              }

              // Устанавливаем время начала в Allure
              Allure.getLifecycle().updateTestCase(testCase ->
                      testCase.setStart(System.currentTimeMillis())
              );

              // Сохраняем пользователя или выбрасываем исключение
              user.ifPresentOrElse(
                      u -> userMap.put(ut, u),
                      () -> { throw new IllegalStateException("Не удалось получить пользователя типа "
                              + ut.value() + " после " + TIMEOUT_SECONDS + " секунд."); }
              );
            });
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    Map<UserType, StaticUser> userMap = getTestStore(context);

    // Возвращаем всех использованных пользователей обратно в очереди
    userMap.forEach((ut, user) -> {
      switch (ut.value()) {
        case EMPTY -> EMPTY_USERS.add(user);
        case WITH_FRIEND -> WITH_FRIEND_USERS.add(user);
        case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS.add(user);
        case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS.add(user);
      }
    });

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
    if (user == null) {
      throw new ParameterResolutionException("Не найден пользователь для параметра с аннотацией " + annotation);
    }

    return user;
  }

  // Вспомогательные методы

  @SuppressWarnings("unchecked")
  private Map<UserType, StaticUser> getTestStore(ExtensionContext context) {
    return (Map<UserType, StaticUser>) context.getStore(NAMESPACE)
            .getOrComputeIfAbsent(context.getUniqueId(), k -> new HashMap<>());
  }

  private Optional<StaticUser> pollUserByType(Type type) {
    return switch (type) {
      case EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
      case WITH_FRIEND -> Optional.ofNullable(WITH_FRIEND_USERS.poll());
      case WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUEST_USERS.poll());
      case WITH_OUTCOME_REQUEST -> Optional.ofNullable(WITH_OUTCOME_REQUEST_USERS.poll());
    };
  }
}