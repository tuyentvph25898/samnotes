package com.thinkdiffai.cloud_note.Model.POST;

import com.google.gson.annotations.SerializedName;

public class FolderPostModel {
    @SerializedName("nameFolder")
    private String folderName;

    public FolderPostModel() {
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
