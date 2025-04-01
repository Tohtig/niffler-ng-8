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

  public record StaticUser(String username, String password, boolean empty) {
  }

  private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> NOT_EMPTY_USERS = new ConcurrentLinkedQueue<>();

  static {
    EMPTY_USERS.add(new StaticUser("bee", "12345", true));
    NOT_EMPTY_USERS.add(new StaticUser("duck", "12345", false));
    NOT_EMPTY_USERS.add(new StaticUser("dima", "12345", false));
  }

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface UserType {
    boolean empty() default true;
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    // Создаем HashMap для хранения всех пользователей, связанных с этим тестом.
    Map<UserType, StaticUser> userMap = context.getStore(NAMESPACE)
            .getOrComputeIfAbsent(
                    context.getUniqueId(),
                    key -> new HashMap<UserType, StaticUser>(),
                    Map.class
            );

    // Обрабатываем все параметры тестового метода, аннотированные @UserType
    Arrays.stream(context.getRequiredTestMethod().getParameters())
            .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
            .forEach(parameter -> {
              UserType ut = parameter.getAnnotation(UserType.class);
              Optional<StaticUser> user = Optional.empty();
              StopWatch sw = StopWatch.createStarted();
              while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                user = ut.empty()
                        ? Optional.ofNullable(EMPTY_USERS.poll())
                        : Optional.ofNullable(NOT_EMPTY_USERS.poll());
              }
              Allure.getLifecycle().updateTestCase(testCase ->
                      testCase.setStart(new Date().getTime())
              );
              if (user.isEmpty()) {
                throw new IllegalStateException("Невозможно получить пользователя после 30 секунд.");
              }
              userMap.put(ut, user.get());
            });
    /*
    Arrays.stream(context.getRequiredTestMethod().getParameters())
        .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
        .findFirst()
        .map(p -> p.getAnnotation(UserType.class))
        .ifPresent(ut -> {
          Optional<StaticUser> user = Optional.empty();
          StopWatch sw = StopWatch.createStarted();
          while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
            user = ut.empty()
                ? Optional.ofNullable(EMPTY_USERS.poll())
                : Optional.ofNullable(NOT_EMPTY_USERS.poll());
          }
          Allure.getLifecycle().updateTestCase(testCase ->
              testCase.setStart(new Date().getTime())
          );
          user.ifPresentOrElse(
              u ->
                  context.getStore(NAMESPACE).put(
                      context.getUniqueId(),
                      u
                  ),
              () -> {
                throw new IllegalStateException("Can`t obtain user after 30s.");
              }
          );
        });
     */
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    // Получаем HashMap из контекста.
    Map<UserType, StaticUser> map = context.getStore(NAMESPACE)
            .get(context.getUniqueId(), Map.class);
    if (map != null) {
      for (Map.Entry<UserType, StaticUser> entry : map.entrySet()) {
        if (entry.getValue().empty()) {
          EMPTY_USERS.add(entry.getValue());
        } else {
          NOT_EMPTY_USERS.add(entry.getValue());
        }
      }
      // Очистка хранилища для данного теста.
      context.getStore(NAMESPACE).remove(context.getUniqueId());
    }
    /*
    StaticUser user = context.getStore(NAMESPACE).get(
        context.getUniqueId(),
        StaticUser.class
    );
    if (user.empty()) {
      EMPTY_USERS.add(user);
    } else {
      NOT_EMPTY_USERS.add(user);
    }
     */
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
        && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
  }

  @Override
  public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    UserType ut = parameterContext.getParameter().getAnnotation(UserType.class);
    Map<UserType, StaticUser> map = extensionContext.getStore(NAMESPACE)
            .get(extensionContext.getUniqueId(), Map.class);
    StaticUser user = map.get(ut);
    if (user == null) {
      throw new ParameterResolutionException("Нет соответствующего пользователя для параметра с аннотацией " + ut);
    }
    return user;
    /*
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), StaticUser.class);
     */
  }
}
