package com.thinkdiffai.cloud_note.Model.GET;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseGroup {
    @SerializedName("data")
    private List<GroupModel> groups;

    public List<GroupModel> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupModel> groups) {
        this.groups = groups;
    }
}
