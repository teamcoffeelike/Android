package com.hanul.caramelhomecchiato.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.RecipeActivity;
import com.hanul.caramelhomecchiato.data.RecipeCover;
import com.hanul.caramelhomecchiato.util.GlideUtils;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.UserViewHandler;

public class RecipeAdapter extends BaseAdapter<RecipeCover>{
	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_recipe, parent, false));
	}


	public static final class ViewHolder extends BaseAdapter.ViewHolder<RecipeCover>{
		private final UserViewHandler userViewHandler;

		private final ImageView recipeImage;
		private final TextView title;
		private final RatingBar rating;

		public ViewHolder(@NonNull View itemView){
			super(itemView);
			userViewHandler = new UserViewHandler(itemView);

			recipeImage = itemView.findViewById(R.id.imageViewRecipe);
			title = itemView.findViewById(R.id.textViewRecipeTitle);
			rating = itemView.findViewById(R.id.ratingBar);

			recipeImage.setOnClickListener(v -> {
				Context context = itemView.getContext();
				context.startActivity(new Intent(context, RecipeActivity.class)
						.putExtra(RecipeActivity.EXTRA_RECIPE_ID, getItem().getId()));
			});
		}

		//레시피 사진, 제목, 사용자, 별점
		@Override protected void setItem(int position, RecipeCover recipeCover){
			userViewHandler.setUser(recipeCover.getAuthor());

			Glide.with(itemView)
					.load(recipeCover.getCoverImage())
					.apply(GlideUtils.recipeCover())
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(recipeImage);
			title.setText(recipeCover.getTitle());
			if(recipeCover.getAverageRating()!=null)
				rating.setRating(recipeCover.getAverageRating().floatValue());
			else rating.setRating(0);
		}
	}
}
