package com.hanul.caramelhomecchiato.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

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
	private User author;
	@Nullable private Uri image;
	private String text;
	private int likes;
	private int reactions;

	public Post(int id, User author, @Nullable Uri image, String text, int likes, int reactions){
		this.id = id;
		this.author = author;
		this.image = image;
		this.text = text;
		this.likes = likes;
		this.reactions = reactions;
	}

	protected Post(Parcel parcel){
		id = parcel.readInt();
		author = parcel.readParcelable(User.class.getClassLoader());
		image = parcel.readParcelable(Uri.class.getClassLoader());
		text = parcel.readString();
		likes = parcel.readInt();
		reactions = parcel.readInt();
	}

	@Override public int describeContents(){
		return 0;
	}
	@Override public void writeToParcel(Parcel dest, int flags){
		dest.writeInt(id);
		dest.writeParcelable(author, flags);
		dest.writeParcelable(image, flags);
		dest.writeString(text);
		dest.writeInt(likes);
		dest.writeInt(reactions);
	}

	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}
	public User getAuthor(){
		return author;
	}
	public void setAuthor(User author){
		this.author = author;
	}
	@Nullable public Uri getImage(){
		return image;
	}
	public void setImage(@Nullable Uri image){
		this.image = image;
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
	public int getReactions(){
		return reactions;
	}
	public void setReactions(int reactions){
		this.reactions = reactions;
	}
}
