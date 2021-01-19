package com.hanul.caramelhomecchiato.util;

import androidx.annotation.Nullable;

import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.data.RecipeCategory;
import com.hanul.caramelhomecchiato.data.RecipeTask;

public interface RecipeWriter{
	Recipe getRecipe();

	void insertStepAt(int index);
	void deleteStepAt(int index);
	void setCategory(RecipeCategory category);
	void setTitle(String title);
	void setStepText(int index, String text);
	void setStepTask(int index, @Nullable RecipeTask task);

	void chooseTitleImage(Runnable onSucceed);
	void chooseStepImage(int index, Runnable onSucceed);
}
