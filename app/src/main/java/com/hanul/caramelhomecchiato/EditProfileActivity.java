package com.hanul.caramelhomecchiato;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.hanul.caramelhomecchiato.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditProfileActivity extends AppCompatActivity {

	private ImageButton editProfileSubmit;
	private ImageView imageViewProfile;
	private Button buttonEditProfile;

	private String imageFilePath;
	private Uri uri;

	private static final int REQUEST_IMAGE_ALBUM = 111;
	private static final int REQUEST_IMAGE_CAPTURE = 222;
	private static final int CROP_IMAGE = 333;

	public String imageRealPathA, imageDbPathA;

	public static final int REQUEST_CODE = 11;

	private File file;

	java.text.SimpleDateFormat tmpDateFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);

		tmpDateFormat = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss");

		editProfileSubmit = findViewById(R.id.editProfileSubmit);
		imageViewProfile = findViewById(R.id.imageViewProfile);
		buttonEditProfile = findViewById(R.id.buttonEditProfile);

		/* 프로필 사진 편집 버튼 클릭 -> 갤러리/사진찍기 팝업메뉴 */
		buttonEditProfile.setOnClickListener(v -> {
			PopupMenu popupMenu = new PopupMenu(this, v);
			popupMenu.getMenuInflater().inflate(R.menu.edit_profile_menu, popupMenu.getMenu());

			popupMenu.setOnMenuItemClickListener(item -> {
				switch (item.getItemId()) {
					case R.id.gallery:
						getAlbum();
						break;
					case R.id.camera:
						takePhoto();
						break;
				}
				return true;
			});
			popupMenu.show();
			checkDangerousPermissions();
		});
	}

	/* 갤러리에서 이미지 가져오기 */
	private void getAlbum() {
		Intent gintent = new Intent(Intent.ACTION_PICK);
		gintent.setType("image/*");
		gintent.setType(MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(gintent, REQUEST_IMAGE_ALBUM);
	}

	/* 카메라로 사진 찍기 */
	private void takePhoto() {
		Intent cItent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cItent, REQUEST_IMAGE_CAPTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	/* 갤러리에서 이미지 가져오기 */
		if (requestCode == REQUEST_IMAGE_ALBUM && resultCode == RESULT_OK) {
			try {
				InputStream inputStream = getContentResolver().openInputStream(data.getData());

				Bitmap img = BitmapFactory.decodeStream(inputStream);
				inputStream.close();

				imageViewProfile.setImageBitmap(img);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (resultCode == RESULT_CANCELED) {
			Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_SHORT).show();
		}

		/* 카메라로 사진찍기 */
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bitmap bitmap = (Bitmap) data.getExtras().get("data");
			imageViewProfile.setImageBitmap(bitmap);
		} else if (resultCode == RESULT_CANCELED) {
			Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_SHORT).show();
		}
	}

	/* 권한설정 */
	private void checkDangerousPermissions() {
		String[] permissions = {
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.CAMERA
		};

		int permissionCheck = PackageManager.PERMISSION_GRANTED;
		for (int i = 0; i < permissions.length; i++) {
			permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
			if (permissionCheck == PackageManager.PERMISSION_DENIED) {
				break;
			}
		}

		if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

			if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
				Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
			} else {
				ActivityCompat.requestPermissions(this, permissions, 1);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == 1) {
			for (int i = 0; i < permissions.length; i++) {
				if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	public void cropImage() {
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		cropIntent.setDataAndType(uri, "image/*");
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		cropIntent.putExtra("scale", true);
		cropIntent.putExtra("output", true);
		startActivityForResult(cropIntent, CROP_IMAGE);
	}

}//class

