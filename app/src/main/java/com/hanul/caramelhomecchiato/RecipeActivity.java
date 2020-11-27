package com.hanul.caramelhomecchiato;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.hanul.caramelhomecchiato.data.Recipe;

public class RecipeActivity extends AppCompatActivity {

Fragment recipeCategoryFragment;
Recipe recipe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);

		Intent intent = getIntent();
		recipe = intent.getParcelableExtra("recipe");

		recipeCategoryFragment = new Fragment();

		getSupportFragmentManager().beginTransaction().replace(R.id.hotCoffee, recipeCategoryFragment);

		Bundle bundle = new Bundle();
		bundle.putParcelable("recipe", recipe);

		recipeCategoryFragment.setArguments(bundle);
	}
}