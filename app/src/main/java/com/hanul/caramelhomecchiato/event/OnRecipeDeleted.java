package com.hanul.caramelhomecchiato.event;

@FunctionalInterface
public interface OnRecipeDeleted{
	void onRecipeDeleted(int recipeId);
}