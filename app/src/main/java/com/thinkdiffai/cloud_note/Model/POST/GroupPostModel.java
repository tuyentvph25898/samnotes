package com.thinkdiffai.cloud_note.Model.POST;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupPostModel {
    @SerializedName("name")
    private String name;
    @SerializedName("createAt")
    private String createAt;
    @SerializedName("idOwner")
    private int idOwner;
    @SerializedName("describe")
    private String describe;
    @SerializedName("members")
    private List<GroupMember> members;

    public GroupPostModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public int getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(int idOwner) {
        this.idOwner = idOwner;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public List<GroupMember> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMember> members) {
        this.members = members;
    }
}
