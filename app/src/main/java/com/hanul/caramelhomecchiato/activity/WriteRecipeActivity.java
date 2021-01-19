package com.hanul.caramelhomecchiato.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.CaramelHomecchiatoApp;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.WriteRecipeAdapter;
import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.data.RecipeCategory;
import com.hanul.caramelhomecchiato.data.RecipeCover;
import com.hanul.caramelhomecchiato.data.RecipeStep;
import com.hanul.caramelhomecchiato.data.RecipeTask;
import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.network.RecipeService;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.RecipeWriter;
import com.hanul.caramelhomecchiato.util.Validate;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.SpinnerHandler;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import kotlin.text.StringsKt;
import retrofit2.Call;
import retrofit2.Response;

public class WriteRecipeActivity extends AppCompatActivity implements RecipeWriter{
	private static final String TAG = "WriteRecipeActivity";
	private static final Random RNG = new Random();

	public static final String EXTRA_RECIPE_TO_EDIT = "recipeToEdit";
	public static final String SAVED_RECIPE = "recipe";

	private static final String[] IMAGE_MIME_TYPE = {"image/*"};

	private ViewPager2 viewPager;
	private TextView textViewInfo;

	private WriteRecipeAdapter adapter;

	private Recipe recipe;

	private final SpinnerHandler spinnerHandler = new SpinnerHandler(this);

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_recipe);

		viewPager = findViewById(R.id.viewPager);
		textViewInfo = findViewById(R.id.textViewInfo);
		Button buttonSubmit = findViewById(R.id.buttonSubmit);

		Parcelable recipeExtra = getIntent().getParcelableExtra(EXTRA_RECIPE_TO_EDIT);
		recipe = recipeExtra!=null ?
				(Recipe)recipeExtra :
				new Recipe(
						new RecipeCover(0,
								RecipeCategory.values()[RNG.nextInt(RecipeCategory.values().length)],
								"",
								new User(0,
										"",
										null,
										null,
										null),
								5f,
								null),
						new RecipeStep(0,
								null,
								"",
								null)
				);

		adapter = new WriteRecipeAdapter(this);
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
			@Override public void onChanged(){
				redrawInfo();
			}
			@Override public void onItemRangeChanged(int positionStart, int itemCount){
				redrawInfo();
			}
			@Override public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload){
				redrawInfo();
			}
			@Override public void onItemRangeInserted(int positionStart, int itemCount){
				redrawInfo();
			}
			@Override public void onItemRangeRemoved(int positionStart, int itemCount){
				redrawInfo();
			}
			@Override public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount){
				redrawInfo();
			}
		});

		viewPager.setAdapter(adapter);
		viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
			@Override public void onPageSelected(int position){
				redrawInfo();
			}
		});

		textViewInfo.setOnClickListener(v -> redrawInfo());

		buttonSubmit.setOnClickListener(v -> {
			if(!validateCover()){
				viewPager.setCurrentItem(0);
				return;
			}

			for(int i = 0; i<recipe.steps().size(); i++){
				if(!validateStep(i)){
					viewPager.setCurrentItem(i+1);
					return;
				}
			}

			ExecutorService exec = ((CaramelHomecchiatoApp)getApplication()).executorService;
			Future<Call<JsonObject>> writeRecipe =
					RecipeService.writeRecipe(recipe, exec, getContentResolver());

			spinnerHandler.show();

			exec.submit(() -> {
				try{
					Call<JsonObject> call = writeRecipe.get();
					call.enqueue(new BaseCallback(){
						@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
							finish();
						}
						@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
							Log.e(TAG, "writeRecipe: "+error);
							fuck();
						}
						@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
							Log.e(TAG, "writeRecipe: "+response.errorBody());
							fuck();
						}
						@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
							Log.e(TAG, "writeRecipe: ", t);
							fuck();
						}

						void fuck(){
							Toast.makeText(WriteRecipeActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
							spinnerHandler.dismiss();
						}
					});
				}catch(Exception e){
					Log.e(TAG, "writeRecipe: ", e);
				}
			});
		});
	}

	@Override protected void onSaveInstanceState(@NonNull Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putParcelable(SAVED_RECIPE, recipe);
	}

	@Override protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		Recipe recipeSaved = savedInstanceState.getParcelable(SAVED_RECIPE);
		boolean flagAdapterChanged = false;

		if(recipeSaved!=null){
			recipe = recipeSaved;
			repositionSteps();
			flagAdapterChanged = true;
		}

		if(flagAdapterChanged){
			Log.d(TAG, "onRestoreInstanceState: Recipe: "+recipe);
			adapter.notifyDataSetChanged();
		}
	}

	@Override public void onBackPressed(){
		new AlertDialog.Builder(this)
				.setTitle("작성중인 레시피를 등록하지 않고 창을 닫으시겠습니까?")
				.setPositiveButton("예", (dialog, which) -> finish())
				.setNegativeButton("계속 작성", (dialog, which) -> {})
				.show();
	}

	private void repositionSteps(){
		for(int i = 0; i<recipe.steps().size(); i++){
			RecipeStep step = recipe.steps().get(i);
			if(step.getIndex()!=i){
				step.setIndex(i);
				adapter.notifyItemChanged(i+1);
			}
		}
	}

	private boolean validateCover(){
		RecipeCover cover = recipe.getCover();
		if(StringsKt.isBlank(cover.getTitle())){
			Toast.makeText(this, "레시피 타이틀을 입력해 주세요.", Toast.LENGTH_SHORT).show();
		}else if(!Validate.recipeTitle(cover.getTitle())){
			Toast.makeText(this, "너무 길거나 사용할 수 없는 타이틀입니다.", Toast.LENGTH_SHORT).show();
		}else if(cover.getPhoto()==null){
			Toast.makeText(this, "레시피 표지 사진을 넣어 주세요.", Toast.LENGTH_SHORT).show();
		}else return true;
		return false;
	}

	private boolean validateStep(int index){
		RecipeStep step = recipe.steps().get(index);
		if(StringsKt.isBlank(step.getText())){
			Toast.makeText(this, "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
		}else if(!Validate.recipeStep(step.getText())){
			Toast.makeText(this, "너무 길거나 잘못된 내용입니다.", Toast.LENGTH_SHORT).show();
		}else return true;
		return false;
	}

	@Override public Recipe getRecipe(){
		return recipe;
	}
	@Override public void insertStepAt(int index){
		if(recipe.steps().size() >= Validate.MAX_RECIPE_STEPS){
			Toast.makeText(WriteRecipeActivity.this, "더 이상 단계를 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}
		recipe.steps().add(index, new RecipeStep(index, null, "", null));
		adapter.notifyItemInserted(index+1);
		repositionSteps();
		viewPager.setCurrentItem(index+1);
	}
	@Override public void deleteStepAt(int index){
		if(recipe.steps().size()==1){
			Toast.makeText(WriteRecipeActivity.this, "레시피에는 최소 한 개의 단계가 필요합니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		new AlertDialog.Builder(this)
				.setTitle("정말 삭제하시겠어요?")
				.setPositiveButton("예", (dialog, which) -> {
					recipe.steps().remove(index);
					adapter.notifyItemRemoved(index+1);
					repositionSteps();
				})
				.setNegativeButton("아니오", (dialog, which) -> {})
				.show();
	}

	@Override public void setCategory(RecipeCategory category){
		if(recipe.getCover().getCategory()!=category){
			recipe.getCover().setCategory(category);
		}
	}
	@Override public void setTitle(String title){
		recipe.getCover().setTitle(title);
	}
	@Override public void setStepText(int index, String text){
		recipe.steps().get(index).setText(text);
	}
	@Override public void setStepTask(int index, @Nullable RecipeTask task){
		recipe.steps().get(index).setTask(task);
	}

	@Nullable private Runnable chooseImageCallback;

	private final ActivityResultLauncher<String[]> chooseTitleImage =
			registerForActivityResult(new OpenDocument(), result -> {
				if(result!=null){
					recipe.getCover().setPhoto(result);
					if(chooseImageCallback!=null){
						chooseImageCallback.run();
						chooseImageCallback = null;
					}
				}
			});

	private int index = -1;

	private final ActivityResultLauncher<String[]> chooseStepImage =
			registerForActivityResult(new OpenDocument(), result -> {
				if(result!=null){
					if(index >= 0){
						recipe.steps().get(index).setImage(result);
						index = -1;
						if(chooseImageCallback!=null){
							chooseImageCallback.run();
							chooseImageCallback = null;
						}
					}else Log.w(TAG, "잘못된 RecipeStep index "+index);
				}
			});

	@Override public void chooseTitleImage(Runnable onSucceed){
		chooseImageCallback = onSucceed;
		chooseTitleImage.launch(IMAGE_MIME_TYPE);
	}
	@Override public void chooseStepImage(int index, Runnable onSucceed){
		chooseImageCallback = onSucceed;
		this.index = index;
		chooseStepImage.launch(IMAGE_MIME_TYPE);
	}


	private void redrawInfo(){
		int currentItem = viewPager.getCurrentItem();

		StringBuilder stb = new StringBuilder()
				.append(currentItem+1)
				.append('/')
				.append(adapter.getItemCount());

		if(currentItem==0){
			stb.append(" Cover");
		}else{
			stb.append(" Step ").append(currentItem);
		}

		// LINE 2

		stb.append('\n').append(recipe.steps().size()).append(" Steps");

		textViewInfo.setText(stb.toString());
	}
}
