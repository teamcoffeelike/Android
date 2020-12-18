package com.hanul.caramelhomecchiato;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hanul.caramelhomecchiato.adapter.RecipeAdapter;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.data.RecipeCategory;
import com.hanul.caramelhomecchiato.data.User;

import java.util.Collections;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

	private RecyclerView recyclerView;
	private RecipeAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);

		//레시피 카테고리 프래그먼트의 인텐트를 받아서 Parcelable 객체 저장
		Intent intent = getIntent();
		RecipeCategory category = (RecipeCategory) intent.getSerializableExtra("recipeCategory");

		//레시피 리사이클러뷰 가져오기
		recyclerView = findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

		//레시피 어댑터 데이터 가져오기
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