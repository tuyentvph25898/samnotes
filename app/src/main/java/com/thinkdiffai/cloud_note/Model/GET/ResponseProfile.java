package com.thinkdiffai.cloud_note.Model.GET;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseProfile {
    @SerializedName("note")
    private List<Model_Notes> notes;
    @SerializedName("status")
    private int status;
    @SerializedName("user")
    private ProfileModel user;

    public ResponseProfile() {
    }

    public List<Model_Notes> getNotes() {
        return notes;
    }

    public void setNotes(List<Model_Notes> notes) {
        this.notes = notes;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ProfileModel getUser() {
        return user;
    }

    public void setUser(ProfileModel user) {
        this.user = user;
    }
}
