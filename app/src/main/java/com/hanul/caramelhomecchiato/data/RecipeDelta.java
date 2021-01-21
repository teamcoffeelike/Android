package com.hanul.caramelhomecchiato.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hanul.caramelhomecchiato.util.RecipeEditorEncoder;
import com.hanul.caramelhomecchiato.util.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kotlin.text.StringsKt;

/**
 * WriteRecipeActivity에서 사용. 레시피 작성/수정 시 데이터 핸들링.
 */
public final class RecipeDelta implements Parcelable{
	public static final Creator<RecipeDelta> CREATOR = new Creator<RecipeDelta>(){
		@Override public RecipeDelta createFromParcel(Parcel in){
			return new RecipeDelta(in);
		}
		@Override public RecipeDelta[] newArray(int size){
			return new RecipeDelta[size];
		}
	};

	@Nullable private final Integer id;
	@Nullable private RecipeCategory category;
	@Nullable private String title;
	private final AttachmentDelta coverImage;

	private final List<RecipeStepDelta> steps;
	@Nullable private final List<Integer> removedSteps;

	public RecipeDelta(){
		this.id = null;
		this.coverImage = new AttachmentDelta();
		this.steps = new ArrayList<>();
		this.steps.add(new RecipeStepDelta());
		this.removedSteps = null;
	}
	public RecipeDelta(@NonNull Recipe editingRecipe){
		this.id = editingRecipe.getCover().getId();
		this.coverImage = new AttachmentDelta();
		this.coverImage.setOverriding(editingRecipe.getCover().getCoverImage()!=null);

		this.steps = new ArrayList<>();
		for(RecipeStep step : editingRecipe.steps()){
			steps.add(new RecipeStepDelta(step));
		}
		this.removedSteps = new ArrayList<>();
	}
	protected RecipeDelta(Parcel in){
		this.id = in.readByte()==0 ? null : in.readInt();
		byte categoryByte = in.readByte();
		this.category = categoryByte==-1 ? null : RecipeCategory.values()[categoryByte];
		this.title = in.readString();
		this.coverImage = in.readParcelable(AttachmentDelta.class.getClassLoader());
		this.steps = in.createTypedArrayList(RecipeStepDelta.CREATOR);
		if(in.readByte()!=0){
			removedSteps = new ArrayList<>();
			for(int i : in.createIntArray()) removedSteps.add(i);
		}else removedSteps = null;
	}

	@Override public int describeContents(){
		return 0;
	}
	@Override public void writeToParcel(Parcel dest, int flags){
		if(id==null) dest.writeByte((byte)0);
		else{
			dest.writeByte((byte)1);
			dest.writeInt(id);
		}
		dest.writeByte(category==null ? (byte)-1 : (byte)category.ordinal());
		dest.writeString(title);
		dest.writeParcelable(coverImage, flags);
		dest.writeParcelableList(steps, flags);
		if(removedSteps==null){
			dest.writeByte((byte)0);
		}else{
			dest.writeByte((byte)1);
			int[] ints = new int[removedSteps.size()];
			for(int i = 0; i<ints.length; i++){
				ints[i] = removedSteps.get(i);
			}
			dest.writeIntArray(ints);
		}
	}

	public boolean hasChange(){
		if(id!=null&&category!=null) return true;
		if(isCoverImageEdited()) return true;
		if(title!=null) return true;
		if(removedSteps!=null&&!removedSteps.isEmpty()) return true;
		for(int i = 0; i<steps.size(); i++){
			RecipeStepDelta step = steps.get(i);
			if(step.isImageEdited()) return true;
			if(step.getText()!=null) return true;
			if(step.getOriginalStep()!=null&&step.getOriginalStep()!=i) return true;
		}
		return false;
	}

