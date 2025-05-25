package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.CurrencyValues;
import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserdataUserDaoJdbc implements UserdataUserDao {

  private static final Config CFG = Config.getInstance();

  @Override
  public UserEntity create(UserEntity user) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
            "INSERT INTO \"user\" (username, currency, firstname, surname, full_name, photo, photo_small) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getCurrency().name());
      ps.setString(3, user.getFirstname());
      ps.setString(4, user.getSurname());
      ps.setString(5, user.getFullname());
      ps.setBytes(6, user.getPhoto());
      ps.setBytes(7, user.getPhotoSmall());
      ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Не удалось получить id из ResultSet");
        }
      }
      user.setId(generatedKey);
      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public Optional<UserEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
            "SELECT * FROM \"user\" WHERE id = ?"
    )) {
      ps.setObject(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return Optional.of(mapResultSetToUserEntity(rs));
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
            "SELECT * FROM \"user\" WHERE username = ?"
    )) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return Optional.of(mapResultSetToUserEntity(rs));
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(UserEntity user) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
            "DELETE FROM \"user\" WHERE id = ?"
    )) {
      ps.setObject(1, user.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public List<UserEntity> findAll() {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
            "SELECT * FROM spend")) {
      ps.execute();
      List<UserEntity> result = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          UserEntity ue = new UserEntity();
          ue.setId(rs.getObject("id", UUID.class));
          ue.setUsername(rs.getString("username"));
          ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          ue.setFirstname(rs.getString("firstname"));
          ue.setSurname(rs.getString("surname"));
          ue.setFullname(rs.getString("full_name"));
          ue.setPhoto(rs.getBytes("photo"));
          ue.setPhotoSmall(rs.getBytes("photo_small"));
          result.add(ue);
        }
      }
      return result;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private UserEntity mapResultSetToUserEntity(ResultSet rs) throws SQLException {
    UserEntity user = new UserEntity();
    user.setId(rs.getObject("id", UUID.class));
    user.setUsername(rs.getString("username"));
    user.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
    user.setFirstname(rs.getString("firstname"));
    user.setSurname(rs.getString("surname"));
    user.setFullname(rs.getString("full_name"));
    user.setPhoto(rs.getBytes("photo"));
    user.setPhotoSmall(rs.getBytes("photo_small"));
    return user;
  }
}
