package com.hanul.caramelhomecchiato.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.RecipeListActivity;
import com.hanul.caramelhomecchiato.data.RecipeCategory;

public class RecipeCategoryAdapter extends RecyclerView.Adapter<RecipeCategoryAdapter.ViewHolder>{
	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_recipe_category, parent, false));
	}
	@Override public void onBindViewHolder(@NonNull RecipeCategoryAdapter.ViewHolder holder, int position){
		holder.setItem(RecipeCategory.values()[position]);
	}
	@Override public int getItemCount(){
		return RecipeCategory.values().length;
	}


	public static final class ViewHolder extends RecyclerView.ViewHolder{
		private final ImageView imageViewCategoryIcon;
		private final TextView textViewCategoryName;

		@Nullable private RecipeCategory recipeCategory;

		public ViewHolder(@NonNull View itemView){
			super(itemView);

			this.imageViewCategoryIcon = itemView.findViewById(R.id.imageViewCategoryIcon);
			this.textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);

			itemView.setOnClickListener(v -> {
				if(this.recipeCategory!=null){
					Context context = itemView.getContext();
					context.startActivity(new Intent(context, RecipeListActivity.class)
							.putExtra(RecipeListActivity.EXTRA_RECIPE_CATEGORY, this.recipeCategory));
				}
			});
		}

		private void setItem(RecipeCategory recipeCategory){
			this.recipeCategory = recipeCategory;

			this.itemView.setBackgroundColor(itemView.getContext().getResources().getColor(recipeCategory.getColor()));

			this.imageViewCategoryIcon.setImageResource(recipeCategory.getIcon());
			this.textViewCategoryName.setText(recipeCategory.getName());
		}
	}
}
