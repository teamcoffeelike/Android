package com.hanul.caramelhomecchiato.data;

import android.os.Parcel;
import android.os.Parcelable;

public class JoinLogin implements Parcelable {
    public static final Creator<JoinLogin> CREATOR = new Creator<JoinLogin>(){
        @Override public JoinLogin createFromParcel(Parcel in){
            return new JoinLogin(in);
        }
        @Override public JoinLogin[] newArray(int size){
            return new JoinLogin[size];
        }
    };

    private String email;
    private String password;
    private String name;

    public JoinLogin(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    protected JoinLogin(Parcel in){
        email = in.readString();
        password = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(name);
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
