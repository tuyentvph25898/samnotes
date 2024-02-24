package com.thinkdiffai.cloud_note.Model;

import com.google.gson.annotations.SerializedName;

public class Model_List_Note {
    @SerializedName("idNote")
    private int id;
    @SerializedName("title")

    private String title;
    @SerializedName("color")

    private Color color;
    @SerializedName("type")

    private String type;
    @SerializedName("dueAt")

    private String duaAt;
    @SerializedName("createAt")

    private String createAt;
    @SerializedName("doneNote")

    private boolean doneNote;
    @SerializedName("idUser")

    private int idUser;
    @SerializedName("lock")

    private String lock;
    @SerializedName("pinned")

    private boolean pinned;
    @SerializedName("remindAt")

    private String reminAt;
    @SerializedName("notePublic")
    private int notePublic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuaAt() {
        return duaAt;
    }

    public void setDuaAt(String duaAt) {
        this.duaAt = duaAt;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public boolean getDoneNote() {
        return doneNote;
    }

    public void setDoneNote(boolean doneNote) {
        this.doneNote = doneNote;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }

    public boolean getPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public String getReminAt() {
        return reminAt;
    }

    public void setReminAt(String reminAt) {
        this.reminAt = reminAt;
    }

    public int getNotePublic() {
        return notePublic;
    }

    public void setNotePublic(int notePublic) {
        this.notePublic = notePublic;
    }
}
