package com.hanul.caramelhomecchiato.data;

import android.net.Uri;
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
	@Nullable private Uri profileImage;

	@Nullable private Boolean isFollowingYou;
	@Nullable private Boolean isFollowedByYou;

	public User(int id, String name, @Nullable Uri profileImage, @Nullable Boolean isFollowingYou, @Nullable Boolean isFollowedByYou){
		this.id = id;
		this.name = name;
		this.profileImage = profileImage;
		this.isFollowingYou = isFollowingYou;
		this.isFollowedByYou = isFollowedByYou;
	}

	protected User(Parcel in){
		id = in.readInt();
		name = in.readString();
		profileImage = in.readParcelable(Uri.class.getClassLoader());
	}

	@Override public int describeContents(){
		return 0;
	}
	@Override public void writeToParcel(Parcel dest, int flags){
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeParcelable(profileImage, flags);
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
	@Nullable public Uri getProfileImage(){
		return profileImage;
	}
	public void setProfileImage(@Nullable Uri profileImage){
		this.profileImage = profileImage;
	}
	@Nullable public Boolean getFollowingYou(){
		return isFollowingYou;
	}
	public void setFollowingYou(@Nullable Boolean followingYou){
		isFollowingYou = followingYou;
	}
	@Nullable public Boolean getFollowedByYou(){
		return isFollowedByYou;
	}
	public void setFollowedByYou(@Nullable Boolean followedByYou){
		isFollowedByYou = followedByYou;
	}

	@Override public String toString(){
		return "User{"+
				"id="+id+
				", name='"+name+'\''+
				", profileImage="+profileImage+
				", isFollowingYou="+isFollowingYou+
				", isFollowedByYou="+isFollowedByYou+
				'}';
	}
}
