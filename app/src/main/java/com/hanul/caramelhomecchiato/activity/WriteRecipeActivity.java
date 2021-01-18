package com.hanul.caramelhomecchiato.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.data.RecipeCategory;
import com.hanul.caramelhomecchiato.data.RecipeCover;
import com.hanul.caramelhomecchiato.data.RecipeStep;
import com.hanul.caramelhomecchiato.data.RecipeTask;
import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.fragment.WriteRecipeCoverFragment;
import com.hanul.caramelhomecchiato.fragment.WriteRecipeStepFragment;
import com.hanul.caramelhomecchiato.util.Validate;

public class WriteRecipeActivity extends AppCompatActivity{
	private static final String TAG = "WriteRecipeActivity";

	public static final String EXTRA_RECIPE_TO_EDIT = "recipeToEdit";
	public static final String STATE_RECIPE = "recipe";

	private Adapter adapter;

	private Recipe recipe;
	private RecipeStepWriter firstStep = new RecipeStepWriter();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_recipe);

		ViewPager2 viewPager = findViewById(R.id.viewPager);

		Parcelable recipeExtra = getIntent().getParcelableExtra(EXTRA_RECIPE_TO_EDIT);
		if(recipeExtra!=null){
			recipe = (Recipe)recipeExtra;
		}else{
			recipe = new Recipe(
					new RecipeCover(0,
							RecipeCategory.ETC,
							"",
							new User(0,
									"",
									null,
									null,
									null),
							5f,
							null),
					new RecipeStep(0,
							null,
							"",
							null)
			);
		}

		adapter = new Adapter(this);

		viewPager.setAdapter(adapter);
	}

	@Override protected void onSaveInstanceState(@NonNull Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putParcelable(STATE_RECIPE, recipe);
	}

	@Override protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		Recipe recipeSaved = savedInstanceState.getParcelable(STATE_RECIPE);
		if(recipeSaved!=null){
			recipe = recipeSaved;
			repositionSteps();
			adapter.notifyDataSetChanged();
		}
	}

	private void updateCover(){
		// TODO ??
	}

	private void updateStep(int step){
		// TODO ????
	}

	private void repositionSteps(){
		for(int i = 0; i<recipe.steps().size(); i++){
			recipe.steps().get(i).setIndex(i);
		}
	}


	private final class Adapter extends FragmentStateAdapter{
		public Adapter(@NonNull FragmentActivity fragmentActivity){
			super(fragmentActivity);
		}

		@NonNull @Override public Fragment createFragment(int position){
			if(position==0){
				WriteRecipeCoverFragment f = new WriteRecipeCoverFragment();
				f.setWriter(new RecipeCoverWriter());
				return f;
			}else{
				WriteRecipeStepFragment f = new WriteRecipeStepFragment();
				f.setWriter(firstStep.at(position-1));
				return f;
			}
		}
		@Override public int getItemCount(){
			return recipe.steps().size()+1;
		}
	}

	public final class RecipeCoverWriter{
		public RecipeCover getCover(){
			return recipe.getCover();
		}

		public void setCategory(RecipeCategory category){
			if(recipe.getCover().getCategory()!=category){
				recipe.getCover().setCategory(category);
				updateCover();
			}
		}
		public void setTitle(String title){
			recipe.getCover().setTitle(title);
			updateCover();
		}
		public void setTitleImage(@Nullable Uri titleImage){
			recipe.getCover().setPhoto(titleImage);
			updateCover();
		}

		public void addFirstStep(){
			if(recipe.steps().size() >= Validate.MAX_RECIPE_STEPS){
				Toast.makeText(WriteRecipeActivity.this, "더 이상 단계를 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			recipe.steps().add(0, new RecipeStep(0, null, "", null));
			repositionSteps();
			firstStep = firstStep.insertAtPrev();
			adapter.notifyItemInserted(1);
		}
	}

	public final class RecipeStepWriter{
		@Nullable private RecipeStepWriter prev;
		@Nullable private RecipeStepWriter next;

		private int position;

		public RecipeStepWriter(){
			this(null, null);
		}
		private RecipeStepWriter(@Nullable RecipeStepWriter prev, @Nullable RecipeStepWriter next){
			if(prev==null){
				position = 0;
			}else{
				this.prev = prev;
				prev.next = this;
				position = prev.position;
			}
			if(next!=null){
				this.next = next;
				next.prev = this;
				next.recalculatePosition();
			}
		}

		private RecipeStepWriter at(int index){
			RecipeStepWriter w = this;
			for(int i = 0; i<index; i++){
				RecipeStepWriter w2 = w.next;
				if(w2==null){
					w2 = new RecipeStepWriter(w, null);
				}
				w = w2;
			}
			return w;
		}

		private RecipeStepWriter insertAtPrev(){
			return new RecipeStepWriter(prev, this);
		}

		private RecipeStepWriter insertAtNext(){
			return new RecipeStepWriter(this, next);
		}

		private void removeNode(){
			if(prev!=null){
				prev.next = next;
			}
			if(next!=null){
				next.prev = prev;
				next.recalculatePosition();
			}
		}

		private void recalculatePosition(){
			position = prev==null ? 0 : prev.position+1;
			if(next!=null) next.recalculatePosition();
		}

		public RecipeStep getStep(){
			return recipe.steps().get(position);
		}
		public int index(){
			return position;
		}
		public boolean isLastStep(){
			return recipe.steps().size()>position;
		}

		public void setImage(@Nullable Uri image){
			getStep().setImage(image);
			updateStep(position);
		}
		public void setText(String text){
			getStep().setText(text);
			updateStep(position);
		}
		public void setTask(@Nullable RecipeTask task){
			getStep().setTask(task);
			updateStep(position);
		}

		public void addNextStep(){
			if(recipe.steps().size() >= Validate.MAX_RECIPE_STEPS){
				Toast.makeText(WriteRecipeActivity.this, "더 이상 단계를 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			recipe.steps().add(position+1, new RecipeStep(0, null, "", null));
			repositionSteps();
			insertAtNext();
			adapter.notifyItemInserted(position+2);
		}
		public void addPrevStep(){
			if(recipe.steps().size() >= Validate.MAX_RECIPE_STEPS){
				Toast.makeText(WriteRecipeActivity.this, "더 이상 단계를 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			recipe.steps().add(position, new RecipeStep(0, null, "", null));
			repositionSteps();
			RecipeStepWriter writer = insertAtPrev();
			if(writer.prev==null) firstStep = writer;
			adapter.notifyItemInserted(position+1);
		}

		public void remove(){
			if(recipe.steps().size()==1){
				Toast.makeText(WriteRecipeActivity.this, "레시피에는 최소 한 개의 단계가 필요합니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			recipe.steps().remove(position);
			repositionSteps();
			removeNode();
			adapter.notifyItemRemoved(position+1);
		}
	}
}
