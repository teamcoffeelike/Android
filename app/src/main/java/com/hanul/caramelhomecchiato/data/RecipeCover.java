package com.hanul.caramelhomecchiato.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public final class RecipeCover implements Parcelable{
	public static final Creator<RecipeCover> CREATOR = new Creator<RecipeCover>(){
		@Override public RecipeCover createFromParcel(Parcel in){
			return new RecipeCover(in);
		}
		@Override public RecipeCover[] newArray(int size){
			return new RecipeCover[size];
		}
	};

	private int id;
	private RecipeCategory category;
	private String title;
	private User author;
	private float rating;
	@Nullable private Uri photo;

	public RecipeCover(int id,
	                   RecipeCategory category,
	                   String title,
	                   User author,
	                   float rating,
	                   @Nullable Uri photo){
		this.id = id;
		this.category = category;
		this.title = title;
		this.author = author;
		this.rating = rating;
		this.photo = photo;
	}

	protected RecipeCover(Parcel in){
		id = in.readInt();
		category = (RecipeCategory)in.readSerializable();
		title = in.readString();
		author = in.readParcelable(User.class.getClassLoader());
		rating = in.readFloat();
		photo = in.readParcelable(Uri.class.getClassLoader());
	}

	@Override public int describeContents(){
		return 0;
	}
	@Override public void writeToParcel(Parcel dest, int flags){
		dest.writeInt(id);
		dest.writeSerializable(category);
		dest.writeString(title);
		dest.writeParcelable(author, flags);
		dest.writeFloat(rating);
		dest.writeParcelable(photo, flags);
	}

	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}
	public RecipeCategory getCategory(){
		return category;
	}
	public void setCategory(RecipeCategory category){
		this.category = category;
	}
	public String getTitle(){
		return title;
	}
	public void setTitle(String title){
		this.title = title;
	}
	public User getAuthor(){
		return author;
	}
	public void setAuthor(User author){
		this.author = author;
	}
	public float getRating(){ return rating; }
	public void setRating(float rating){ this.rating = rating; }
	@Nullable public Uri getPhoto(){ return photo; }
	public void setPhoto(@Nullable Uri photo){ this.photo = photo; }

	@Override public String toString(){
		return "RecipeCover{"+
				"id="+id+
				", category="+category+
				", title='"+title+'\''+
				", author="+author+
				", rating="+rating+
				", photo="+photo+
				'}';
	}
}
