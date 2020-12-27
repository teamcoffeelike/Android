package com.hanul.caramelhomecchiato.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * 타이머 버튼 등의 레시피 스텝 내부에 사용되는 상호작용 가능한 과제
 */
public abstract class RecipeTask implements Parcelable{
	public static class Timer extends RecipeTask{
		public static final Creator<Timer> CREATOR = new Creator<Timer>(){
			@Override public Timer createFromParcel(Parcel in){
				return new Timer(in);
			}
			@Override public Timer[] newArray(int size){
				return new Timer[size];
			}
		};

		private final int seconds;

		public Timer(int seconds){
			this.seconds = seconds;
		}
		protected Timer(Parcel in){
			seconds = in.readInt();
		}

		@Override public void writeToParcel(Parcel dest, int flags){
			dest.writeInt(seconds);
		}
		@Override public int describeContents(){
			return 0;
		}

		public int getSeconds(){
			return seconds;
		}
	}

	public enum Json implements JsonDeserializer<RecipeTask>{
		INSTANCE;

		@Override public RecipeTask deserialize(JsonElement json,
		                                        Type typeOfT,
		                                        JsonDeserializationContext context
		) throws JsonParseException{
			JsonObject o = json.getAsJsonObject();

			return null;
		}
	}
}
