package com.hanul.caramelhomecchiato.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public final class Recipe implements Parcelable{
	public static final Creator<Recipe> CREATOR = new Creator<Recipe>(){
		@Override public Recipe createFromParcel(Parcel in){
			return new Recipe(in);
		}
		@Override public Recipe[] newArray(int size){
			return new Recipe[size];
		}
	};

	private int id;
	/*private RecipeCategory category;*/
	private String title;
	private User author;
	private float rating;
	@Nullable private Uri photo;

	public Recipe(
			int id,
			/*RecipeCategory category,*/
			String title,
			User author,
			float rating,
			@Nullable Uri photo
	){
		this.id = id;
		/*this.category = category;*/
		this.title = title;
		this.author = author;
		this.rating = rating;
		this.photo = photo;
	}

	protected Recipe(Parcel in){
		id = in.readInt();
		/*category = RecipeCategory.values()[in.readByte()];*/
		title = in.readString();
		author = in.readParcelable(User.class.getClassLoader());
		rating = in.readFloat();
		photo = in.readParcelable(User.class.getClassLoader());
	}

	@Override public int describeContents(){
		return 0;
	}
	@Override public void writeToParcel(Parcel dest, int flags){
		dest.writeInt(id);
		/*dest.writeByte((byte) category.ordinal());*/
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
	/*public RecipeCategory getCategory(){
		return category;
	}
	public void setCategory(RecipeCategory category){
		this.category = category;
	}*/
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
	public float getRating() { return rating; }
	public void setRating(float rating) { this.rating = rating; }
	@Nullable public Uri getPhoto() { return photo; }
	public void setPhoto(@Nullable Uri photo) { this.photo = photo; }
}
