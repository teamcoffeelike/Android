package com.hanul.caramelhomecchiato.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.RecipeActivity;
import com.hanul.caramelhomecchiato.activity.WriteRecipeActivity;
import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.data.RecipeCover;
import com.hanul.caramelhomecchiato.event.ProfileImageChangeEvent;
import com.hanul.caramelhomecchiato.event.RecipeDeleteEvent;
import com.hanul.caramelhomecchiato.event.Ticket;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.RecipeService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.GlideUtils;
import com.hanul.caramelhomecchiato.util.SignatureManagers;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.SpinnerHandler;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.UserViewHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeListAdapter extends BaseAdapter<RecipeCover>{
	private static final String TAG = "RecipeListAdapter";

	private final Ticket recipeDeleteTicket = RecipeDeleteEvent.subscribeAll(this::onRecipeDeleted);
	private final Ticket profileImageChangeTicket = ProfileImageChangeEvent.subscribe(this::onProfileImageChanged);
	private final SpinnerHandler spinnerHandler;

	public RecipeListAdapter(SpinnerHandler spinnerHandler){
		this.spinnerHandler = spinnerHandler;
	}

	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_recipe, parent, false), spinnerHandler);
	}

	private void onRecipeDeleted(int recipeId){
		List<RecipeCover> elements = elements();
		for(int i = 0; i<elements.size(); i++){
			RecipeCover cover = elements.get(i);
			if(cover.getId()==recipeId){
				elements.remove(i);
				notifyItemRemoved(i);
				return;
			}
		}
	}
	private void onProfileImageChanged(){
		int loginUser = Auth.getInstance().expectLoginUser();
		List<RecipeCover> elements = elements();
		for(int i = 0; i<elements.size(); i++){
			RecipeCover cover = elements.get(i);
			if(cover.getAuthor().getId()==loginUser){
				notifyItemChanged(i);
			}
		}
	}


	public static final class ViewHolder extends BaseAdapter.ViewHolder<RecipeCover>{
		private final SpinnerHandler spinnerHandler;

		private final UserViewHandler userViewHandler;

		private final ImageView imageViewRecipe;
		private final TextView textViewRecipeTitle;
		private final RatingBar ratingBar;
		private final View coloredCircleLayout;
		private final ImageView imageViewCategoryIcon;
		private final View buttonRecipeOption;

		public ViewHolder(@NonNull View itemView, SpinnerHandler spinnerHandler){
			super(itemView);
			this.spinnerHandler = spinnerHandler;

			userViewHandler = new UserViewHandler(itemView).setNameColor(0xFFFFFFFF);

			imageViewRecipe = itemView.findViewById(R.id.imageViewRecipe);
			textViewRecipeTitle = itemView.findViewById(R.id.textViewRecipeTitle);
			ratingBar = itemView.findViewById(R.id.ratingBar);
			coloredCircleLayout = itemView.findViewById(R.id.coloredCircleLayout);
			imageViewCategoryIcon = itemView.findViewById(R.id.imageViewCategoryIcon);
			buttonRecipeOption = itemView.findViewById(R.id.buttonRecipeOption);

			itemView.setOnClickListener(v -> {
				Context context = itemView.getContext();
				context.startActivity(new Intent(context, RecipeActivity.class)
						.putExtra(RecipeActivity.EXTRA_RECIPE_ID, getItem().getId()));
			});

			PopupMenu popupMenu = new PopupMenu(itemView.getContext(), buttonRecipeOption);
			popupMenu.getMenuInflater().inflate(R.menu.recipe_option_menu, popupMenu.getMenu());

			popupMenu.setOnMenuItemClickListener(item -> {
				int itemId = item.getItemId();
				if(itemId==R.id.editRecipe){
					Context ctx = itemView.getContext();
					spinnerHandler.show();
					RecipeService.INSTANCE.recipe(getItem().getId()).enqueue(new BaseCallback(){
						@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
							spinnerHandler.dismiss();
							ctx.startActivity(new Intent(ctx, WriteRecipeActivity.class)
									.putExtra(WriteRecipeActivity.EXTRA_RECIPE,
											NetUtils.GSON.fromJson(result, Recipe.class)));
						}
						@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
							Log.e(TAG, "recipe: 예상치 못한 오류: "+error);
							error();
						}
						@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
							Log.e(TAG, "recipe: Failure : "+response.errorBody());
							error();
						}
						@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
							Log.e(TAG, "recipe: Unexpected", t);
							error();
						}

						private void error(){
							Toast.makeText(itemView.getContext(), "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
							spinnerHandler.dismiss();
						}
					});
				}else if(itemId==R.id.deleteRecipe) delete();
				else{
					Log.w(TAG, "RecipeActivity: recipe_option_menu의 알 수 없는 옵션 "+itemId);
					return false;
				}
				return true;
			});

			buttonRecipeOption.setOnClickListener(v -> popupMenu.show());
		}

		@Override protected void setItem(int position, RecipeCover cover){
			userViewHandler.setUser(cover.getAuthor());

			Glide.with(itemView)
					.load(cover.getCoverImage())
					.apply(GlideUtils.recipeImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.signature(SignatureManagers.RECIPE_IMAGE.getKeyForId(cover.getId()))
					.into(imageViewRecipe);
			textViewRecipeTitle.setText(cover.getTitle());
			if(cover.getAverageRating()!=null)
				ratingBar.setRating(cover.getAverageRating().floatValue());
			else ratingBar.setRating(0);

			imageViewCategoryIcon.setImageResource(cover.getCategory().getIcon());
			int color = ContextCompat.getColor(itemView.getContext(), cover.getCategory().getColor());
			coloredCircleLayout.setBackgroundTintList(new ColorStateList(new int[][]{new int[0]}, new int[]{color}));
			itemView.setBackgroundColor(color);

			buttonRecipeOption.setVisibility(cover.getAuthor().getId()==Auth.getInstance().getLoginUser() ? View.VISIBLE : View.GONE);
		}

		private void delete(){
			new AlertDialog.Builder(itemView.getContext())
					.setTitle("정말 삭제하시겠습니까?")
					.setPositiveButton("예", (dialog, which) -> {
						int id = getItem().getId();
						spinnerHandler.show();
						RecipeService.INSTANCE.deleteRecipe(id).enqueue(new BaseCallback(){
							@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
								spinnerHandler.dismiss();
								Toast.makeText(itemView.getContext(), "레시피를 삭제했습니다.", Toast.LENGTH_SHORT).show();
								RecipeDeleteEvent.dispatch(id);
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
								Toast.makeText(itemView.getContext(), "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
								spinnerHandler.dismiss();
							}
						});
					})
					.setNegativeButton("아니오", (dialog, which) -> {})
					.show();
		}
	}
}
