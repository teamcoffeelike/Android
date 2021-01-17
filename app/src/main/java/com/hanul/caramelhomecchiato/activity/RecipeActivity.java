package com.hanul.caramelhomecchiato.activity;

import android.os.Bundle;

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
import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.fragment.RecipeCoverFragment;
import com.hanul.caramelhomecchiato.fragment.RecipeStepFragment;

import java.util.Arrays;

public class RecipeActivity extends AppCompatActivity{
	private static final String TAG = "RecipeActivity";

	public static final String EXTRA_RECIPE_ID = "recipeId";

	private int recipeId;

	private PagerAdapter adapter;

	@Nullable private Recipe recipe;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);

		recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, 0);
		if(recipeId==0) throw new IllegalStateException("RecipeActivity에 recipeId 제공되지 않음");

		ViewPager2 viewPager = findViewById(R.id.viewPager);

		adapter = new PagerAdapter(this);
		viewPager.setAdapter(adapter);
	}

	@Override protected void onResume(){
		super.onResume();
		setRecipe(new Recipe(
				new RecipeCover(1,
						RecipeCategory.ETC,
						"asdf",
						new User(1, "t", null, null, null),
						1.5f,
						null),
				Arrays.asList(
						new RecipeStep(1, null, "첫번째"+getString(R.string.very_long_text), null),
						new RecipeStep(2, null, "두번째"+getString(R.string.very_long_text), null),
						new RecipeStep(3, null, "세번째"+getString(R.string.very_long_text), null)
				)));
		/*RecipeService.INSTANCE.recipe(recipeId).enqueue(new BaseCallback(){
			@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
				setRecipe(NetUtils.GSON.fromJson(result, Recipe.class));
			}
			@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
				Log.e(TAG, "recipe: "+error);
				error();
			}
			@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
				Log.e(TAG, "recipe: "+response.errorBody());
				error();
			}
			@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
				Log.e(TAG, "recipe: ", t);
				error();
			}

			private void error(){
				Toast.makeText(RecipeActivity.this, "레시피를 불러오는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			}
		});*/
	}

	private void setRecipe(Recipe recipe){
		this.recipe = recipe;
		adapter.notifyDataSetChanged();
	}


	private final class PagerAdapter extends FragmentStateAdapter{
		public PagerAdapter(@NonNull FragmentActivity fragmentActivity){
			super(fragmentActivity);
		}

		@NonNull @Override public Fragment createFragment(int position){
			if(position==0) return RecipeCoverFragment.newInstance(recipe);
			return RecipeStepFragment.newInstance(recipe, position-1);
		}

		@Override public int getItemCount(){
			return recipe==null ? 0 : recipe.steps().size()+1;
		}
	}
}