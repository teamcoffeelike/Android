package com.hanul.caramelhomecchiato.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.SimpleRecipeAdapter;
import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.data.RecipeCover;
import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.RecipeService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.UserViewHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeRateFragment extends Fragment{
	private static final String TAG = "RecipeRateFragment";

	private static final String ARG_RECIPE = "recipe";

	public static RecipeRateFragment newInstance(Recipe recipe){
		RecipeRateFragment f = new RecipeRateFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_RECIPE, recipe);
		f.setArguments(args);
		return f;
	}

	private RatingBar ratingBar;
	private Button buttonRemoveRating;

	private Recipe recipe;

	private UserViewHandler userViewHandler;

	private SimpleRecipeAdapter simpleRecipeAdapter;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_recipe_rate, container, false);

		Bundle arguments = getArguments();
		Parcelable recipeArg = arguments==null ? null : arguments.getParcelable(ARG_RECIPE);
		if(!(recipeArg instanceof Recipe))
			throw new IllegalStateException("RecipeStepImageFragment에 Recipe 제공되지 않음");

		recipe = (Recipe)recipeArg;

		View backgroundLayout = view.findViewById(R.id.backgroundLayout);
		ratingBar = view.findViewById(R.id.ratingBar);
		buttonRemoveRating = view.findViewById(R.id.buttonRemoveRating);
		Button buttonSubmitRating = view.findViewById(R.id.buttonSubmitRating);
		View recipeByUserLayout = view.findViewById(R.id.recipeByUserLayout);
		RecyclerView recyclerViewOtherRecipes = view.findViewById(R.id.recyclerViewOtherRecipes);

		userViewHandler = new UserViewHandler(view);

		buttonRemoveRating.setOnClickListener(v -> {
			RecipeService.INSTANCE.deleteRecipeRating(recipe.getCover().getId()).enqueue(new BaseCallback(){
				@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){}
				@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
					Log.e(TAG, "removeRecipeRating: "+error);
					rateError();
				}
				@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
					Log.e(TAG, "removeRecipeRating: "+response.errorBody());
					rateError();
				}
				@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
					Log.e(TAG, "removeRecipeRating: ", t);
					rateError();
				}
			});
			recipe.getCover().setYourRating(null);
			updateRatingWidget();
		});
		buttonSubmitRating.setOnClickListener(v -> {
			float rating = ratingBar.getRating();
			RecipeService.INSTANCE.rateRecipe(recipe.getCover().getId(), rating).enqueue(new BaseCallback(){
				@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){}
				@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
					Log.e(TAG, "rateRecipe: "+error);
					rateError();
				}
				@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
					Log.e(TAG, "rateRecipe: "+response.errorBody());
					rateError();
				}
				@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
					Log.e(TAG, "rateRecipe: ", t);
					rateError();
				}
			});
			recipe.getCover().setYourRating((double)rating);
			updateRatingWidget();
		});

		backgroundLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), recipe.getCover().getCategory().getColor()));

		User author = recipe.getCover().getAuthor();
		if(author.getId()==Auth.getInstance().expectLoginUser()){
			recipeByUserLayout.setVisibility(View.GONE);
		}else{
			userViewHandler.setUser(author);
		}

		simpleRecipeAdapter = new SimpleRecipeAdapter();
		recyclerViewOtherRecipes.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
		recyclerViewOtherRecipes.setAdapter(simpleRecipeAdapter);

		updateRatingWidget();

		return view;
	}

	private void rateError(){
		Toast.makeText(requireContext(), "레시피 평가를 수정하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
	}

	@Override public void onResume(){
		super.onResume();
		RecipeService.INSTANCE.recipeList(null, 6, null, recipe.getCover().getAuthor().getId()).enqueue(new BaseCallback(){
			@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
				List<RecipeCover> elements = simpleRecipeAdapter.elements();
				elements.clear();

				for(JsonElement e : result.get("recipes").getAsJsonArray()){
					elements.add(NetUtils.GSON.fromJson(e, RecipeCover.class));
				}

				simpleRecipeAdapter.notifyDataSetChanged();
			}
			@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
				Log.e(TAG, "onErrorResponse: "+error);
				error();
			}
			@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
				Log.e(TAG, "onFailedResponse: "+response.errorBody());
				error();
			}
			@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
				Log.e(TAG, "onFailure: ", t);
				error();
			}

			private void error(){
				Toast.makeText(requireContext(), "레시피를 불러오는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void updateRatingWidget(){
		Double rating = recipe.getCover().getYourRating();
		if(rating==null){
			buttonRemoveRating.setVisibility(View.INVISIBLE);
			ratingBar.setRating(2.5f);
		}else{
			buttonRemoveRating.setVisibility(View.VISIBLE);
			ratingBar.setRating(rating.floatValue());
		}
	}
}
