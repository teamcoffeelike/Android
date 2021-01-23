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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.CaramelHomecchiatoApp;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.WriteRecipeAdapter;
import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.data.RecipeCategory;
import com.hanul.caramelhomecchiato.data.RecipeDelta;
import com.hanul.caramelhomecchiato.data.RecipeWriteError;
import com.hanul.caramelhomecchiato.event.RecipeEditEvent;
import com.hanul.caramelhomecchiato.network.RecipeService;
import com.hanul.caramelhomecchiato.util.RecipeEditorEncoder;
import com.hanul.caramelhomecchiato.util.SignatureManagers;
import com.hanul.caramelhomecchiato.util.SimpleRecipeWriter;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.SpinnerHandler;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import retrofit2.Call;
import retrofit2.Response;

public class WriteRecipeActivity extends AppCompatActivity{
	private static final String TAG = "WriteRecipeActivity";
	private static final Random RNG = new Random();

	public static final String EXTRA_RECIPE = "recipe";
	public static final String EXTRA_EDITING_PAGE = "editingPage";

	private static final String SAVED_DELTA = "recipeToSend";

	private static final String[] IMAGE_MIME_TYPE = {"image/*"};

	private ViewPager2 viewPager;
	private TextView textViewInfo;

	private WriteRecipeAdapter adapter;

	@Nullable private Runnable chooseImageCallback;

	private final ActivityResultLauncher<String[]> chooseCoverImage =
			registerForActivityResult(new OpenDocument(), result -> {
				if(result!=null){
					this.writer.setCoverImage(result);
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
						this.writer.setStepImage(index, result);
						index = -1;
						if(chooseImageCallback!=null){
							chooseImageCallback.run();
							chooseImageCallback = null;
						}
					}else Log.w(TAG, "잘못된 Step "+index);
				}
			});

	private final SimpleRecipeWriter writer = new SimpleRecipeWriter(){
		@Override protected void insertStep(int index){
			super.insertStep(index);
			adapter.notifyItemInserted(index+1);
			int after = adapter.getItemCount()-(index+2);
			if(after>0) adapter.notifyItemRangeChanged(index+2, after);
			viewPager.setCurrentItem(index+1);
		}
		@Override protected void deleteStep(int index){
			new AlertDialog.Builder(WriteRecipeActivity.this)
					.setTitle("정말 삭제하시겠어요?")
					.setPositiveButton("예", (dialog, which) -> {
						super.deleteStep(index);
						adapter.notifyItemRemoved(index+1);
						int after = adapter.getItemCount()-(index+1);
						if(after>0) adapter.notifyItemRangeChanged(index+1, after);
					})
					.setNegativeButton("아니오", (dialog, which) -> {})
					.show();
		}
		@Override public void chooseCoverImage(Runnable onSucceed){
			chooseImageCallback = onSucceed;
			chooseCoverImage.launch(IMAGE_MIME_TYPE);
		}
		@Override public void chooseStepImage(int index, Runnable onSucceed){
			chooseImageCallback = onSucceed;
			WriteRecipeActivity.this.index = index;
			chooseStepImage.launch(IMAGE_MIME_TYPE);
		}
		@Override protected void error(String message){
			Toast.makeText(WriteRecipeActivity.this, message, Toast.LENGTH_SHORT).show();
		}
	};
	private final SpinnerHandler spinnerHandler = new SpinnerHandler(this);

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setTheme(R.style.DarkStatusBarTheme);
		setContentView(R.layout.activity_write_recipe);

		viewPager = findViewById(R.id.viewPager);
		textViewInfo = findViewById(R.id.textViewInfo);
		Button buttonSubmit = findViewById(R.id.buttonSubmit);

		Parcelable recipeExtra = getIntent().getParcelableExtra(EXTRA_RECIPE);
		if(recipeExtra!=null){
			Recipe recipe = (Recipe)recipeExtra;
			writer.setEditingRecipe(recipe);
			writer.setDelta(new RecipeDelta(recipe));
		}else{
			RecipeCategory category = RecipeCategory.values()[RNG.nextInt(RecipeCategory.values().length)];
			writer.setCategory(category);
		}

		int editingPage = getIntent().getIntExtra(EXTRA_EDITING_PAGE, 0);

		adapter = new WriteRecipeAdapter(writer);
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
			RecipeDelta delta = writer.getDelta();

			RecipeWriteError recipeWriteError = delta.validate();
			if(recipeWriteError!=null){
				Toast.makeText(this, recipeWriteError.getMessage(), Toast.LENGTH_SHORT).show();
				viewPager.setCurrentItem(recipeWriteError.getStepIndex()==null ? 0 : recipeWriteError.getStepIndex()+1);
				return;
			}

			if(!delta.hasChange()){
				finish();
				return;
			}

			ExecutorService exec = ((CaramelHomecchiatoApp)getApplication()).executorService;
			spinnerHandler.show();

			exec.submit(() -> {
				Call<JsonObject> call;
				try{
					RecipeEditorEncoder encoder = new RecipeEditorEncoder();
					delta.apply(encoder);
					call = RecipeService.INSTANCE.editRecipe(encoder.toRequestBody(exec, getContentResolver()));
				}catch(Exception e){
					Log.e(TAG, "editRecipe: During Execution", e);
					spinnerHandler.dismiss();
					return;
				}

				try{
					Response<JsonObject> response = call.execute();

					if(response.isSuccessful()){
						JsonObject body = Objects.requireNonNull(response.body());
						if(!body.has("error")){
							ContextCompat.getMainExecutor(this).execute(() -> {
								if(delta.getId()!=null){
									int id = delta.getId();
									if(delta.isAnyImageReplaced()) SignatureManagers.RECIPE_IMAGE.updateKeyForId(id);
									RecipeEditEvent.dispatch(id, delta.getCategory());
								}
								finish();
							});
							return;
						}
						String error = body.get("error").getAsString();
						Log.e(TAG, "editRecipe: "+error);
					}else{
						Log.e(TAG, "editRecipe: "+response.errorBody());
					}
				}catch(Exception ex){
					Log.e(TAG, "editRecipe: ", ex);
				}

				ContextCompat.getMainExecutor(this).execute(() -> {
					Toast.makeText(WriteRecipeActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
					spinnerHandler.dismiss();
				});
			});
		});

		if(editingPage>0&&editingPage<adapter.getItemCount()){
			viewPager.setCurrentItem(editingPage, false);
		}
	}

	@Override protected void onSaveInstanceState(@NonNull Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putParcelable(EXTRA_RECIPE, writer.getEditingRecipe());
		outState.putParcelable(SAVED_DELTA, writer.getDelta());
	}

	@Override protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		Recipe editingRecipe = savedInstanceState.getParcelable(EXTRA_RECIPE);

		writer.setEditingRecipe(editingRecipe);
		writer.setDelta(Objects.requireNonNull(savedInstanceState.getParcelable(SAVED_DELTA)));

		Log.d(TAG, "onRestoreInstanceState: Recipe: "+editingRecipe);
		adapter.notifyDataSetChanged();
	}

	@Override public void onBackPressed(){
		if(writer.getDelta().hasChange())
			new AlertDialog.Builder(this)
					.setTitle("작성중인 레시피를 등록하지 않고 창을 닫으시겠습니까?")
					.setPositiveButton("예", (dialog, which) -> finish())
					.setNegativeButton("계속 작성", (dialog, which) -> {})
					.show();
		else super.onBackPressed();
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

		stb.append('\n').append(writer.getDelta().steps().size()).append(" Steps");

		textViewInfo.setText(stb.toString());
	}
}
