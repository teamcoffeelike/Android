package com.hanul.caramelhomecchiato;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hanul.caramelhomecchiato.adapter.RecipeAdapter;
import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.data.RecipeCategory;

public class RecipeActivity extends AppCompatActivity {

	private Fragment recipeCategoryFragment;
	private Recipe recipe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);

		//레시피 뷰 레이아웃 가져오기
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		RecyclerView recyclerViewRecipe = findViewById(R.id.recyclerViewRecipe);

		recyclerViewRecipe.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
		recyclerViewRecipe.setAdapter(new RecipeAdapter());

		//레시피 카테고리 프래그먼트의 인텐트를 받아서 Parcelable 객체 저장
		Intent intent = getIntent();
		recipe = intent.getParcelableExtra("recipe");

		//프래그먼트 생성
		recipeCategoryFragment = new Fragment();

		getSupportFragmentManager().beginTransaction().replace(R.id.recyclerViewRecipe, recipeCategoryFragment);

		//번들 객체 생성
		Bundle bundle = new Bundle();
		bundle.putParcelable("recipe", recipe);

		recipeCategoryFragment.setArguments(bundle);
	}
}