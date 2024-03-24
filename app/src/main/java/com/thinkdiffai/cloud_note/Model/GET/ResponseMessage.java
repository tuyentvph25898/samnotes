package com.thinkdiffai.cloud_note.Model.GET;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseMessage {
    @SerializedName("data")
    private List<MessageModel> messageModels;
    @SerializedName("status")
    private int status;

    public List<MessageModel> getMessageModels() {
        return messageModels;
    }

    public void setMessageModels(List<MessageModel> messageModels) {
        this.messageModels = messageModels;
    }
}
