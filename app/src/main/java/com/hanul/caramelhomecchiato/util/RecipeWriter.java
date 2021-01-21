package com.hanul.caramelhomecchiato.util;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.data.RecipeCategory;

public interface RecipeWriter{
	boolean isEditMode();

	@Nullable RecipeCategory getCategory();
	String getTitle();
	@Nullable Uri getCoverImage();

	@Nullable Uri getStepImage(int index);
	String getStepText(int index);

	ImageState getCoverImageState();
	ImageState getStepImageState(int index);

	void insertStepAt(int index);
	void deleteStepAt(int index);
	void setCategory(RecipeCategory category);
	void setTitle(String title);
	void setStepText(int index, String text);

	void chooseCoverImage(Runnable onSucceed);
	void revertCoverImage();
	void chooseStepImage(int index, Runnable onSucceed);
	void removeStepImage(int index);
	void revertStepImage(int index);

	int getNumberOfSteps();


	enum ImageState{
		UNEDITED,
		REPLACED,
		ADDED,
		REMOVED
	}
}
