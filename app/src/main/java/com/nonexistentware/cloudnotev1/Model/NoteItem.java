package com.nonexistentware.cloudnotev1.Model;

public class NoteItem {
    private long id;
    private String noteTitle;
    private String noteBody;
    private String date;
    private String time;

    public NoteItem() {
    }

    public NoteItem(String noteTitle, String noteBody, String date, String time) {
        this.noteTitle = noteTitle;
        this.noteBody = noteBody;
        this.date = date;
        this.time = time;
    }

    public NoteItem(long id, String noteTitle, String noteBody, String date, String time) {
        this.id = id;
        this.noteTitle = noteTitle;
        this.noteBody = noteBody;
        this.date = date;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteBody() {
        return noteBody;
    }

    public void setNoteBody(String noteBody) {
        this.noteBody = noteBody;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
