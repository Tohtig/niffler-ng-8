package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import static guru.qa.niffler.data.Databases.transaction;

import java.util.Optional;

public class SpendDbClient {

  private static final Config CFG = Config.getInstance();

  public SpendJson createSpend(SpendJson spend) {
    return transaction(connection -> {
              SpendDao spendDao = new SpendDaoJdbc(connection);
              CategoryDao categoryDao = new CategoryDaoJdbc(connection);

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
            },
            CFG.spendJdbcUrl()
    );
  }

  public CategoryJson createCategory(CategoryJson category) {
    return transaction(connection -> {
      SpendDao spendDao = new SpendDaoJdbc(connection);
      CategoryDao categoryDao = new CategoryDaoJdbc(connection);
      CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
      return CategoryJson.fromEntity(
              categoryDao.create(categoryEntity)
      );
    }, CFG.spendJdbcUrl());
  }

  public CategoryJson updateCategory(CategoryJson category) {
    return transaction(connection -> {
      CategoryDao categoryDao = new CategoryDaoJdbc(connection);
      CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
      return CategoryJson.fromEntity(
              categoryDao.update(categoryEntity)
      );
    }, CFG.spendJdbcUrl());
  }
}
