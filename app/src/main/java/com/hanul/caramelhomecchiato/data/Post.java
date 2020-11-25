package com.hanul.caramelhomecchiato.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Post implements Parcelable{
	public static final Creator<Post> CREATOR = new Creator<Post>(){
		@Override public Post createFromParcel(Parcel in){
			return new Post(in);
		}
		@Override public Post[] newArray(int size){
			return new Post[size];
		}
	};

	private int id;
	private User user;
	private List<String> images;
	private String text;
	private int likes;
	@Nullable private Reaction firstReaction;

	public Post(int id, User user, List<String> images, String text, int likes, @Nullable Reaction firstReaction){
		this.id = id;
		this.user = user;
		this.images = images;
		this.text = text;
		this.likes = likes;
		this.firstReaction = firstReaction;
	}

	protected Post(Parcel parcel){
		id = parcel.readInt();
		user = parcel.readParcelable(User.class.getClassLoader());
		parcel.readStringList(images = new ArrayList<>());
		text = parcel.readString();
		likes = parcel.readInt();
		firstReaction = parcel.readParcelable(Reaction.class.getClassLoader());
	}

	@Override public int describeContents(){
		return 0;
	}
	@Override public void writeToParcel(Parcel dest, int flags){
		dest.writeInt(id);
		dest.writeParcelable(user, flags);
		dest.writeStringList(images);
		dest.writeString(text);
		dest.writeInt(likes);
		dest.writeParcelable(firstReaction, flags);
	}

	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}
	public User getUser(){
		return user;
	}
	public void setUser(User user){
		this.user = user;
	}
	public List<String> getImages(){
		return images;
	}
	public void setImages(List<String> images){
		this.images = images;
	}
	public String getText(){
		return text;
	}
	public void setText(String text){
		this.text = text;
	}
	public int getLikes(){
		return likes;
	}
	public void setLikes(int likes){
		this.likes = likes;
	}
	@Nullable public Reaction getFirstReaction(){
		return firstReaction;
	}
	public void setFirstReaction(@Nullable Reaction firstReaction){
		this.firstReaction = firstReaction;
	}
}
