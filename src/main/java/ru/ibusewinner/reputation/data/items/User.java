package ru.ibusewinner.reputation.data.items;

import java.util.List;

public class User {
    private long id;
    private String nickname;
    private long rep;
    private List<User> actedUsers;

    public User(long id, String nickname, long rep, List<User> actedUsers) {
        this.id = id;
        this.nickname = nickname;
        this.rep = rep;
        this.actedUsers = actedUsers;
    }

    public long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public List<User> getActedUsers() {
        return actedUsers;
    }

    public long getRep() {
        return rep;
    }

    public boolean addActedUser(User actor) {
        return this.actedUsers.add(actor);
    }

    public boolean removeActedUser(User actor) {
        return this.actedUsers.remove(actor);
    }

    public void addRep(long amount) {
        this.rep += amount;
    }

    public void removeRep(long amount) {
        this.rep -= amount;
    }
}
