package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.util.List;
import java.util.Optional;

public interface CategoryDao {
  CategoryEntity create(CategoryEntity category);

  Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName);

  List<CategoryEntity> findAllByUsername(String username);

  void deleteCategory(CategoryEntity category);
}