package com.hanul.caramelhomecchiato.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public final class RecipeCover implements Parcelable{
	public static final Creator<RecipeCover> CREATOR = new Creator<RecipeCover>(){
		@Override
		public RecipeCover createFromParcel(Parcel in){
			return new RecipeCover(in);
		}

		@Override
		public RecipeCover[] newArray(int size){
			return new RecipeCover[size];
		}
	};

	private int id;
	private RecipeCategory category;
	private String title;
	@Nullable private Uri coverImage;
	private User author;
	private long postDate;
	@Nullable private Long lastEditDate;
	private int ratings;
	@Nullable private Double averageRating;
	@Nullable private Double yourRating;

	public RecipeCover(int id,
	                   RecipeCategory category,
	                   String title,
	                   @Nullable Uri coverImage,
	                   User author,
	                   long postDate,
	                   @Nullable Long lastEditDate,
	                   int ratings,
	                   @Nullable Double averageRating,
	                   @Nullable Double yourRating){
		this.id = id;
		this.category = category;
		this.title = title;
		this.coverImage = coverImage;
		this.author = author;
		this.postDate = postDate;
		this.lastEditDate = lastEditDate;
		this.ratings = ratings;
		this.averageRating = averageRating;
		this.yourRating = yourRating;
	}
	protected RecipeCover(Parcel in){
		id = in.readInt();
		category = (RecipeCategory)in.readSerializable();
		title = in.readString();
		coverImage = in.readParcelable(Uri.class.getClassLoader());
		author = in.readParcelable(User.class.getClassLoader());
		postDate = in.readLong();
		if(in.readByte()==0) lastEditDate = null;
		else lastEditDate = in.readLong();
		ratings = in.readInt();
		if(in.readByte()==0) averageRating = null;
		else averageRating = in.readDouble();
		if(in.readByte()==0) yourRating = null;
		else yourRating = in.readDouble();
	}

	@Override public int describeContents(){
		return 0;
	}
	@Override public void writeToParcel(Parcel dest, int flags){
		dest.writeInt(id);
		dest.writeSerializable(category);
		dest.writeString(title);
		dest.writeParcelable(coverImage, flags);
		dest.writeParcelable(author, flags);
		dest.writeLong(postDate);
		if(lastEditDate==null){
			dest.writeByte((byte)0);
		}else{
			dest.writeByte((byte)1);
			dest.writeLong(lastEditDate);
		}
		dest.writeInt(ratings);
		if(averageRating==null){
			dest.writeByte((byte)0);
		}else{
			dest.writeByte((byte)1);
			dest.writeDouble(averageRating);
		}
		if(yourRating==null){
			dest.writeByte((byte)0);
		}else{
			dest.writeByte((byte)1);
			dest.writeDouble(yourRating);
		}
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
	@Nullable public Uri getCoverImage(){
		return coverImage;
	}
	public void setCoverImage(@Nullable Uri coverImage){
		this.coverImage = coverImage;
	}
	public User getAuthor(){
		return author;
	}
	public void setAuthor(User author){
		this.author = author;
	}
	public long getPostDate(){
		return postDate;
	}
	public void setPostDate(long postDate){
		this.postDate = postDate;
	}
	@Nullable public Long getLastEditDate(){
		return lastEditDate;
	}
	public void setLastEditDate(@Nullable Long lastEditDate){
		this.lastEditDate = lastEditDate;
	}
	public int getRatings(){
		return ratings;
	}
	public void setRatings(int ratings){
		this.ratings = ratings;
	}
	@Nullable public Double getAverageRating(){
		return averageRating;
	}
	public void setAverageRating(@Nullable Double averageRating){
		this.averageRating = averageRating;
	}
	@Nullable public Double getYourRating(){
		return yourRating;
	}
	public void setYourRating(@Nullable Double yourRating){
		this.yourRating = yourRating;
	}

	@Override public String toString(){
		return "RecipeCover{"+
				"id="+id+
				", category="+category+
				", title='"+title+'\''+
				", coverImage="+coverImage+
				", author="+author+
				", postDate="+postDate+
				", lastEditDate="+lastEditDate+
				", ratings="+ratings+
				", averageRating="+averageRating+
				", yourRating="+yourRating+
				'}';
	}
}
