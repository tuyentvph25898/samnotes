package com.thinkdiffai.cloud_note.Model.PATCH;

import com.google.gson.annotations.SerializedName;

public class ChangPublicNote {
    @SerializedName("notePublic")
    private int publicNote;

    public ChangPublicNote(int publicNote) {
        this.publicNote = publicNote;
    }

    public int getPublicNote() {
        return publicNote;
    }

    public void setPublicNote(int publicNote) {
        this.publicNote = publicNote;
    }
}
