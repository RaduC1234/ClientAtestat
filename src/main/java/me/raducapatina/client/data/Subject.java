package me.raducapatina.client.data;

import java.util.LinkedHashSet;
import java.util.Set;


public class Subject {

    private Long id;
    private Set<User> users = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public Subject setId(Long id) {
        this.id = id;
        return this;
    }

    public Set<User> getUsers() {
        return users;
    }

    public Subject setUsers(Set<User> users) {
        this.users = users;
        return this;
    }
}