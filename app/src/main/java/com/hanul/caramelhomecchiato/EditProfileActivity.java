package com.hanul.caramelhomecchiato;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.provider.MediaStore.EXTRA_OUTPUT;

public class EditProfileActivity extends AppCompatActivity {
	private static final String TAG = "EditProfileActivity:";
	private ImageButton editProfileSubmit;
	private ImageView imageViewProfile;
	private Button buttonEditProfile;

	private static final int GRANT_CAMERA_PERMS = 1;
	private static final int GRANT_IMAGE_PERMS = 2;

	private static final int REQUEST_IMAGE_ALBUM = 3;
	private static final int REQUEST_IMAGE_CAPTURE = 4;
	private static final int REQUEST_IMAGE_CROP = 5;

	Uri imageUri;
	Uri photoURI, albumURI;
	File photoFile = null;

	File imageFile = null;
	String currentPath;
	File file;
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
			Intent pickImageIntent = new Intent(Intent.ACTION_PICK);
			pickImageIntent.setType("image/*");
			pickImageIntent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
			startActivityForResult(pickImageIntent, REQUEST_IMAGE_ALBUM);
		}
	}

	/* 카메라로 사진찍기 */
	private void takePhoto(boolean requestPermission) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
					checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				if (requestPermission) {
					requestPermissions(new String[]{
							Manifest.permission.CAMERA,
							Manifest.permission.READ_EXTERNAL_STORAGE
					}, GRANT_CAMERA_PERMS);
				} else new AlertDialog.Builder(this)
						.setMessage("요청을 처리하기 위한 권한이 없습니다.")
						.setPositiveButton("OK", null).show();
				return;
			}
		}
		/*Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
			File photoFile = null;
			try {
				photoFile = createImageFile2();
			}catch (IOException e) {
				e.printStackTrace();
			}
			if (photoFile != null) {
				Uri photoURI = FileProvider.getUriForFile(this, "com.hanul.caramelhomecchiato.provider", photoFile);
				takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
			}
		}*/
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
				try {
					photoFile = createImageFile();
				}catch (Exception e) {
					e.printStackTrace();
				}
				if (photoFile != null) {
					Uri providerURI = FileProvider.getUriForFile(this, "com.hanul.caramelhomecchiato.fileprovider", photoFile);
					imageUri = providerURI;
					takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);
					startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
				}
			}
		}else {
			Toast.makeText(this, "저장 불가", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	private File createImageFile2() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "Caramel_" + timeStamp + ".jpg";
		//File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		//File storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM), "camera_test");
		File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures" + "home");

		if (!storageDir.exists()) {
			Log.i("mCurrentPhotoPath", storageDir.toString());
			storageDir.mkdirs();
		}

		imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
		return imageFile;
	}

	/* 이미지 파일 만들기 */
	public File createImageFile(){
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "Caramel_" + timeStamp + ".jpg";


		//File storageDir = new File(Environment.getExternalStorageDirectory() + "/test/");
		File storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "caramel");
		if (!storageDir.exists()) {
			Log.i("mCurrentPhotoPath", storageDir.toString());
			storageDir.mkdirs();
		}

		imageFile = new File(storageDir, imageFileName);
		return imageFile;
	}

	/* 갤러리에 이미지 저장 */
	private void galleryAddPic() {
		Log.i("galleryAddPic", "Call");
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

		File f = new File(imageFile.getAbsolutePath());
		Log.d(TAG, "galleryAddPic: " + imageFile.getAbsolutePath());
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
		Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
	}

	/* onActivityResult */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_IMAGE_CAPTURE:
				if(resultCode == RESULT_OK) {
					try {
						Toast.makeText(this, "리퀘스트 캡쳐 처리", Toast.LENGTH_SHORT).show();
						photoURI = imageUri;
						albumURI = Uri.fromFile(photoFile);
						cropImage();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case REQUEST_IMAGE_ALBUM:
				if (resultCode == Activity.RESULT_OK && data != null) {
					Log.d(TAG, "album result: " + photoURI);
					Log.d(TAG, "album result: " + albumURI);
					if (data.getData() != null) {
						try {
							File albumFile = createImageFile();
							photoURI = data.getData();
							albumURI = Uri.fromFile(albumFile);
							cropImage();
						} catch (Exception e) {
							Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
						}
					}
				}
				break;

			case REQUEST_IMAGE_CROP:
				if (resultCode == Activity.RESULT_OK) {

					galleryAddPic();
					imageViewProfile.setImageURI(albumURI);
				}
				break;
		}
	}

	/* 이미지 자르기 */
	public void cropImage(){

		Log.i("cropImage", "Call");
		Log.i("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);

		Intent cropIntent = new Intent("com.android.camera.action.CROP");

		cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		cropIntent.setDataAndType(photoURI, "image/*");
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		cropIntent.putExtra("scale", true);
		cropIntent.putExtra(EXTRA_OUTPUT, albumURI); //저장경로
		startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
	}

	private void setPic() {
		int targetW = imageViewProfile.getWidth();
		int targetH = imageViewProfile.getHeight();

		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;

		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
		imageViewProfile.setImageBitmap(bitmap);

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
}//class