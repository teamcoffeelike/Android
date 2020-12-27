package com.hanul.caramelhomecchiato.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.net.URI;

public class RecipeStep implements Parcelable{
	public static final Creator<RecipeStep> CREATOR = new Creator<RecipeStep>(){
		@Override public RecipeStep createFromParcel(Parcel in){
			return new RecipeStep(in);
		}
		@Override public RecipeStep[] newArray(int size){
			return new RecipeStep[size];
		}
	};

	private int index;
	@Nullable private URI image;
	private String text;
	@Nullable private RecipeTask task;

	public RecipeStep(int index,
	                  @Nullable URI image,
	                  String text,
	                  @Nullable RecipeTask task){
		this.index = index;
		this.image = image;
		this.text = text;
		this.task = task;
	}
	protected RecipeStep(Parcel in){
		index = in.readInt();
		text = in.readString();
		task = in.readParcelable(RecipeTask.class.getClassLoader());
	}

	@Override public void writeToParcel(Parcel dest, int flags){
		dest.writeInt(index);
		dest.writeString(text);
		dest.writeParcelable(task, flags);
	}
	@Override public int describeContents(){
		return 0;
	}

	public int getIndex(){
		return index;
	}
	public void setIndex(int index){
		this.index = index;
	}
	@Nullable public URI getImage(){
		return image;
	}
	public void setImage(@Nullable URI image){
		this.image = image;
	}
	public String getText(){
		return text;
	}
	public void setText(String text){
		this.text = text;
	}
	@Nullable public RecipeTask getTask(){
		return task;
	}
	public void setTask(@Nullable RecipeTask task){
		this.task = task;
	}
}
