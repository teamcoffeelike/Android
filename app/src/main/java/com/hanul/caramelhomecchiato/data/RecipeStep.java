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

	public RecipeStep(int step,
	                  @Nullable Uri image,
	                  String text){
		this.step = step;
		this.image = image;
		this.text = text;
	}
	public RecipeStep(RecipeStep step){
		this.step = step.step;
		this.image = step.image;
		this.text = step.text;
	}
	protected RecipeStep(Parcel in){
		this.step = in.readInt();
		this.image = in.readParcelable(Uri.class.getClassLoader());
		this.text = in.readString();
	}

	@Override public void writeToParcel(Parcel dest, int flags){
		dest.writeInt(step);
		dest.writeParcelable(image, flags);
		dest.writeString(text);
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

	@NonNull @Override public String toString(){
		return "RecipeStep{"+
				"step="+step+
				", image="+image+
				", text='"+text+'\''+
				'}';
	}
}
