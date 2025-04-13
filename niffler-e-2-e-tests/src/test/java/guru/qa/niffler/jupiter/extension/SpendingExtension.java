package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);
  private final SpendApiClient spendApiClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
            .ifPresent(userAnno -> {
              if (userAnno.spends() == null || userAnno.spends().length == 0) {
                return;
              }
              Spend spendAnno = userAnno.spends()[0];
              SpendJson spend = new SpendJson(
                      null,
                      new Date(),
                      new CategoryJson(
                              null,
                              spendAnno.category(),
                              userAnno.username(),
                              false
                      ),
                      spendAnno.currency(),
                      spendAnno.amount(),
                      spendAnno.description(),
                      userAnno.username()
              );
              SpendJson created = spendApiClient.addSpend(spend);
              context.getStore(NAMESPACE).put(context.getUniqueId(), created);
            });
}

@Override
public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
  return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
}

@Override
public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
  return extensionContext.getStore(SpendingExtension.NAMESPACE).get(extensionContext.getUniqueId(), SpendJson.class);
}
}
