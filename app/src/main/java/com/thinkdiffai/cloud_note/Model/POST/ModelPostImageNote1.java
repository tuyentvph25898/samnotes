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
    private String image_note;
    @SerializedName("r")
    private int r;
    @SerializedName("g")
    private int g;
    @SerializedName("b")
    private int b;
    @SerializedName("a")
    private int a;
    @SerializedName("remind")
    private String remind;

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

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

    public String getImage_note() {
        return image_note;
    }

    public void setImage_note(String image_note) {
        this.image_note = image_note;
    }


    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }
}
