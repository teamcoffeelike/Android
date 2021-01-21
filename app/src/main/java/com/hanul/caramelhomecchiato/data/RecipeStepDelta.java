package com.hanul.caramelhomecchiato.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public final class RecipeStepDelta implements Parcelable{
	public static final Creator<RecipeStepDelta> CREATOR = new Creator<RecipeStepDelta>(){
		@Override public RecipeStepDelta createFromParcel(Parcel in){
			return new RecipeStepDelta(in);
		}

		@Override public RecipeStepDelta[] newArray(int size){
			return new RecipeStepDelta[size];
		}
	};

	@Nullable private final Integer originalStep;
	private final AttachmentDelta image;
	@Nullable private String text;

	public RecipeStepDelta(){
		this.originalStep = null;
		this.image = new AttachmentDelta();
	}
	public RecipeStepDelta(RecipeStep step){
		this.originalStep = step.getStep();
		this.image = new AttachmentDelta();
		this.image.setOverriding(step.getImage()!=null);
	}
	protected RecipeStepDelta(Parcel in){
		this.originalStep = in.readByte()==0 ? null : in.readInt();
		this.image = in.readParcelable(AttachmentDelta.class.getClassLoader());
		this.text = in.readString();
	}

	@Override public int describeContents(){
		return 0;
	}
	@Override public void writeToParcel(Parcel dest, int flags){
		if(originalStep==null) dest.writeByte((byte)0);
		else{
			dest.writeByte((byte)1);
			dest.writeInt(originalStep);
		}
		dest.writeParcelable(image, flags);
		dest.writeString(text);
	}

	@Nullable public Integer getOriginalStep(){
		return originalStep;
	}
	@Nullable public Uri getImage(){
		return image.getUri();
	}
	public void setImage(Uri image){
		this.image.setUri(image);
	}
	public boolean isImageEdited(){
		return image.isDirty();
	}
	public void revertImageEdited(){
		this.image.revertEdited();
	}
	@Nullable public String getText(){
		return text;
	}
	public void setText(@Nullable String text){
		this.text = text;
	}

	@Override public String toString(){
		return "RecipeStepDelta{"+
				"originalStep="+originalStep+
				", image="+image+
				", text='"+text+'\''+
				'}';
	}
}
