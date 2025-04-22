package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;

public class SpendDbClient {

  private final SpendDao spendDao = new SpendDaoJdbc();
  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  public SpendJson createSpend(SpendJson spend) {
    SpendEntity spendEntity = SpendEntity.fromJson(spend);
    if (spendEntity.getCategory().getId() == null) {
      CategoryEntity category = spendEntity.getCategory();
      // Check if category already exists
      Optional<CategoryEntity> existingCategory = categoryDao.findCategoryByUsernameAndCategoryName(
          category.getUsername(), 
          category.getName()
      );

      if (existingCategory.isPresent()) {
        // Use existing category
        spendEntity.setCategory(existingCategory.get());
      } else {
        // Create new category
        CategoryEntity categoryEntity = categoryDao.create(category);
        spendEntity.setCategory(categoryEntity);
      }
    }
    return SpendJson.fromEntity(
        spendDao.create(spendEntity)
    );
  }

  public CategoryJson createCategory(CategoryJson category) {
    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
    return CategoryJson.fromEntity(
            categoryDao.create(categoryEntity)
    );
  }

  public CategoryJson updateCategory(CategoryJson category) {
    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
    return CategoryJson.fromEntity(
            categoryDao.update(categoryEntity)
    );
  }
}
