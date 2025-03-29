package guru.qa.niffler.jupiter;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.UUID;

public class CreateCategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateCategoryExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(anno -> {
                    CategoryJson categoryJson = new CategoryJson(
                            null,
                            getRandomName(),
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

    private String getRandomName() {
        return "Category_" + UUID.randomUUID().toString().substring(0, 8);
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
}
