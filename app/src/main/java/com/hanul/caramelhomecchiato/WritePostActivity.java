package com.hanul.caramelhomecchiato;

import android.Manifest;
import android.app.ProgressDialog;
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

import com.hanul.caramelhomecchiato.task.WritePostTask;
import com.hanul.caramelhomecchiato.util.Validate;

import java.io.File;

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
			}

			dialog.show();

			WritePostTask<WritePostActivity> t = new WritePostTask<>(this, postText, image);
			t.onSucceed((a, o) -> {
				if(o.has("error")){
					Toast.makeText(a, "포스트 생성 중 오류가 발생했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
					a.dialog.dismiss();
				}else{
					a.finish();
				}
			}).onCancelled((a, o) -> a.dialog.dismiss()).execute();
		});
		findViewById(R.id.buttonCancel).setOnClickListener(v -> {
			finish();
		});
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