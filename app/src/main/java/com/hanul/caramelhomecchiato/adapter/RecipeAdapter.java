package com.hanul.caramelhomecchiato.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Recipe;

public class RecipeAdapter extends BaseAdapter<Recipe> {
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_recipe, parent, false));
	}

	public static final class ViewHolder extends  BaseAdapter.ViewHolder<Recipe>{
		private final ImageView recipeImage;
		private final TextView recipeTitle;
		private final TextView recipeAuthor;
		private final RatingBar ratingBar;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			recipeImage = itemView.findViewById(R.id.recipeImage);
			recipeTitle = itemView.findViewById(R.id.recipeTitle);
			recipeAuthor = itemView.findViewById(R.id.recipeAuthor);
			ratingBar = itemView.findViewById(R.id.ratingBar);

			ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
					ratingBar.setRating(rating);
				}
			});
		}

		@Override
		protected void setItem(int position, Recipe element) {
			
		}
	}
}
