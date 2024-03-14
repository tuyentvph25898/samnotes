package com.thinkdiffai.cloud_note.Model.GET;

import com.google.gson.annotations.SerializedName;

public class FolderModel {
    @SerializedName("idFolder")
    private int id;
    @SerializedName("createAt")
    private String createAt;
    @SerializedName("idUser")
    private int idUser;
    @SerializedName("nameFolder")
    private String nameFolder;

    public FolderModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNameFolder() {
        return nameFolder;
    }

    public void setNameFolder(String nameFolder) {
        this.nameFolder = nameFolder;
    }
}
