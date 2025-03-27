package guru.qa.niffler.jupiter;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.UUID;

public class CreateCategoryExtension implements BeforeEachCallback {

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
                    context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                });
    }

    private String getRandomName() {
        return "Category_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
