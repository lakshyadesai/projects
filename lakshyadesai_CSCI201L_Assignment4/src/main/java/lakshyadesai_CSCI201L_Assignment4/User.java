package lakshyadesai_CSCI201L_Assignment4;

public class User {
    String username;
    String password;
    String email;
    double balance;

    // Constructor
    public User(String username, String password, String email, double balance) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.balance = balance;
    }

    // Getters and setters for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getters and setters for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getters and setters for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getters and setters for balance
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}

