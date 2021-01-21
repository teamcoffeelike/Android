package com.hanul.caramelhomecchiato.data;

import androidx.annotation.Nullable;

public final class RecipeWriteError{
	@Nullable private final Integer stepIndex;
	@Nullable private final String message;

	public RecipeWriteError(@Nullable String message){
		this.stepIndex = null;
		this.message = message;
	}

	public RecipeWriteError(int stepIndex, @Nullable String message){
		this.stepIndex = stepIndex;
		this.message = message;
	}

	public boolean isCoverError(){
		return stepIndex==null;
	}
	@Nullable public Integer getStepIndex(){
		return stepIndex;
	}
	@Nullable public String getMessage(){
		return message;
	}
}
