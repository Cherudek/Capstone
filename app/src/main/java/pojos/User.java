package pojos;

public class User {

  public String username;
  public String email;
  public String id;

  public User() {
    // Default constructor required for calls to DataSnapshot.getValue(User.class)
  }

  public User(String username, String email, String id) {
    this.username = username;
    this.email = email;
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
