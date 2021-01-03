package com.hanul.caramelhomecchiato;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.network.PostService;
import com.hanul.caramelhomecchiato.util.ContentResolverHelper;
import com.hanul.caramelhomecchiato.util.Validate;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class WritePostActivity extends AppCompatActivity{
	private static final String TAG = "WritePostActivity";

	private static final int GRANT_IMAGE_PERMS = 2;
	private static final int PICK_IMAGE = 4;

	private ImageView imageViewPostImage;
	@Nullable private Uri image;

	private ProgressDialog dialog;
	private Button buttonSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_post);

		dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		imageViewPostImage = findViewById(R.id.imageViewPostImage);

		findViewById(R.id.buttonPickImage).setOnClickListener(v -> pickImage(true));

		EditText editTextPost = findViewById(R.id.editTextPost);

		buttonSubmit = findViewById(R.id.buttonSubmit);
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
				read = ContentResolverHelper.read(contentResolver, image);
			}catch(IOException e){
				e.printStackTrace();
				Toast.makeText(this, "이미지를 읽어들이는 중 예상치 못한 오류가 발생하여 포스트를 작성할 수 없습니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			dialog.show();
			PostService.writePost(postText, read).enqueue(new Callback<JsonObject>(){
				@Override public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
					JsonObject body = response.body();
					if(body.has("error")){
						Log.e(TAG, "profile: 예상치 못한 오류: "+body.get("error").getAsString());
						Toast.makeText(WritePostActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						return;
					}
					finish();
				}
				@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
					Log.e(TAG, "profile: Failure ", t);
					Toast.makeText(WritePostActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
					dialog.dismiss();
				}
			});
		});
		findViewById(R.id.buttonCancel).setOnClickListener(v -> finish());
	}

	@Override protected void onDestroy(){
		super.onDestroy();
		if(dialog!=null){
			dialog.dismiss();
			dialog = null;
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
					imageViewPostImage.setImageURI(uri);
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