package me.raducapatina.client.data;

public class Grade {

    private String mark;
    private String notes;
    private User user;
    private User teacher;

    public Grade(String mark, String notes, User user, User teacher) {
        this.mark = mark;
        this.notes = notes;
        this.user = user;
        this.teacher = teacher;
    }

    public String getMark() {
        return mark;
    }

    public Grade setMark(String mark) {
        this.mark = mark;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public Grade setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Grade setUser(User user) {
        this.user = user;
        return this;
    }

    public User getTeacher() {
        return teacher;
    }

    public Grade setTeacher(User teacher) {
        this.teacher = teacher;
        return this;
    }
}
