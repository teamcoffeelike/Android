package com.hanul.caramelhomecchiato.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.RecipeCover;

public class RecipeAdapter extends BaseAdapter<RecipeCover> {
	@NonNull @Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_recipe, parent, false));
	}

	public static final class ViewHolder extends BaseAdapter.ViewHolder<RecipeCover>{
		private final ImageView recipeImage;
		private final TextView title;
		private final TextView author;
		private final RatingBar rating;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			recipeImage = itemView.findViewById(R.id.recipeImage);
			title = itemView.findViewById(R.id.textViewRecipeTitle);
			author = itemView.findViewById(R.id.textViewRecipeAuthor);
			rating = itemView.findViewById(R.id.ratingBar);
			
			//RatingBar 클릭시 변경
			rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
					ratingBar.setRating(rating);
					ratingBar.setIsIndicator(true);	//사용자가 변경 불가능
					//ratingBar.setIsIndicator(false);	//사용자가 변경 가능
				}
			});
		}

		//레시피 사진, 제목, 사용자, 별점
		@Override
		protected void setItem(int position, RecipeCover recipeCover) {
			if (recipeCover.getPhoto() != null)recipeImage.setImageURI(Uri.parse(recipeCover.getPhoto()));
			title.setText(recipeCover.getTitle());
			author.setText(recipeCover.getAuthor().getName());
			rating.setRating(recipeCover.getRating());
		}
	}
}
