package com.thinkdiffai.cloud_note.Model.GET;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseFolder {
    @SerializedName("folder")
    private List<FolderModel> folder;

    public List<FolderModel> getFolder() {
        return folder;
    }

    public void setFolder(List<FolderModel> folder) {
        this.folder = folder;
    }
}
