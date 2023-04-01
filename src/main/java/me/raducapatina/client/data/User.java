package me.raducapatina.client.data;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class User implements Serializable {

    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private UserType type;
    private Set<Subject> subjects = new LinkedHashSet<>();
    private Set<Grade> grades = new LinkedHashSet<>();

    public User() {
    }

    public User(Long id, String username, String password, UserType type, Set<Subject> subjects) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.type = type;
        this.subjects = subjects;
    }

    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserType getType() {
        return type;
    }

    public User setType(UserType type) {
        this.type = type;
        return this;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public User setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
        return this;
    }

    public Set<Grade> getGrades() {
        return grades;
    }

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
}
