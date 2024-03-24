package com.thinkdiffai.cloud_note.Model.GET;

import com.google.gson.annotations.SerializedName;
import com.thinkdiffai.cloud_note.Model.POST.GroupMember;

import java.util.List;

public class ResponseUser {
    @SerializedName("data")
    private List<GroupMember> members;
    @SerializedName("status")
    private int status;

    public ResponseUser() {
    }

    public List<GroupMember> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMember> members) {
        this.members = members;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
