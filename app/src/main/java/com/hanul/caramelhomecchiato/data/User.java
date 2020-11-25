package com.hanul.caramelhomecchiato.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public final class User implements Parcelable{
	public static final Creator<User> CREATOR = new Creator<User>(){
		@Override public User createFromParcel(Parcel in){
			return new User(in);
		}
		@Override public User[] newArray(int size){
			return new User[size];
		}
	};

	private int id;
	private String name;
	@Nullable private String profileImage;

	public User(int id, String name, @Nullable String profileImage){
		this.id = id;
		this.name = name;
		this.profileImage = profileImage;
	}

	protected User(Parcel in){
		id = in.readInt();
		name = in.readString();
		profileImage = in.readString();
	}

	@Override public int describeContents(){
		return 0;
	}
	@Override public void writeToParcel(Parcel dest, int flags){
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(profileImage);
	}

	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	@Nullable public String getProfileImage(){
		return profileImage;
	}
	public void setProfileImage(@Nullable String profileImage){
		this.profileImage = profileImage;
	}
}
