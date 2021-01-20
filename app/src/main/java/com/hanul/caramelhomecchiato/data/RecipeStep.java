package com.hanul.caramelhomecchiato.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RecipeStep implements Parcelable{
	public static final Creator<RecipeStep> CREATOR = new Creator<RecipeStep>(){
		@Override public RecipeStep createFromParcel(Parcel in){
			return new RecipeStep(in);
		}
		@Override public RecipeStep[] newArray(int size){
			return new RecipeStep[size];
		}
	};

	private int step;
	@Nullable private Uri image;
	private String text;
	@Nullable private RecipeTask task;

	public RecipeStep(int step,
	                  @Nullable Uri image,
	                  String text,
	                  @Nullable RecipeTask task){
		this.step = step;
		this.image = image;
		this.text = text;
		this.task = task;
	}
	protected RecipeStep(Parcel in){
		step = in.readInt();
		image = in.readParcelable(Uri.class.getClassLoader());
		text = in.readString();
		task = in.readParcelable(RecipeTask.class.getClassLoader());
	}

	@Override public void writeToParcel(Parcel dest, int flags){
		dest.writeInt(step);
		dest.writeParcelable(image, flags);
		dest.writeString(text);
		dest.writeParcelable(task, flags);
	}
	@Override public int describeContents(){
		return 0;
	}

	public int getStep(){
		return step;
	}
	public void setStep(int step){
		this.step = step;
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
	@Nullable public RecipeTask getTask(){
		return task;
	}
	public void setTask(@Nullable RecipeTask task){
		this.task = task;
	}

	@NonNull @Override public String toString(){
		return "RecipeStep{"+
				"step="+step+
				", image="+image+
				", text='"+text+'\''+
				", task="+task+
				'}';
	}
}
