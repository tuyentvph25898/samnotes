package com.thinkdiffai.cloud_note.Model.POST;

import com.google.gson.annotations.SerializedName;
import com.thinkdiffai.cloud_note.Model.GET.MessageModel;

public class ResponsePostMessageUK {
    @SerializedName("message")
    private MessageModel model;
    @SerializedName("status")
    private int status;

    public MessageModel getModel() {
        return model;
    }

    public void setModel(MessageModel model) {
        this.model = model;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
