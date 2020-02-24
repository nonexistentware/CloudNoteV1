package com.nonexistentware.cloudnotev1.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class NoteItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "note_body")
    public String noteBody;

    @ColumnInfo(name = "note_title")
    public String noteTitle;

    @ColumnInfo(name = "note_date")
    public long noteDate;

    @Ignore
    public boolean checked = false;

    public NoteItem() {

    }

    public NoteItem(String noteBody, String noteTitle, long noteDate) {
        this.noteBody = noteBody;
        this.noteTitle = noteTitle;
        this.noteDate = noteDate;
    }

    public String getNoteBody() {
        return noteBody;
    }

    public void setNoteBody(String noteBody) {
        this.noteBody = noteBody;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public long getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(long noteDate) {
        this.noteDate = noteDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
