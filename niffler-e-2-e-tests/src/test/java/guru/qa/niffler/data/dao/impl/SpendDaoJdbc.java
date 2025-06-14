package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendDaoJdbc implements SpendDao {

  private static final Config CFG = Config.getInstance();

  @Override
  public SpendEntity create(SpendEntity spend) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                    "VALUES ( ?, ?, ?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, spend.getUsername());
      ps.setDate(2, spend.getSpendDate());
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());

      ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can`t find id in ResultSet");
        }
      }
      spend.setId(generatedKey);
      return spend;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "SELECT s.*, c.id as category_id, c.name as category_name, c.username as category_username " +
                    "FROM spend s JOIN category c ON s.category_id = c.id " +
                    "WHERE s.id = ?"
    )) {
      ps.setObject(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return Optional.of(mapResultSetToSpendEntity(rs));
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public List<SpendEntity> findAllByUsername(String username) {
    List<SpendEntity> spends = new ArrayList<>();
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "SELECT s.*, c.id as category_id, c.name as category_name, c.username as category_username " +
                    "FROM spend s JOIN category c ON s.category_id = c.id " +
                    "WHERE s.username = ?"
    )) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          spends.add(mapResultSetToSpendEntity(rs));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return spends;
  }

  @Override
  public void deleteSpend(SpendEntity spend) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "DELETE FROM spend WHERE id = ?"
    )) {
      ps.setObject(1, spend.getId());
      ps.executeUpdate();

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<SpendEntity> findAll() {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "SELECT * FROM spend")) {
      ps.execute();
      List<SpendEntity> result = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          SpendEntity se = new SpendEntity();
          se.setId(rs.getObject("id", UUID.class));
          se.setUsername(rs.getString("username"));
          se.setSpendDate(rs.getDate("spend_date"));
          se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          se.setAmount(rs.getDouble("amount"));
          se.setDescription(rs.getString("description"));
          CategoryEntity category = new CategoryEntity();
          category.setId(rs.getObject("category_id", UUID.class));
          se.setCategory(category);
          result.add(se);
        }
      }
      return result;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private SpendEntity mapResultSetToSpendEntity(ResultSet rs) throws SQLException {
    SpendEntity spend = new SpendEntity();
    spend.setId(rs.getObject("id", UUID.class));
    spend.setUsername(rs.getString("username"));
    spend.setSpendDate(rs.getDate("spend_date"));
    spend.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
    spend.setAmount(rs.getDouble("amount"));
    spend.setDescription(rs.getString("description"));

    CategoryEntity category = new CategoryEntity();
    category.setId(rs.getObject("category_id", UUID.class));
    category.setName(rs.getString("category_name"));
    category.setUsername(rs.getString("category_username"));

    spend.setCategory(category);
    return spend;
  }
}
