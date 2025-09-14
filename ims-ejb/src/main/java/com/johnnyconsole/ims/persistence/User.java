package com.johnnyconsole.ims.persistence;

import at.favre.lib.crypto.bcrypt.BCrypt;

import javax.persistence.*;

@Entity
@Table(name="ims_users")
@NamedQueries({
        @NamedQuery(name="User.FindByUsername", query="SELECT u FROM User u WHERE u.username = :username"),
        @NamedQuery(name="User.FindCount", query="SELECT COUNT(u) FROM User u"),
        @NamedQuery(name="User.FindAllExcept", query="SELECT u FROM User u WHERE u.username != :username")
})
public class User {

    @Id
    public String username;
    public String name;
    private String password;
    public int accessLevel;

    public User() {}

    public User(String username, String name, String password, int accessLevel) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.accessLevel = accessLevel;
    }

    public boolean verifyPassword(String password) {
        return BCrypt.verifyer().verify(password.toCharArray(), this.password).verified;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
