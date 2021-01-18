package com.hanul.caramelhomecchiato.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Recipe implements Parcelable{
	public static final Creator<Recipe> CREATOR = new Creator<Recipe>(){
		@Override
		public Recipe createFromParcel(Parcel in){
			return new Recipe(in);
		}

		@Override
		public Recipe[] newArray(int size){
			return new Recipe[size];
		}
	};

	private RecipeCover cover;
	private final List<RecipeStep> steps;

	public Recipe(RecipeCover cover, List<RecipeStep> steps){
		this.cover = cover;
		this.steps = new ArrayList<>(steps);
	}
	public Recipe(RecipeCover cover, RecipeStep... steps){
		this.cover = cover;
		this.steps = new ArrayList<>();
		this.steps.addAll(Arrays.asList(steps));
	}
	protected Recipe(Parcel in){
		cover = in.readParcelable(RecipeCover.class.getClassLoader());
		steps = in.createTypedArrayList(RecipeStep.CREATOR);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags){
		dest.writeParcelable(cover, flags);
		dest.writeTypedList(steps);
	}

	@Override
	public int describeContents(){
		return 0;
	}
	public RecipeCover getCover(){
		return cover;
	}
	public void setCover(RecipeCover cover){
		this.cover = cover;
	}
	public List<RecipeStep> steps(){
		return steps;
	}
}
