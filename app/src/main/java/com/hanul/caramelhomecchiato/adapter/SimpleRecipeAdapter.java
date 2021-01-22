package com.hanul.caramelhomecchiato.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.RecipeActivity;
import com.hanul.caramelhomecchiato.data.RecipeCover;
import com.hanul.caramelhomecchiato.util.GlideUtils;

public class SimpleRecipeAdapter extends BaseAdapter<RecipeCover>{
	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_simple_recipe, parent, false));
	}

	private static final class ViewHolder extends BaseAdapter.ViewHolder<RecipeCover>{
		private final ImageView imageViewCover;
		private final TextView textViewTitle;
		private final ImageView imageViewCategoryIcon;

		public ViewHolder(@NonNull View itemView){
			super(itemView);

			imageViewCover = itemView.findViewById(R.id.imageViewCover);
			textViewTitle = itemView.findViewById(R.id.textViewTitle);
			imageViewCategoryIcon = itemView.findViewById(R.id.imageViewCategoryIcon);

			itemView.setOnClickListener(v -> {
				Context context = v.getContext();
				context.startActivity(new Intent(context, RecipeActivity.class)
						.putExtra(RecipeActivity.EXTRA_RECIPE_ID, getItem().getId()));
			});
		}

		@Override protected void setItem(int position, RecipeCover element){
			Glide.with(itemView)
					.load(element.getCoverImage())
					.apply(GlideUtils.recipeCoverNoCenterCrop())
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(imageViewCover);
			textViewTitle.setText(element.getTitle());

			imageViewCategoryIcon.setImageResource(element.getCategory().getIcon());
			imageViewCategoryIcon.setBackgroundTintList(
					new ColorStateList(
							new int[][]{new int[0]},
							new int[]{
									ContextCompat.getColor(itemView.getContext(), element.getCategory().getColor())
							}));
		}
	}
}
