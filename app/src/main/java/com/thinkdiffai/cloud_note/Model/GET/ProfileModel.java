package com.thinkdiffai.cloud_note.Model.GET;

import com.google.gson.annotations.SerializedName;
import com.thinkdiffai.cloud_note.Model.Color;

public class ProfileModel {
    @SerializedName("Avarta")
    private String avatar;
    @SerializedName("AvtProfile")
    private String avtProfile;
    @SerializedName("createAccount")
    private String createAccount;
    @SerializedName("df_color")
    private Color dfColor;
    @SerializedName("gmail")
    private String gmail;
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("password_2")
    private String password2;
    @SerializedName("status_Login")
    private Boolean statusLogin;
    @SerializedName("user_Name")
    private String username;

    public ProfileModel() {
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvtProfile() {
        return avtProfile;
    }

    public void setAvtProfile(String avtProfile) {
        this.avtProfile = avtProfile;
    }

    public String getCreateAccount() {
        return createAccount;
    }

    public void setCreateAccount(String createAccount) {
        this.createAccount = createAccount;
    }

    public Color getDfColor() {
        return dfColor;
    }

    public void setDfColor(Color dfColor) {
        this.dfColor = dfColor;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public Boolean getStatusLogin() {
        return statusLogin;
    }

    public void setStatusLogin(Boolean statusLogin) {
        this.statusLogin = statusLogin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
