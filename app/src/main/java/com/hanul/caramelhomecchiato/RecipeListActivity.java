package com.hanul.caramelhomecchiato;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hanul.caramelhomecchiato.adapter.RecipeAdapter;
import com.hanul.caramelhomecchiato.data.RecipeCategory;
import com.hanul.caramelhomecchiato.data.RecipeCover;
import com.hanul.caramelhomecchiato.data.User;

import java.util.List;

public class RecipeListActivity extends AppCompatActivity{
	private static final String TAG = "RecipeListActivity";

	public static final String EXTRA_RECIPE_CATEGORY = "recipeCategory";
	public static final String EXTRA_MY_RECIPE = "myRecipe";

	private RecipeAdapter recipeAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_list);

		//레시피 카테고리 프래그먼트의 인텐트를 받아서 Parcelable 객체 저장
		Intent intent = getIntent();
		RecipeCategory category = (RecipeCategory)intent.getSerializableExtra(EXTRA_RECIPE_CATEGORY);
		boolean myRecipe = intent.getBooleanExtra(EXTRA_MY_RECIPE, false);
		Log.d(TAG, "onCreate: Category = "+category+", myRecipe = "+myRecipe);

		//레시피 리사이클러뷰 가져오기
		RecyclerView recyclerView = findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

		//레시피 어댑터 데이터 가져오기
		recipeAdapter = new RecipeAdapter();
		List<RecipeCover> recipeCovers = recipeAdapter.elements();

		recipeCovers.add(new RecipeCover(1, RecipeCategory.ETC, "아메리카노", new User(1, "dd", null), 4, null));
		recipeCovers.add(new RecipeCover(1, RecipeCategory.ETC, "카페라떼", new User(1, "dd", null), 4.5f, null));
		recipeCovers.add(new RecipeCover(1, RecipeCategory.ETC, "카페모카", new User(1, "dd", null), 5, null));
		recipeCovers.add(new RecipeCover(1, RecipeCategory.ETC, "딸기스무디", new User(1, "dd", null), 4.5f, null));
		recipeCovers.add(new RecipeCover(1, RecipeCategory.ETC, "레몬아이스티", new User(1, "dd", null), 4, null));

		recyclerView.setAdapter(recipeAdapter);
	}
}