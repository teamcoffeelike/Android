package com.hanul.caramelhomecchiato.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.fragment.RecipeCoverFragment;
import com.hanul.caramelhomecchiato.fragment.RecipeRateFragment;
import com.hanul.caramelhomecchiato.fragment.RecipeStepFragment;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.RecipeService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.SpinnerHandler;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeActivity extends AppCompatActivity{
	private static final String TAG = "RecipeActivity";

	public static final String EXTRA_RECIPE_ID = "recipeId";
	private static final String SAVED_INDEX_CACHE = "indexCache";

	private int recipeId;

	private ViewPager2 viewPager;
	private TextView textViewIndex;

	private PagerAdapter adapter;

	@Nullable private Recipe recipe;

	private int indexCache = -1;

	private final SpinnerHandler spinnerHandler = new SpinnerHandler(this);

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);
		setTheme(R.style.RecipeTheme);

		if(savedInstanceState!=null){
			indexCache = savedInstanceState.getInt(SAVED_INDEX_CACHE, -1);
		}

		recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, 0);
		if(recipeId==0) throw new IllegalStateException("RecipeActivity에 recipeId 제공되지 않음");

		viewPager = findViewById(R.id.viewPager);
		textViewIndex = findViewById(R.id.textViewIndex);
		View buttonOption = findViewById(R.id.buttonOption);

		adapter = new PagerAdapter(this);
		viewPager.setAdapter(adapter);

		viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
			@Override public void onPageSelected(int position){
				if(recipe!=null){
					updateIndex();
					indexCache = position;
				}
			}
		});


		PopupMenu popupMenu = new PopupMenu(this, buttonOption);
		popupMenu.getMenuInflater().inflate(R.menu.recipe_option_menu, popupMenu.getMenu());

		popupMenu.setOnMenuItemClickListener(item -> {
			int itemId = item.getItemId();
			if(itemId==R.id.editRecipe){
				startActivity(new Intent(this, WriteRecipeActivity.class)
						.putExtra(WriteRecipeActivity.EXTRA_RECIPE, recipe)
						.putExtra(WriteRecipeActivity.EXTRA_EDITING_PAGE, viewPager.getCurrentItem()));
			}else if(itemId==R.id.deleteRecipe) delete();
			else{
				Log.w(TAG, "RecipeActivity: recipe_option_menu의 알 수 없는 옵션 "+itemId);
				return false;
			}
			return true;
		});

		buttonOption.setOnClickListener(v -> {
			if(recipe!=null&&recipe.getCover().getAuthor().getId()==Auth.getInstance().expectLoginUser()){
				popupMenu.show();
			}
		});
	}

	@Override protected void onSaveInstanceState(@NonNull Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putInt(SAVED_INDEX_CACHE, indexCache);
	}

	@Override protected void onResume(){
		super.onResume();
		spinnerHandler.show();
		RecipeService.INSTANCE.recipe(recipeId).enqueue(new BaseCallback(){
			@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
				setRecipe(NetUtils.GSON.fromJson(result, Recipe.class));
				spinnerHandler.dismiss();
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
				spinnerHandler.dismiss();
			}
		});
	}

	private void setRecipe(Recipe recipe){
		this.recipe = recipe;
		adapter.notifyDataSetChanged();
		if(indexCache >= 0&&indexCache<adapter.getItemCount()){
			viewPager.setCurrentItem(indexCache);
		}
		updateIndex();
	}

	private void updateIndex(){
		textViewIndex.setText(getString(R.string.recipe_index, viewPager.getCurrentItem()+1, adapter.getItemCount()));
	}

	private void delete(){
		Recipe recipe = this.recipe;
		if(recipe==null) return;
		new AlertDialog.Builder(this)
				.setTitle("정말 삭제하시겠습니까?")
				.setPositiveButton("예", (dialog, which) -> {
					spinnerHandler.show();
					RecipeService.INSTANCE.deleteRecipe(recipe.getCover().getId()).enqueue(new BaseCallback(){
						@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
							Toast.makeText(RecipeActivity.this, "레시피를 삭제했습니다.", Toast.LENGTH_SHORT).show();
							finish();
						}
						@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
							Log.e(TAG, "deleteRecipe: 예상치 못한 오류: "+error);
							error();
						}
						@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
							Log.e(TAG, "deleteRecipe: Failure : "+response.errorBody());
							error();
						}
						@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
							Log.e(TAG, "deleteRecipe: Failure ", t);
							error();
						}

						private void error(){
							Toast.makeText(RecipeActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
							spinnerHandler.dismiss();
						}
					});
				})
				.setNegativeButton("아니오", (dialog, which) -> {})
				.show();
	}


	private final class PagerAdapter extends FragmentStateAdapter{
		public PagerAdapter(@NonNull FragmentActivity fragmentActivity){
			super(fragmentActivity);
		}

		@NonNull @Override public Fragment createFragment(int position){
			Objects.requireNonNull(recipe);
			if(position==0) return RecipeCoverFragment.newInstance(recipe);
			else if(position>recipe.steps().size()) return RecipeRateFragment.newInstance(recipe);
			return RecipeStepFragment.newInstance(recipe, position-1);
		}

		@Override public int getItemCount(){
			return recipe==null ? 0 : recipe.steps().size()+2;
		}
	}
}