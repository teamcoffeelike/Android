package com.hanul.caramelhomecchiato.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public final class AttachmentDelta implements Parcelable{
	public static final Creator<AttachmentDelta> CREATOR = new Creator<AttachmentDelta>(){
		@Override public AttachmentDelta createFromParcel(Parcel in){
			return new AttachmentDelta(in);
		}
		@Override
		public AttachmentDelta[] newArray(int size){
			return new AttachmentDelta[size];
		}
	};

	@Nullable private Uri uri;
	private boolean overriding;
	private boolean dirty;

	public AttachmentDelta(){}
	protected AttachmentDelta(Parcel in){
		uri = in.readParcelable(Uri.class.getClassLoader());
		overriding = in.readByte()!=0;
		dirty = in.readByte()!=0;
	}

	@Override
	public int describeContents(){
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags){
		dest.writeParcelable(uri, flags);
		dest.writeByte((byte)(overriding ? 1 : 0));
		dest.writeByte((byte)(dirty ? 1 : 0));
	}
	@Nullable public Uri getUri(){
		return uri;
	}
	public void setUri(@Nullable Uri uri){
		this.uri = uri;
		dirty = overriding||uri!=null;
	}
	public boolean isOverriding(){
		return overriding;
	}
	public void setOverriding(boolean overriding){
		this.overriding = overriding;
	}
	public boolean isDirty(){
		return dirty;
	}
	public void setDirty(boolean dirty){
		this.dirty = dirty;
	}

	public void revertEdited(){
		dirty = false;
	}

	/**
	 * 해당 리소스가 한 번도 입력되지 않았거나, 기존 리소스를 삭제하는 작업일 때 {@code true}를 반환합니다.
	 */
	public boolean isEmpty(){
		return overriding ?
				dirty&&uri==null :
				uri==null;
	}

	@Override public String toString(){
		return "AttachmentDelta{"+
				"uri="+uri+
				", overriding="+overriding+
				", dirty="+dirty+
				'}';
	}
}
