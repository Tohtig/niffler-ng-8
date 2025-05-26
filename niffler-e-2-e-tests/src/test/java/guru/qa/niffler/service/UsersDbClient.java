package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;


public class UsersDbClient {

  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  // Spring JDBC DAO
  private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
  private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
  private final UserdataUserDao udUserDao = new UserdataUserDaoSpringJdbc();

  // JDBC DAO
  private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();
  private final UserdataUserDao udUserDaoJdbc = new UserdataUserDaoJdbc();

  private final TransactionTemplate txTemplate = new TransactionTemplate(
          new JdbcTransactionManager(
                  DataSources.dataSource(CFG.authJdbcUrl())
          )
  );

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
          CFG.authJdbcUrl(),
          CFG.userdataJdbcUrl()
  );

  private final TransactionTemplate xaTransactionTemplateChained = new TransactionTemplate(
          new ChainedTransactionManager(
                  new JdbcTransactionManager(DataSources.dataSource(CFG.authJdbcUrl())),
                  new JdbcTransactionManager(DataSources.dataSource(CFG.userdataJdbcUrl()))
          )
  );

  // Spring JDBC с транзакциями (по умолчанию)
  public UserJson createUser(UserJson user) {
    return xaCreateUserSpringJdbc(user);
  }

  // JDBC без транзакций
  public UserJson createUserJdbc(UserJson user) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(user.username());
    authUser.setPassword(pe.encode("12345"));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);
    authUser.setAuthorities(
            Arrays.stream(Authority.values())
                    .map(e -> {
                      AuthorityEntity ae = new AuthorityEntity();
                      ae.setAuthority(e);
                      ae.setUser(authUser);
                      return ae;
                    })
                    .toList()
    );

    authUserRepository.create(authUser);
    return UserJson.fromEntity(
            udUserDaoJdbc.create(UserEntity.fromJson(user)),
            null
    );
  }

  // JDBC с XA транзакциями
  public UserJson xaCreateUserJdbc(UserJson user) {
    return xaTransactionTemplate.execute(() -> {
      AuthUserEntity authUser = new AuthUserEntity();
      authUser.setUsername(user.username());
      authUser.setPassword(pe.encode("12345"));
      authUser.setEnabled(true);
      authUser.setAccountNonExpired(true);
      authUser.setAccountNonLocked(true);
      authUser.setCredentialsNonExpired(true);
      authUser.setAuthorities(
              Arrays.stream(Authority.values())
                      .map(e -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setAuthority(e);
                        ae.setUser(authUser);
                        return ae;
                      })
                      .toList()
      );

      authUserRepository.create(authUser);
      return UserJson.fromEntity(
              udUserDaoJdbc.create(UserEntity.fromJson(user)),
              null
      );
    });
  }

  // Spring JDBC с XA транзакциями
  public UserJson xaCreateUserSpringJdbc(UserJson user) {
    return xaTransactionTemplate.execute(() -> {
      AuthUserEntity authUser = new AuthUserEntity();
      authUser.setUsername(user.username());
      authUser.setPassword(pe.encode("12345"));
      authUser.setEnabled(true);
      authUser.setAccountNonExpired(true);
      authUser.setAccountNonLocked(true);
      authUser.setCredentialsNonExpired(true);
      authUser.setAuthorities(
              Arrays.stream(Authority.values())
                      .map(e -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setAuthority(e);
                        ae.setUser(authUser);
                        return ae;
                      })
                      .toList()
      );

      authUserRepository.create(authUser);
      return UserJson.fromEntity(
              udUserDao.create(UserEntity.fromJson(user)),
              null
      );
    });
  }

  // Spring JDBC без транзакций
  public UserJson createUserSpringJdbc(UserJson user) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(user.username());
    authUser.setPassword(pe.encode("12345"));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);
    authUser.setAuthorities(
            Arrays.stream(Authority.values())
                    .map(e -> {
                      AuthorityEntity ae = new AuthorityEntity();
                      ae.setAuthority(e);
                      ae.setUser(authUser);
                      return ae;
                    })
                    .toList()
    );

    authUserRepository.create(authUser);
    return UserJson.fromEntity(
            udUserDao.create(UserEntity.fromJson(user)),
            null
    );
  }

  // Spring JDBC с ChainedTransactionManager
  public UserJson createUserChainedTxManager(UserJson user) {
    return xaTransactionTemplateChained.execute(status -> {
      AuthUserEntity authUser = new AuthUserEntity();
      authUser.setUsername(user.username());
      authUser.setPassword(pe.encode("12345"));
      authUser.setEnabled(true);
      authUser.setAccountNonExpired(true);
      authUser.setAccountNonLocked(true);
      authUser.setCredentialsNonExpired(true);
      authUser.setAuthorities(
              Arrays.stream(Authority.values())
                      .map(e -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setAuthority(e);
                        ae.setUser(authUser);
                        return ae;
                      })
                      .toList()
      );

      authUserRepository.create(authUser);
      return UserJson.fromEntity(
              udUserDao.create(UserEntity.fromJson(user)),
              null
      );
    });
  }

}
