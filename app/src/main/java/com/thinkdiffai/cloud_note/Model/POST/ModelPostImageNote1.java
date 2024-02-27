package com.thinkdiffai.cloud_note.Model.POST;

import com.google.gson.annotations.SerializedName;
import com.thinkdiffai.cloud_note.Model.Color;

import okhttp3.MultipartBody;

public class ModelPostImageNote1 {
    @SerializedName("type")
    private String type;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("image_note")
    private MultipartBody image_note;
    @SerializedName("color")
    private Color color;
    @SerializedName("remind")
    private String remind;

    public ModelPostImageNote1() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MultipartBody getImage_note() {
        return image_note;
    }

    public void setImage_note(MultipartBody image_note) {
        this.image_note = image_note;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }
}