	/**
	 * 작성한 레시피 변경사항 전체를 체크하며, 발견한 첫 번째 에러를 반환힙니다.
	 *
	 * @return 에러
	 */
	@Nullable public RecipeWriteError validate(){
		if(id==null){
			//초기 작성 시
			if(category==null) return new RecipeWriteError("레시피 카테고리를 선택해 주세요.");
			if(title==null) return new RecipeWriteError("레시피 타이틀을 입력해 주세요.");
		}
		if(title!=null){
			if(StringsKt.isBlank(title)) return new RecipeWriteError("레시피 타이틀을 입력해 주세요.");
			if(!Validate.recipeTitle(title)) return new RecipeWriteError("너무 길거나 사용할 수 없는 타이틀입니다.");
		}

		if(coverImage.isEmpty()) return new RecipeWriteError("레시피 표지 이미지를 선택해 주세요.");
		if(steps.size()<1) return new RecipeWriteError("레시피에는 적어도 한 페이지가 필요합니다.");
		if(steps.size()>Validate.MAX_RECIPE_STEPS) return new RecipeWriteError("레시피의 페이지가 너무 많습니다.");
		for(int i = 0; i<steps.size(); i++){
			RecipeStepDelta step = steps.get(i);

			String text = step.getText();
			if(text==null ? step.getOriginalStep()==null : StringsKt.isBlank(text)){
				return new RecipeWriteError(i, "내용을 입력해 주세요.");
			}else if(text!=null&&!Validate.recipeStep(text)){
				return new RecipeWriteError(i, "내용이 너무 깁니다.");
			}
		}
		return null;
	}

	public void apply(RecipeEditorEncoder encoder){
		if(id==null) encoder.writeMode();
		else encoder.editMode(id);

		if(category!=null) encoder.setCategory(category);
		if(title!=null) encoder.setTitle(title);

		if(coverImage.isDirty()) encoder.setCoverImage(Objects.requireNonNull(coverImage.getUri()));
		encoder.setTotalStepCount(steps.size());

		for(int i = 0; i<steps.size(); i++){
			RecipeStepDelta step = steps.get(i);

			Integer originalStep = step.getOriginalStep();

			if(originalStep==null) encoder.newStep(i);
			else if(originalStep==i) encoder.selectStep(i);
			else encoder.moveStep(originalStep, i);

			if(step.isImageEdited()){
				Uri image = step.getImage();
				if(image==null) encoder.removeStepImage();
				else encoder.setStepImage(image);
			}

			if(step.getText()!=null) encoder.setStepText(step.getText());
		}

		if(removedSteps!=null){
			for(int removedStep : removedSteps){
				encoder.removeStep(removedStep);
			}
		}
	}

	@Nullable public Integer getId(){
		return id;
	}
	@Nullable public RecipeCategory getCategory(){
		return category;
	}
	public void setCategory(@Nullable RecipeCategory category){
		this.category = category;
	}
	@Nullable public String getTitle(){
		return title;
	}
	public void setTitle(@Nullable String title){
		this.title = title;
	}
	@Nullable public Uri getCoverImage(){
		return coverImage.getUri();
	}
	public void setCoverImage(@Nullable Uri coverImage){
		this.coverImage.setUri(coverImage);
	}
	public boolean isCoverImageEdited(){
		return this.coverImage.isDirty();
	}
	public void revertCoverImageEdited(){
		this.coverImage.revertEdited();
	}
	public List<RecipeStepDelta> steps(){
		return steps;
	}
	public RecipeStepDelta step(int index){
		return steps.get(index);
	}

	public void removeStep(int index){
		RecipeStepDelta removed = steps.remove(index);
		if(this.removedSteps!=null&&removed.getOriginalStep()!=null){
			this.removedSteps.add(removed.getOriginalStep());
		}
	}

	@Override public String toString(){
		return "RecipeDelta{"+
				"id="+id+
				", category="+category+
				", title='"+title+'\''+
				", coverImage="+coverImage+
				", steps="+steps+
				", removedSteps="+removedSteps+
				'}';
	}
}
