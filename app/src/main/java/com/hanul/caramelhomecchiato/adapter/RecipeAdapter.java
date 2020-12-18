package com.hanul.caramelhomecchiato.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.TextInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Recipe;

import org.w3c.dom.Text;

public class RecipeAdapter extends BaseAdapter<Recipe> {
	@NonNull @Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_recipe, parent, false));
	}

	public static final class ViewHolder extends BaseAdapter.ViewHolder<Recipe>{
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
		protected void setItem(int position, Recipe recipe) {
			if (recipe.getPhoto() != null)recipeImage.setImageURI(recipe.getPhoto());
			title.setText(recipe.getTitle());
			author.setText(recipe.getAuthor().getName());
			rating.setRating(recipe.getRating());
		}
	}
}
