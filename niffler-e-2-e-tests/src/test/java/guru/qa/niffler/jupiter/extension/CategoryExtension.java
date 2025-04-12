package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
  private final SpendApiClient spendApiClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
            .ifPresent(anno -> {
              CategoryJson categoryJson = new CategoryJson(
                      null,
                      RandomDataUtils.randomCategoryName(),
                      anno.username(),
                      anno.archived()
              );

              CategoryJson created = spendApiClient.addCategory(categoryJson);
              if (anno.archived()) {
                CategoryJson archvedCategory = new CategoryJson(
                        created.id(),
                        created.name(),
                        created.username(),
                        true // устанавливаем archived = true
                );
                created = spendApiClient.updateCategory(archvedCategory);
              }
              context.getStore(NAMESPACE).put(context.getUniqueId(), created);
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
      spendApiClient.updateCategory(archivedCategory);
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
