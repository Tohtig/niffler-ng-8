package guru.qa.niffler.config;

enum LocalConfig implements Config {
  instance;

  @Override
  public String frontUrl() {
    return "http://127.0.0.1:3000/";
  }

  @Override
  public String ghUrl() {
    return "https://api.github.com/";
  }

  @Override
  public String profileUrl() {
    return frontUrl() + "profile";
  }

  @Override
  public String friendsUrl() {
    return frontUrl() + "people/friends";
  }

  @Override
  public String spendUrl() {
    return "http://127.0.0.1:8093/";
  }
}
