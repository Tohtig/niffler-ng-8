package guru.qa.niffler.jupiter.extension;

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

  public enum Type {
    EMPTY,
    WITH_FRIEND,
    WITH_INCOME_REQUEST,
    WITH_OUTCOME_REQUEST
  }

  public record StaticUser(
          String username,
          String password,
          String friend,         // друг (если есть)
          String income,         // входящий запрос
          String outcome         // исходящий запрос
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
    // Создаем или получаем HashMap для хранения пользователей
    // Приведение типов безопасно, и предупреждение будет подавлено. В данном случае это оправдано, так как метод
    // getOrComputeIfAbsent возвращает именно тот объект, который мы создаем в лямбда-выражении.
    @SuppressWarnings("unchecked")
    Map<Type, StaticUser> userMap = (Map<Type, StaticUser>) context.getStore(NAMESPACE)
            .getOrComputeIfAbsent(
                    context.getUniqueId(),
                    key -> new HashMap<>()
            );

    // Обрабатываем все параметры с аннотацией @UserType
    Arrays.stream(context.getRequiredTestMethod().getParameters())
            .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
            .forEach(parameter -> {
              UserType ut = parameter.getAnnotation(UserType.class);
              Type type = ut.value();
              Optional<StaticUser> user = Optional.empty();
              StopWatch sw = StopWatch.createStarted();

              // Ждем до 30 секунд, пытаясь получить пользователя из соответствующей очереди
              while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                user = switch (type) {
                  case EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
                  case WITH_FRIEND -> Optional.ofNullable(WITH_FRIEND_USERS.poll());
                  case WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUEST_USERS.poll());
                  case WITH_OUTCOME_REQUEST -> Optional.ofNullable(WITH_OUTCOME_REQUEST_USERS.poll());
                };
              }

              Allure.getLifecycle().updateTestCase(testCase ->
                      testCase.setStart(new Date().getTime())
              );

              if (user.isEmpty()) {
                throw new IllegalStateException("Не удалось получить пользователя типа " + type + " после 30 секунд ожидания.");
              }

              // Сохраняем пользователя в HashMap с ключом Type
              userMap.put(type, user.get());
            });
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    // Получаем HashMap с пользователями
    @SuppressWarnings("unchecked")
    Map<Type, StaticUser> map = (Map<Type, StaticUser>) context.getStore(NAMESPACE).get(
            context.getUniqueId(),
            Map.class
    );

    if (map != null) {
      // Возвращаем всех пользователей в соответствующие очереди
      for (Map.Entry<Type, StaticUser> entry : map.entrySet()) {
        Type type = entry.getKey();
        StaticUser user = entry.getValue();

        switch (type) {
          case EMPTY -> EMPTY_USERS.add(user);
          case WITH_FRIEND -> WITH_FRIEND_USERS.add(user);
          case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS.add(user);
          case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS.add(user);
        }
      }

      // Очищаем хранилище после использования
      context.getStore(NAMESPACE).remove(context.getUniqueId());
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
            && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
  }

  @Override
  public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    UserType ut = parameterContext.getParameter().getAnnotation(UserType.class);
    Type type = ut.value();

    @SuppressWarnings("unchecked")
    Map<Type, StaticUser> map = extensionContext.getStore(NAMESPACE).get(
            extensionContext.getUniqueId(),
            Map.class
    );

    // Получаем пользователя из HashMap по типу
    StaticUser user = map.get(type);
    if (user == null) {
      throw new ParameterResolutionException("Пользователь не найден для типа " + type);
    }

    return user;
  }
}
