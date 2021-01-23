package com.hanul.caramelhomecchiato.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.CaramelHomecchiatoApp;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.event.PostEditEvent;
import com.hanul.caramelhomecchiato.network.PostService;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.GlideUtils;
import com.hanul.caramelhomecchiato.util.IOUtils;
import com.hanul.caramelhomecchiato.util.SignatureManagers;
import com.hanul.caramelhomecchiato.util.Validate;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.SpinnerHandler;

import java.io.IOException;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class WritePostActivity extends AppCompatActivity{
	private static final String TAG = "WritePostActivity";

	public static final String EXTRA_POST = "post";

	private static final int GRANT_IMAGE_PERMS = 2;
	private static final int PICK_IMAGE = 4;

	private ImageView imageViewPostImage;
	private TextView textViewAttach;
	private EditText editTextPost;
	private Button buttonSubmit;

	private final SpinnerHandler spinnerHandler = new SpinnerHandler(this);

	@Nullable private Post post;

	@Nullable private Uri image;
	private boolean postEdited = false;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_post);

		Parcelable postExtra = getIntent().getParcelableExtra(EXTRA_POST);
		if(postExtra!=null){
			if(!(postExtra instanceof Post)){
				throw new IllegalStateException("WritePostActivity에 Post 제공되지 않음");
			}
			post = (Post)postExtra;
		}

		imageViewPostImage = findViewById(R.id.imageViewPostImage);
		textViewAttach = findViewById(R.id.textViewAttach);
		editTextPost = findViewById(R.id.editTextPost);
		buttonSubmit = findViewById(R.id.buttonSubmit);

		imageViewPostImage.setOnClickListener(v -> pickImage(true));

		editTextPost.addTextChangedListener(new TextWatcher(){
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count){
				postEdited = true;
				buttonSubmit.setEnabled(true);
			}
			@Override public void afterTextChanged(Editable s){}
		});
		if(post!=null){
			editTextPost.setText(post.getText());

			Glide.with(this)
					.load(post.getImage())
					.apply(GlideUtils.postImage())
					.signature(SignatureManagers.POST_IMAGE.getKeyForId(post.getId()))
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(imageViewPostImage);
			if(post.getImage()!=null){
				textViewAttach.setVisibility(View.INVISIBLE);
			}
		}else{
			Glide.with(this)
					.load((Uri)null)
					.apply(GlideUtils.postImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(imageViewPostImage);
		}

		buttonSubmit.setOnClickListener(v -> {
			if(!Validate.postText(editTextPost.getText())){
				Toast.makeText(WritePostActivity.this, "메시지가 너무 깁니다.", Toast.LENGTH_SHORT).show();
				return;
			}

			String text = editTextPost.getText().toString();

			if(post!=null){
				CaramelHomecchiatoApp app = (CaramelHomecchiatoApp)getApplication();

				@Nullable Future<Response<JsonObject>> editPostImage = image==null ?
						null :
						app.executorService.submit(() -> {
							byte[] read = IOUtils.read(getContentResolver(), image);
							return PostService.editPostImage(post.getId(), read).execute();
						});

				@Nullable Future<Response<JsonObject>> editPost = postEdited ?
						app.executorService.submit(() -> PostService.INSTANCE.editPost(post.getId(), text).execute()) :
						null;

				if(editPostImage==null&&editPost==null){
					finish();
					return;
				}

				spinnerHandler.show();
				app.executorService.submit(() -> {
					boolean editPostImageSucceed = check(editPostImage, "editPostImage");
					boolean editPostSucceed = check(editPost, "editPost");

					ContextCompat.getMainExecutor(this).execute(() -> {
						spinnerHandler.dismiss();
						if(!editPostImageSucceed){
							if(!editPostSucceed)
								Toast.makeText(this, "포스트 변경 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
							else
								Toast.makeText(this, "포스트 이미지 변경 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
						}else if(!editPostSucceed)
							Toast.makeText(this, "포스트 내용 변경 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
						else{
							if(editPostImage!=null){
								SignatureManagers.POST_IMAGE.updateKeyForId(post.getId());
							}
							PostEditEvent.dispatch(post);

							setResult(RESULT_OK);
							finish();
						}
					});
				});
			}else{
				if(image==null){
					Toast.makeText(this, "이미지를 첨부해 주세요.", Toast.LENGTH_SHORT).show();
					return;
				}

				byte[] read;
				try{
					read = IOUtils.read(getContentResolver(), image);
				}catch(IOException e){
					e.printStackTrace();
					Toast.makeText(this, "이미지를 읽어들이는 중 예상치 못한 오류가 발생하여 포스트를 작성할 수 없습니다.", Toast.LENGTH_SHORT).show();
					return;
				}
				spinnerHandler.show();
				PostService.writePost(text, read).enqueue(new BaseCallback(){
					@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
						setResult(RESULT_OK);
						finish();
					}
					@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
						// TODO
						Log.e(TAG, "profile: Error: "+error);
						error();
					}
					@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
						Log.e(TAG, "profile: Failure: "+response.errorBody());
						error();
					}
					@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
						Log.e(TAG, "profile: Unexpected ", t);
						error();
					}

					private void error(){
						Toast.makeText(WritePostActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
						spinnerHandler.dismiss();
					}
				});
			}
		});

		updateSubmitButton();
	}

	@Override public void onBackPressed(){
		if(image!=null||editTextPost.getText().length()>0)
			new AlertDialog.Builder(this)
					.setTitle("작성중인 포스트를 등록하지 않고 창을 닫으시겠습니까?")
					.setPositiveButton("예", (dialog, which) -> finish())
					.setNegativeButton("계속 작성", (dialog, which) -> {})
					.show();
		else super.onBackPressed();
	}

	@WorkerThread
	@SuppressWarnings({"ConstantConditions", "RedundantSuppression"})
	private boolean check(@Nullable Future<Response<JsonObject>> editFuture, String logCategory){
		if(editFuture==null) return true;
		try{
			Response<JsonObject> response = editFuture.get();
			if(!response.isSuccessful()){
				Log.e(TAG, logCategory+": "+response.errorBody());
				return false;
			}
			JsonObject result = response.body();
			if(result.has("error")){
				Log.e(TAG, logCategory+": Error: "+result.get("error").getAsString());
				return false;
			}

			return true;
		}catch(Exception e){
			Log.e(TAG, logCategory+": ", e);
			return false;
		}
	}

	private void pickImage(boolean requestPermission){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PERMISSION_GRANTED){
				if(requestPermission){
					requestPermissions(new String[]{
							Manifest.permission.READ_EXTERNAL_STORAGE
					}, GRANT_IMAGE_PERMS);
				}else new AlertDialog.Builder(this)
						.setMessage("요청을 처리하기 위한 권한이 없습니다.")
						.setPositiveButton("OK", null).show();
				return;
			}
		}
		Intent intent = new Intent()
				.setType("image/*")
				.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==PICK_IMAGE){
			if(resultCode==RESULT_OK&&data!=null){
				Uri uri = data.getData();
				if(uri!=null){
					image = uri;
					Log.d(TAG, "onActivityResult: Image = "+uri);
					Glide.with(this)
							.load(uri)
							.apply(GlideUtils.postImage())
							.transition(DrawableTransitionOptions.withCrossFade())
							.into(imageViewPostImage);
					updateSubmitButton();
					textViewAttach.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	@Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode==GRANT_IMAGE_PERMS){
			pickImage(false);
		}
	}

	private void updateSubmitButton(){
		buttonSubmit.setEnabled(post!=null ?
				image!=null||(postEdited&&Validate.postText(editTextPost.getText())) :
				image!=null&&(postEdited&&Validate.postText(editTextPost.getText())));
	}
}