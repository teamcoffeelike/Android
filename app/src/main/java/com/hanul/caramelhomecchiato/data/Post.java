package com.hanul.caramelhomecchiato.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
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
	private long postDate;
	private int likes;
	private int reactions;
	@Nullable private Boolean likedByYou;

	public Post(int id, User author, @Nullable Uri image, String text, long postDate, int likes, int reactions, @Nullable Boolean likedByYou){
		this.id = id;
		this.author = author;
		this.image = image;
		this.text = text;
		this.postDate = postDate;
		this.likes = likes;
		this.reactions = reactions;
		this.likedByYou = likedByYou;
	}

	protected Post(Parcel parcel){
		this.id = parcel.readInt();
		this.author = parcel.readParcelable(User.class.getClassLoader());
		this.image = parcel.readParcelable(Uri.class.getClassLoader());
		this.text = parcel.readString();
		this.postDate = parcel.readLong();
		this.likes = parcel.readInt();
		this.reactions = parcel.readInt();
		switch(parcel.readByte()){
			case -1: this.likedByYou = null; break;
			case 0: this.likedByYou = false; break;
			default: this.likedByYou = true;
		}
	}

	@Override public int describeContents(){
		return 0;
	}
	@Override public void writeToParcel(Parcel dest, int flags){
		dest.writeInt(id);
		dest.writeParcelable(author, flags);
		dest.writeParcelable(image, flags);
		dest.writeString(text);
		dest.writeLong(postDate);
		dest.writeInt(likes);
		dest.writeInt(reactions);
		dest.writeByte((byte)(likedByYou==null ? -1 : likedByYou ? 1 : 0));
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
	public long getPostDate(){
		return postDate;
	}
	public void setPostDate(long postDate){
		this.postDate = postDate;
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
	@Nullable public Boolean getLikedByYou(){
		return likedByYou;
	}
	public void setLikedByYou(@Nullable Boolean likedByYou){
		this.likedByYou = likedByYou;
	}

	@NonNull @Override public String toString(){
		return "Post{"+
				"id="+id+
				", author="+author+
				", image="+image+
				", text='"+text+'\''+
				", postDate="+postDate+
				", likes="+likes+
				", reactions="+reactions+
				", likedByYou="+likedByYou+
				'}';
	}
}
