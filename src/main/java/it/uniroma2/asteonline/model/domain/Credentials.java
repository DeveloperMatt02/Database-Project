package it.uniroma2.asteonline.model.domain;

public class Credentials {

    private final String username;
    private final String password;
    private final Role role;
    private final String cf;

    public Credentials(String username, String password, Role role, String cf) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.cf = cf;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public String getCF() {
        return cf;
    }

}
