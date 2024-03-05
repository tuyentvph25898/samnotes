package com.thinkdiffai.cloud_note.Model.GET;

import com.google.gson.annotations.SerializedName;

public class GroupModel {
    @SerializedName("idOwner")
    private int idUser;
    @SerializedName("name")
    private String groupName;
    @SerializedName("describe")
    private String describe;
    @SerializedName("numberMems")
    private int memQuantity;

    public GroupModel() {
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getMemQuantity() {
        return memQuantity;
    }

    public void setMemQuantity(int memQuantity) {
        this.memQuantity = memQuantity;
    }
}
