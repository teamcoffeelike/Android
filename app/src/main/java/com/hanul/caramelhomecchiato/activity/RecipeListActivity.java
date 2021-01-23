package com.hanul.caramelhomecchiato.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.RecipeListAdapter;
import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.data.RecipeCategory;
import com.hanul.caramelhomecchiato.data.RecipeCover;
import com.hanul.caramelhomecchiato.event.FollowingEvent;
import com.hanul.caramelhomecchiato.event.RecipeEditEvent;
import com.hanul.caramelhomecchiato.event.RecipeRateEvent;
import com.hanul.caramelhomecchiato.event.Ticket;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.RecipeService;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.AbstractScrollHandler;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.RecipeScrollHandler;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.SpinnerHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeListActivity extends AppCompatActivity implements AbstractScrollHandler.Listener<RecipeCover>{
	private static final String TAG = "RecipeListActivity";

	public static final String EXTRA_RECIPE_CATEGORY = "recipeCategory";
	public static final String EXTRA_AUTHOR = "author";

	private View root;
	private TextView textViewError;
	private View endOfList;

	private RecipeListAdapter recipeListAdapter;

	@Nullable private RecipeCategory category;
	@Nullable private Integer author;

	private final RecipeScrollHandler recipeScrollHandler = new RecipeScrollHandler(this,
			since -> RecipeService.INSTANCE.recipeList(since, 10, this.category, this.author),
			this);

	@Nullable private final Ticket recipeEditTicket = RecipeEditEvent.subscribeAll(this::onRecipeEdited);
	@Nullable private final Ticket recipeRateTicket = RecipeRateEvent.subscribeAll(this::onRecipeRated);

	//레시피 어댑터 데이터 가져오기
	private SpinnerHandler spinnerHandler = new SpinnerHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setTheme(R.style.DarkStatusBarTheme);
		setContentView(R.layout.activity_recipe_list);

		//레시피 카테고리 프래그먼트의 인텐트를 받아서 Parcelable 객체 저장
		Intent intent = getIntent();
		this.category = (RecipeCategory)intent.getSerializableExtra(EXTRA_RECIPE_CATEGORY);
		if(intent.hasExtra(EXTRA_AUTHOR)){
			this.author = intent.getIntExtra(EXTRA_AUTHOR, -1);
			if(author==-1) author = null;
		}

		root = findViewById(R.id.root);
		textViewError = findViewById(R.id.textViewError);
		endOfList = findViewById(R.id.endOfList);

		if(category!=null)
			root.setBackgroundColor(ContextCompat.getColor(this, category.getColor()));

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

		recipeListAdapter = new RecipeListAdapter(spinnerHandler);
		recyclerView.setAdapter(recipeListAdapter);

		if(category==null){
			recipeListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
				@Override public void onChanged(){
					updateBackgroundColor();
				}
				@Override public void onItemRangeChanged(int positionStart, int itemCount){
					updateBackgroundColor();
				}
				@Override public void onItemRangeInserted(int positionStart, int itemCount){
					updateBackgroundColor();
				}
				@Override public void onItemRangeRemoved(int positionStart, int itemCount){
					updateBackgroundColor();
				}
				@Override public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount){
					updateBackgroundColor();
				}
			});
		}
	}

	@Override protected void onResume(){
		super.onResume();
		recipeScrollHandler.enqueue(recipeListAdapter.elements().isEmpty());
	}

	@Override public void append(List<RecipeCover> recipes, boolean endOfList, boolean reset){
		for(RecipeCover r : recipes){
			FollowingEvent.dispatch(r.getAuthor());
		}
		List<RecipeCover> elements = recipeListAdapter.elements();
		if(reset){
			elements.clear();
			elements.addAll(recipes);
			recipeListAdapter.notifyDataSetChanged();
		}else{
			int size = elements.size();
			elements.addAll(recipes);
			recipeListAdapter.notifyItemRangeInserted(size, recipes.size());
		}
		textViewError.setVisibility(View.GONE);
		this.endOfList.setVisibility(endOfList ? View.VISIBLE : View.GONE);
	}
	@Override public void error(){
		textViewError.setVisibility(View.VISIBLE);
		endOfList.setVisibility(View.GONE);
	}

	private void onRecipeEdited(int recipeId, RecipeCategory newCategory){
		if(newCategory!=null&&category==newCategory){
			recipeScrollHandler.enqueue(true);
			return;
		}
		List<RecipeCover> elements = recipeListAdapter.elements();
		for(int i = 0; i<elements.size(); i++){
			RecipeCover cover = elements.get(i);
			if(cover.getId()!=recipeId) continue;

			if(category!=null&&newCategory!=null){
				elements.remove(i);
				recipeListAdapter.notifyItemRemoved(i);
			}else queueUpdate(recipeId);
			return;
		}
	}

	private void onRecipeRated(int recipeId){
		queueUpdate(recipeId);
	}

	private void queueUpdate(int recipeId){
		RecipeService.INSTANCE.recipe(recipeId).enqueue(new BaseCallback(){
			@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
				List<RecipeCover> elements = recipeListAdapter.elements();
				for(int i = 0; i<elements.size(); i++){
					RecipeCover cover = elements.get(i);
					if(cover.getId()!=recipeId) continue;
					elements.set(i, NetUtils.GSON.fromJson(result, Recipe.class).getCover());
					recipeListAdapter.notifyItemChanged(i);
					return;
				}
			}
			@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
				Log.e(TAG, "recipe: 예상치 못한 오류: "+error);
				error();
			}
			@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
				Log.e(TAG, "recipe: Failure : "+response.errorBody());
				error();
			}
			@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
				Log.e(TAG, "recipe: Unexpected", t);
				error();
			}

			private void error(){
				Toast.makeText(RecipeListActivity.this, "레시피를 불러오는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void updateBackgroundColor(){
		List<RecipeCover> elements = recipeListAdapter.elements();
		root.setBackgroundColor(
				ContextCompat.getColor(
						RecipeListActivity.this,
						elements.isEmpty() ?
								R.color.white :
								elements.get(elements.size()-1).getCategory().getColor()));
	}
}