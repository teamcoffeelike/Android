package com.hanul.caramelhomecchiato.util;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.data.RecipeCategory;
import com.hanul.caramelhomecchiato.data.RecipeDelta;
import com.hanul.caramelhomecchiato.data.RecipeStep;
import com.hanul.caramelhomecchiato.data.RecipeStepDelta;

import java.util.Objects;

public class SimpleRecipeWriter implements RecipeWriter{
	@Nullable private Recipe editingRecipe;
	private RecipeDelta delta = new RecipeDelta();

	@Nullable public Recipe getEditingRecipe(){
		return editingRecipe;
	}
	public void setEditingRecipe(@Nullable Recipe editingRecipe){
		this.editingRecipe = editingRecipe;
	}
	public RecipeDelta getDelta(){
		return delta;
	}
	public void setDelta(RecipeDelta delta){
		this.delta = Objects.requireNonNull(delta);
	}

	@Override public boolean isEditMode(){
		return editingRecipe!=null;
	}
	@Nullable @Override public Integer getEditingRecipeId(){
		return editingRecipe==null ? null : editingRecipe.getCover().getId();
	}

	@Nullable @Override public RecipeCategory getCategory(){
		if(delta.getCategory()!=null) return delta.getCategory();
		if(editingRecipe!=null) return editingRecipe.getCover().getCategory();
		return null;
	}
	@Override public String getTitle(){
		if(delta.getTitle()!=null) return delta.getTitle();
		if(editingRecipe!=null) return editingRecipe.getCover().getTitle();
		return "";
	}
	@Nullable @Override public Uri getCoverImage(){
		if(delta.isCoverImageEdited()) return delta.getCoverImage();
		if(editingRecipe!=null) return editingRecipe.getCover().getCoverImage();
		return null;
	}
	@Nullable @Override public Uri getStepImage(int index){
		RecipeStepDelta step = delta.step(index);
		if(step.isImageEdited()) return step.getImage();
		if(step.getOriginalStep()!=null) return Objects.requireNonNull(editingRecipe).steps().get(step.getOriginalStep()).getImage();
		return null;
	}
	@Override public String getStepText(int index){
		RecipeStepDelta step = delta.step(index);
		if(step.getText()!=null) return step.getText();
		if(step.getOriginalStep()!=null) return Objects.requireNonNull(editingRecipe).steps().get(step.getOriginalStep()).getText();
		return "";
	}
	@Override public ImageState getCoverImageState(){
		if(editingRecipe==null){
			return delta.isCoverImageEdited() ?
					ImageState.ADDED :
					ImageState.UNEDITED;
		}else{
			return delta.isCoverImageEdited() ?
					(delta.getCoverImage()==null ?
							ImageState.REMOVED :
							ImageState.REPLACED) :
					ImageState.UNEDITED;
		}
	}
	@Override public ImageState getStepImageState(int index){
		RecipeStepDelta step = delta.step(index);
		if(editingRecipe==null||step.getOriginalStep()==null){
			return step.isImageEdited() ?
					ImageState.ADDED :
					ImageState.UNEDITED;
		}else{
			RecipeStep s2 = editingRecipe.steps().get(step.getOriginalStep());
			if(s2.getImage()==null)
				return step.isImageEdited() ? ImageState.ADDED : ImageState.UNEDITED;
			else
				return step.isImageEdited() ?
						(step.getImage()==null ?
								ImageState.REMOVED :
								ImageState.REPLACED) :
						ImageState.UNEDITED;
		}
	}

	@Override public void insertStepAt(int index){
		if(delta.steps().size() >= Validate.MAX_RECIPE_STEPS){
			error("더 이상 단계를 추가할 수 없습니다.");
			return;
		}
		insertStep(index);
	}
	protected void insertStep(int index){
		delta.steps().add(index, new RecipeStepDelta());
	}
	@Override public void deleteStepAt(int index){
		if(delta.steps().size()<=1){
			error("레시피에는 최소 한 개의 단계가 필요합니다.");
			return;
		}
		deleteStep(index);
	}
	protected void deleteStep(int index){
		delta.removeStep(index);
	}

	@Override public void setCategory(RecipeCategory category){
		if(editingRecipe!=null&&editingRecipe.getCover().getCategory()==category) delta.setCategory(null);
		else delta.setCategory(category);
	}
	@Override public void setTitle(String title){
		if(editingRecipe!=null&&editingRecipe.getCover().getTitle().equals(title)) delta.setTitle(null);
		else delta.setTitle(title);
	}
	@Override public void setStepText(int index, String text){
		RecipeStepDelta step = delta.step(index);
		if(editingRecipe!=null
				&&step.getOriginalStep()!=null
				&&editingRecipe.steps().get(step.getOriginalStep()).getText().equals(text))
			step.setText(null);
		else step.setText(text);
	}
	@Override public void chooseCoverImage(Runnable onSucceed){}
	public void setCoverImage(Uri image){
		delta.setCoverImage(image);
	}
	@Override public void revertCoverImage(){
		delta.revertCoverImageEdited();
	}
	@Override public void chooseStepImage(int index, Runnable onSucceed){}
	public void setStepImage(int index, Uri image){
		delta.steps().get(index).setImage(image);
	}
	@Override public void removeStepImage(int index){
		delta.step(index).setImage(null);
	}
	@Override public void revertStepImage(int index){
		delta.step(index).revertImageEdited();
	}
	@Override public int getNumberOfSteps(){
		return delta.steps().size();
	}

	protected void error(String message){}
}
