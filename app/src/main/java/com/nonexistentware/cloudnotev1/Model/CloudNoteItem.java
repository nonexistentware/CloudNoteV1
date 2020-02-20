package com.nonexistentware.cloudnotev1.Model;

public class CloudNoteItem {

    public String noteBody;
    public String noteImage;
    public String title;

    public CloudNoteItem() {

    }

    public CloudNoteItem(String paramString1, String paramString2, String paramString3) {
        this.title = paramString1;
        this.noteBody = paramString2;
        this.noteImage = paramString3;
    }

    public String getNoteBody() {
        return this.noteBody;
    }

    public String getNoteImage() {
        return this.noteImage;
    }

    public String getTitle() {
        return this.title;
    }

    public void setNoteBody(String paramString) {
        this.noteBody = paramString;
    }

    public void setNoteImage(String paramString) {
        this.noteImage = paramString;
    }

    public void setTitle(String paramString) {
        this.title = paramString;
    }

}
