package com.thinkdiffai.cloud_note.Model.GET;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseComment {
    @SerializedName("data")
    private List<CommentModel> comments;

    public List<CommentModel> getComments() {
        return comments;
    }

    public void setComments(List<CommentModel> comments) {
        this.comments = comments;
    }
}
