package com.hanul.caramelhomecchiato.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hanul.caramelhomecchiato.MainActivity;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.RecipeActivity;
import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.data.RecipeCategory;

public class RecipeCategoryFragment extends Fragment{
	private LinearLayout hotCoffee;
	private LinearLayout iceCoffee;
	private LinearLayout tea;
	private LinearLayout ade;
	private LinearLayout smoothie;
	private LinearLayout etc;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_recipe_category, container, false);

		hotCoffee = rootView.findViewById(R.id.hotCoffee);
		iceCoffee = rootView.findViewById(R.id.iceCoffee);
		tea = rootView.findViewById(R.id.tea);
		ade = rootView.findViewById(R.id.ade);
		smoothie = rootView.findViewById(R.id.smoothie);
		etc = rootView.findViewById(R.id.etc);

		//가져오기
		hotCoffee.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showRecipes(RecipeCategory.HOT_COFFEE);
			}
		});

		iceCoffee.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showRecipes(RecipeCategory.ICE_COFFEE);
			}
		});

		tea.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showRecipes(RecipeCategory.TEA);
			}
		});

		ade.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showRecipes(RecipeCategory.ADE);
			}
		});

		smoothie.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showRecipes(RecipeCategory.SMOOTHIE);
			}
		});

		etc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showRecipes(RecipeCategory.ETC);
			}
		});

		return rootView;
	}

	private void showRecipes(RecipeCategory category){
		Intent intent = new Intent(getContext(), RecipeActivity.class);
		intent.putExtra("recipeCategory", category);
		startActivity(intent);
	}
}
