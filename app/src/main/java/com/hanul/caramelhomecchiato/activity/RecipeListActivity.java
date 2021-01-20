package com.hanul.caramelhomecchiato.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.RecipeAdapter;
import com.hanul.caramelhomecchiato.data.RecipeCategory;
import com.hanul.caramelhomecchiato.data.RecipeCover;
import com.hanul.caramelhomecchiato.event.FollowingEvent;
import com.hanul.caramelhomecchiato.network.RecipeService;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.AbstractScrollHandler;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.RecipeScrollHandler;

import java.util.List;

public class RecipeListActivity extends AppCompatActivity implements AbstractScrollHandler.Listener<RecipeCover>{
	private static final String TAG = "RecipeListActivity";

	public static final String EXTRA_RECIPE_CATEGORY = "recipeCategory";
	public static final String EXTRA_AUTHOR = "author";

	private TextView textViewError;
	private View endOfList;

	private RecipeAdapter recipeAdapter;

	@Nullable private RecipeCategory category;
	@Nullable private Integer author;

	private final RecipeScrollHandler recipeScrollHandler = new RecipeScrollHandler(this,
			since -> RecipeService.INSTANCE.recipeList(since, 10, this.category, this.author),
			this);

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_list);

		//레시피 카테고리 프래그먼트의 인텐트를 받아서 Parcelable 객체 저장
		Intent intent = getIntent();
		this.category = (RecipeCategory)intent.getSerializableExtra(EXTRA_RECIPE_CATEGORY);
		if(intent.hasExtra(EXTRA_AUTHOR)){
			this.author = intent.getIntExtra(EXTRA_AUTHOR, -1);
			if(author==-1) author = null;
		}
		Log.d(TAG, "onCreate: Category = "+category+", author = "+author);

		textViewError = findViewById(R.id.textViewError);
		endOfList = findViewById(R.id.endOfList);

		NestedScrollView scrollView = findViewById(R.id.scrollView);
		scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)(v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
			int bottom = v.getChildAt(0).getBottom();
			int y = v.getHeight()+scrollY;

			if(bottom-2000<=y){
				recipeScrollHandler.enqueue();
			}
		});

		//레시피 리사이클러뷰 가져오기
		RecyclerView recyclerView = findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

		//레시피 어댑터 데이터 가져오기
		recipeAdapter = new RecipeAdapter();
		recyclerView.setAdapter(recipeAdapter);
	}

	@Override protected void onResume(){
		super.onResume();
		recipeScrollHandler.enqueue(recipeAdapter.elements().isEmpty());
	}

	@Override public void append(List<RecipeCover> recipes, boolean endOfList, boolean reset){
		for(RecipeCover r : recipes){
			FollowingEvent.dispatch(r.getAuthor());
		}
		List<RecipeCover> elements = recipeAdapter.elements();
		if(reset){
			elements.clear();
			elements.addAll(recipes);
			recipeAdapter.notifyDataSetChanged();
		}else{
			int size = elements.size();
			elements.addAll(recipes);
			recipeAdapter.notifyItemRangeInserted(size, recipes.size());
		}
		textViewError.setVisibility(View.GONE);
		this.endOfList.setVisibility(endOfList ? View.VISIBLE : View.GONE);
	}
	@Override public void error(){
		textViewError.setVisibility(View.VISIBLE);
		endOfList.setVisibility(View.GONE);
	}
}