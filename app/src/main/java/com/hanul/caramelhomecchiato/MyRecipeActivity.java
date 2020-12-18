package com.hanul.caramelhomecchiato;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.hanul.caramelhomecchiato.adapter.RecipeAdapter;
import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.data.RecipeCategory;
import com.hanul.caramelhomecchiato.data.User;

import java.util.List;

public class MyRecipeActivity extends AppCompatActivity{

	private RecyclerView recyclerView;
	private RecipeAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_recipe);

		Intent intent = getIntent();
		RecipeCategory category = (RecipeCategory) intent.getSerializableExtra("recipeCategory");

		recyclerView = findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

		RecipeAdapter adapter = new RecipeAdapter();
		List<Recipe> recipes = adapter.elements();

		recipes.add(new Recipe(1, "아메리카노", new User(1, "dd", null), 4, null));
		recipes.add(new Recipe(1, "카페라떼", new User(1, "dd", null), 4.5f, null));
		recipes.add(new Recipe(1, "카페모카", new User(1, "dd", null), 5, null));
		recipes.add(new Recipe(1, "딸기스무디", new User(1, "dd", null), 4.5f, null));
		recipes.add(new Recipe(1, "레몬아이스티", new User(1, "dd", null), 4, null));

		recyclerView.setAdapter(adapter);
	}
}