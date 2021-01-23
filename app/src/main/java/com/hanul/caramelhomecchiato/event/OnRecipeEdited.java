package com.hanul.caramelhomecchiato.event;

import androidx.annotation.Nullable;

import com.hanul.caramelhomecchiato.data.RecipeCategory;

@FunctionalInterface
public interface OnRecipeEdited{
	void onRecipeEdited(int recipeId, @Nullable RecipeCategory newCategory);
}
