package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
  private final SpendDbClient spendDbClient = new SpendDbClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
            .ifPresent(userAnno -> {
              if (userAnno.categories() == null || userAnno.categories().length == 0) {
                return;
              }
              Category categoryAnno = userAnno.categories()[0];
              CategoryJson category = new CategoryJson(
                      null,
                      RandomDataUtils.randomCategoryName(),
                      userAnno.username(),
                      categoryAnno.archived()
              );

              CategoryJson created = spendDbClient.createCategory(category);
              if (categoryAnno.archived()) {
                CategoryJson archvedCategory = new CategoryJson(
                        created.id(),
                        created.name(),
                        created.username(),
                        true // устанавливаем archived = true
                );
                created = spendDbClient.updateCategory(archvedCategory);
              }
              context.getStore(NAMESPACE).put(
                      context.getUniqueId(),
                      created
              );
            });
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    CategoryJson category = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
    if (category != null && !category.archived()) {
      // Создаем объект с archived = true
      CategoryJson archivedCategory = new CategoryJson(
              category.id(),
              category.name(),
              category.username(),
              true // устанавливаем archived = true
      );
      spendDbClient.updateCategory(archivedCategory);
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
  }

  @Override
  public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(CategoryExtension.NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
  }
}
