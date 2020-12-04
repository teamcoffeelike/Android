package com.hanul.caramelhomecchiato;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {
	private static final String TAG = "EditProfileActivity:";
	private ImageButton editProfileSubmit;
	private ImageView imageViewProfile;
	private Button buttonEditProfile;

	private static final int GRANT_CAMERA_PERMS = 1;
	private static final int GRANT_IMAGE_PERMS = 2;

	private static final int REQUEST_IMAGE_ALBUM = 3;
	private static final int REQUEST_IMAGE_CAPTURE = 4;
	private static final int REQUEST_CROP_IMAGE = 5;

	private File file = null;

	private Uri photoUri;

	public String mCurrentPhotoPath;
	public String imageRealPathA, imageDbPathA;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);

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
						pickImage(true);
						break;
					case R.id.camera:
						takePhoto(true);
						break;
				}
				return true;
			});
			popupMenu.show();
		});
	}

	/* 갤러리에서 이미지 가져오기 */
	private void pickImage(boolean requestPermission){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				if (requestPermission) {
					requestPermissions(new String[]{
							Manifest.permission.READ_EXTERNAL_STORAGE
					}, GRANT_IMAGE_PERMS);
				} else new AlertDialog.Builder(this)
						.setMessage("요청을 처리하기 위한 권한이 없습니다.")
						.setPositiveButton("OK", null).show();
				return;
			}
			Intent gintent = new Intent(Intent.ACTION_PICK);
			gintent.setType(MediaStore.Images.Media.CONTENT_TYPE);
			startActivityForResult(gintent, REQUEST_IMAGE_ALBUM);
		}
	}

	/* 카메라로 사진 찍기 */
	private void takePhoto(boolean requestPermission) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
					checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				if (requestPermission) {
					requestPermissions(new String[]{
							Manifest.permission.CAMERA,
							Manifest.permission.READ_EXTERNAL_STORAGE
					}, GRANT_CAMERA_PERMS);
				}else new AlertDialog.Builder(this)
						.setMessage("요청을 처리하기 위한 권한이 없습니다.")
						.setPositiveButton("OK", null).show();
				return;
			}
		}

		Intent cItent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File photoFile = null;
		try {
			photoFile = createImage();
		}catch (IOException e) {
			Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요", Toast.LENGTH_SHORT).show();
			finish();
			e.printStackTrace();
		}
		if (photoFile != null) {
			photoUri = FileProvider.getUriForFile(EditProfileActivity.this,
					"com.hanul.caramelhomecchiato.provider", photoFile);
			cItent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(cItent, REQUEST_IMAGE_CAPTURE);
		}
	}
/*
	*//* 이미지 저장 *//*
	private File createImage() throws IOException{
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "caramel_" + timeStamp + "_";

		File storageDir = new File(Environment.getExternalStorageDirectory() + "/caramel/");

		return image;
	}*/

	/* onActivityResult */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요", Toast.LENGTH_SHORT).show();
		}

		switch (requestCode) {
			case REQUEST_IMAGE_ALBUM:
				if (resultCode == RESULT_OK) {
					if (data == null) return;

					photoUri = data.getData();
					cropImage();
				}
				break;
			case REQUEST_IMAGE_CAPTURE:
				if (resultCode == RESULT_OK) {
					cropImage();

					MediaScannerConnection.scanFile(this,
							new String[]{photoUri.getPath()}, null,
							new MediaScannerConnection.OnScanCompletedListener() {
								@Override
								public void onScanCompleted(String path, Uri uri) {
								}
							});
				}
				break;
			case REQUEST_CROP_IMAGE:
				if (resultCode == RESULT_OK) {
					imageViewProfile.setImageURI(null);
					imageViewProfile.setImageURI(photoUri);
					revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
				}
		}
	}

	/* 권한 설정 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case GRANT_CAMERA_PERMS:
				takePhoto(false);
				break;
			case GRANT_IMAGE_PERMS:
				pickImage(false);
				break;
		}
	}

	/*private void cropImage(Uri uri) {
		Intent cropIntent = new Intent("com.android.camera.action.CROP");

		cropIntent.setDataAndType(uri, "image/*");

		cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		cropIntent.putExtra("scale", true);
		cropIntent.putExtra("return-data", true);

		startActivityForResult(cropIntent, REQUEST_CROP_IMAGE);
	}*/

	public void cropImage() {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");

		List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
		grantUriPermission(list.get(0).activityInfo.packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
		int size = list.size();

		if (size == 0) {
			Toast.makeText(this, "취소되었습니다", Toast.LENGTH_SHORT).show();
			return;
		}else {
			Toast.makeText(this, "잠시만 기다려주세요!", Toast.LENGTH_SHORT).show();
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);

			File cropImgFileName = null;
			try {
				cropImgFileName = createImage();
			}catch (IOException e) {
				e.printStackTrace();
			}
			File folder = new File(Environment.getExternalStorageDirectory() + "/caramel/");
			File tempFile = new File(folder.toString(), cropImgFileName.getName());
			photoUri = FileProvider.getUriForFile(EditProfileActivity.this, "com.hanul.caramelhomecchiato.provider", tempFile);
			intent.putExtra("return-data", false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			intent.putExtra("output", Bitmap.CompressFormat.JPEG.toString());

			Intent i = new Intent(intent);
			ResolveInfo res = list.get(0);
			grantUriPermission(res.activityInfo.packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
			i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
			startActivityForResult(i, REQUEST_CROP_IMAGE);
		}
	}

}//class