package com.hanul.caramelhomecchiato.data;

import android.os.Parcel;
import android.os.Parcelable;

public final class Reaction implements Parcelable{
	public static final Creator<Reaction> CREATOR = new Creator<Reaction>(){
		@Override public Reaction createFromParcel(Parcel in){
			return new Reaction(in);
		}
		@Override public Reaction[] newArray(int size){
			return new Reaction[size];
		}
	};

	private User user;
	private String text;

	public Reaction(User user, String text){
		this.user = user;
		this.text = text;
	}

	protected Reaction(Parcel in){
		user = in.readParcelable(User.class.getClassLoader());
		text = in.readString();
	}

	@Override public int describeContents(){
		return 0;
	}
	@Override public void writeToParcel(Parcel dest, int flags){
		dest.writeParcelable(user, flags);
		dest.writeString(text);
	}

	public User getUser(){
		return user;
	}
	public void setUser(User user){
		this.user = user;
	}
	public String getText(){
		return text;
	}
	public void setText(String text){
		this.text = text;
	}
}
