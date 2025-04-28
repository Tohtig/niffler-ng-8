package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

  private final DataSource dataSource;

  public CategoryDaoSpringJdbc(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public CategoryEntity create(CategoryEntity category) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
              "INSERT INTO category (name, username, archived) VALUES (?, ?, ?)",
              Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, category.getName());
      ps.setString(2, category.getUsername());
      ps.setBoolean(3, category.isArchived());
      return ps;
    }, kh);

    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    category.setId(generatedKey);
    return category;
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    return Optional.empty();
  }

  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    try {
      return Optional.ofNullable(
              jdbcTemplate.queryForObject(
                      "SELECT * FROM category WHERE username = ? AND name = ?",
                      CategoryEntityRowMapper.instance,
                      username, categoryName
              )
      );
    } catch (org.springframework.dao.EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<CategoryEntity> findAllByUsername(String username) {

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    return jdbcTemplate.query(
            "SELECT * FROM category WHERE username = ?",
            CategoryEntityRowMapper.instance,
            username
    );
  }

  @Override
  public CategoryEntity update(CategoryEntity category) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    jdbcTemplate.update(
            "UPDATE category SET category_name = ? WHERE id = ?",
            category.getName(),
            category.getId()
    );
    return findById(category.getId()).orElseThrow(() ->
            new RuntimeException("Категория не найдена после обновления"));
  }

  @Override
  public void deleteCategory(CategoryEntity category) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    jdbcTemplate.update(
            "DELETE FROM category WHERE id = ?",
            category.getId()
    );
  }

  public Optional<CategoryEntity> findById(UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    try {
      return Optional.ofNullable(
              jdbcTemplate.queryForObject(
                      "SELECT * FROM category WHERE id = ?",
                      CategoryEntityRowMapper.instance,
                      id
              )
      );
    } catch (org.springframework.dao.EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<CategoryEntity> findAll() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    return jdbcTemplate.query(
            "SELECT * FROM \"category\"",
            CategoryEntityRowMapper.instance
    );
  }
}