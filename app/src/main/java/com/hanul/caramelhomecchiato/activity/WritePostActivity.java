package com.hanul.caramelhomecchiato.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.PostAdapter;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.network.PostService;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.GlideUtils;
import com.hanul.caramelhomecchiato.util.IOUtils;
import com.hanul.caramelhomecchiato.util.Validate;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.SpinnerHandler;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class WritePostActivity extends AppCompatActivity{
	private static final String TAG = "WritePostActivity";

	public static final String EXTRA_POST = "post";

	private static final int GRANT_IMAGE_PERMS = 2;
	private static final int PICK_IMAGE = 4;

	private ImageView imageViewPostImage;
	@Nullable private Uri image;

	private final SpinnerHandler spinnerHandler = new SpinnerHandler(this);
	private Button buttonSubmit;
	private EditText editTextPost;
	private PostAdapter postAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_post);

		Parcelable postExtra = getIntent().getParcelableExtra(EXTRA_POST);

		imageViewPostImage = findViewById(R.id.imageViewPostImage);

		Glide.with(this)
				.load((Uri)null)
				.apply(GlideUtils.postImage())
				.transition(DrawableTransitionOptions.withCrossFade())
				.into(imageViewPostImage);

		imageViewPostImage.setOnClickListener(v -> pickImage(true));

		editTextPost = findViewById(R.id.editTextPost);
		buttonSubmit = findViewById(R.id.buttonSubmit);


		if(postExtra!=null){
			if(!(postExtra instanceof Post)){
				throw new IllegalStateException("WritePostActivity에 Post 제공되지 않음");
			}
			Post post = (Post)postExtra;

			editTextPost.setText(post.getText());

			Glide.with(this)
					.load(post.getImage())
					.apply(GlideUtils.postImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(imageViewPostImage);

			buttonSubmit.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					//TODO 버튼 눌렀을때 수정한 내용 저장
					String postText = editTextPost.getText().toString();
					if(!Validate.postText(postText)){
						Toast.makeText(WritePostActivity.this, "메시지가 너무 깁니다.", Toast.LENGTH_SHORT).show();
						return;
					}


					spinnerHandler.show();
				}
			});

		}else{
			buttonSubmit.setOnClickListener(v -> {
				if(image==null){
					Toast.makeText(this, "이미지를 첨부해 주세요.", Toast.LENGTH_SHORT).show();
					return;
				}

				String postText = editTextPost.getText().toString();
				if(!Validate.postText(postText)){
					Toast.makeText(this, "메시지가 너무 깁니다.", Toast.LENGTH_SHORT).show();
					return;
				}

				ContentResolver contentResolver = getContentResolver();
				byte[] read;
				try{
					read = IOUtils.read(contentResolver, image);
				}catch(IOException e){
					e.printStackTrace();
					Toast.makeText(this, "이미지를 읽어들이는 중 예상치 못한 오류가 발생하여 포스트를 작성할 수 없습니다.", Toast.LENGTH_SHORT).show();
					return;
				}
				spinnerHandler.show();
				PostService.writePost(postText, read).enqueue(new BaseCallback(){
					@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
						finish();
					}
					@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
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
			});
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
					buttonSubmit.setEnabled(true);
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
}