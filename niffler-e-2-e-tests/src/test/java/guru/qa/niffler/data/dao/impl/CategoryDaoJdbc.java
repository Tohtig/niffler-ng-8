package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class CategoryDaoJdbc implements CategoryDao {

  private static final Config CFG = Config.getInstance();

  @Override
  public CategoryEntity create(CategoryEntity category) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "INSERT INTO category (username, name, archived) " +
            "VALUES (?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, category.getUsername());
      ps.setString(2, category.getName());
      ps.setBoolean(3, category.isArchived());

      ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can`t find id in ResultSet");
        }
      }
      category.setId(generatedKey);
      return category;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM category WHERE id = ?"
    )) {
      ps.setObject(1, id);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          CategoryEntity ce = new CategoryEntity();
          ce.setId(rs.getObject("id", UUID.class));
          ce.setUsername(rs.getString("username"));
          ce.setName(rs.getString("name"));
          ce.setArchived(rs.getBoolean("archived"));
          return Optional.of(ce);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
      try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
              "SELECT * FROM category WHERE username = ? AND name = ?"
      )) {
        ps.setString(1, username);
        ps.setString(2, categoryName);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            return Optional.of(mapResultSetToCategoryEntity(rs));
          } else {
            return Optional.empty();
          }
        }
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
  }

  @Override
  public List<CategoryEntity> findAllByUsername(String username) {
    List<CategoryEntity> categories = new ArrayList<>();
      try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
              "SELECT * FROM category WHERE username = ?"
      )) {
        ps.setString(1, username);
        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            categories.add(mapResultSetToCategoryEntity(rs));
          }
        }
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    return categories;
  }

  @Override
  public void deleteCategory(CategoryEntity category) {
      try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
              "DELETE FROM category WHERE id = ?"
      )) {
        ps.setObject(1, category.getId());
        ps.executeUpdate();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
  }

  @Override
  public CategoryEntity update(CategoryEntity category) {
    String sql = "UPDATE category SET name = ?, archived = ? WHERE id = ? AND username = ?";
    try ( PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(sql)) {
      ps.setString(1, category.getName());
      ps.setBoolean(2, category.isArchived());
      ps.setObject(3, category.getId());
      ps.setString(4, category.getUsername());

      int updated = ps.executeUpdate();
      if (updated == 0) {
        throw new RuntimeException("Не удалось обновить категорию с id: " + category.getId());
      }

      return findCategoryById(category.getId()).orElseThrow(() ->
              new RuntimeException("Категория не найдена после обновления"));
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при обновлении категории", e);
    }
  }

  @Override
  public List<CategoryEntity> findAll() {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "SELECT * FROM category")) {
      ps.execute();
      List<CategoryEntity> result = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          CategoryEntity ce = new CategoryEntity();
          ce.setId(rs.getObject("id", UUID.class));
          ce.setUsername(rs.getString("username"));
          ce.setName(rs.getString("name"));
          ce.setArchived(rs.getBoolean("archived"));
          result.add(ce);
        }
      }
      return result;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private CategoryEntity mapResultSetToCategoryEntity(ResultSet rs) throws SQLException {
    CategoryEntity category = new CategoryEntity();
    category.setId(rs.getObject("id", UUID.class));
    category.setName(rs.getString("name"));
    category.setUsername(rs.getString("username"));
    return category;
  }
}
