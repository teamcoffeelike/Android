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
			Intent pickImageIntent = new Intent(Intent.ACTION_PICK);
			pickImageIntent.setType("image/*");
			pickImageIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
			startActivityForResult(pickImageIntent, REQUEST_IMAGE_ALBUM);
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
				} else new AlertDialog.Builder(this)
						.setMessage("요청을 처리하기 위한 권한이 없습니다.")
						.setPositiveButton("OK", null).show();
				return;
			}
		}
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
			try {
				file = createImageFile();
			} catch (IOException e) {
				Toast.makeText(this, "오류", Toast.LENGTH_SHORT).show();
			}
			if (file != null) {
				photoUri = FileProvider.getUriForFile(this, getPackageName(), file);
				takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
				startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
			}
		}
	}

		/*try{
			file = createImage();
			Log.d("FilePath ", file.getAbsolutePath());

		}catch(Exception e){
			Log.d("Sub1Add:filepath", "Something Wrong", e);
		}

		imageViewProfile.setVisibility(View.VISIBLE);

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // API24 이상 부터
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					FileProvider.getUriForFile(getApplicationContext(),
							getApplicationContext().getPackageName() + ".fileprovider", file));
			Log.d("sub1:appId", getApplicationContext().getPackageName());
		}else {
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		}

		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
		}*/
		/*Intent cItent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File photoFile = null;
		if (cItent.resolveActivity(getPackageManager()) != null) {
			try {
				photoFile = createImage();
			}catch (IOException e) {
				Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
				return;
			}

			if (photoFile != null) {
				photoUri = FileProvider.getUriForFile(EditProfileActivity.this, getPackageName(), photoFile);
				cItent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
				startActivityForResult(cItent, REQUEST_IMAGE_CAPTURE);
			}
		}*/


	/* 이미지 저장 */
	private File createImageFile() throws IOException{
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "caramel_" + timeStamp + ".jpg";
		File storageDir = Environment.getExternalStorageDirectory();
		File curFile = new File(storageDir, imageFileName);


		/*String imageFileName = "caramel_" + timeStamp + "_";

		File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);

		mCurrentPhotoPath = image.getAbsolutePath();*/
		return curFile;
	}

	private void galleryAddPic() {
		Log.i("galleryAddPic", "Call");
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		sendBroadcast(mediaScanIntent);
		Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
	}


	/* onActivityResult */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap bitmap;
		switch (requestCode) {
			case REQUEST_IMAGE_CAPTURE:
				if (resultCode == RESULT_OK ) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 8;
					bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

					imageViewProfile.setImageBitmap(bitmap);
					//mageViewProfile.setImageURI(photoUri);
				}
				break;
			/*case REQUEST_IMAGE_ALBUM:
				if (resultCode == RESULT_OK) {
					if (data.getData() != null) {
						try {
							File albumFile = null;
							albumFile = createImage();
							photoUri = data.getData();
							//albumUri = Uri.fromFile(albumFile);
							cropImage();
						}catch (Exception e) {
							Log.e("REQUEST_IMAGE_ALBUM", e.toString());
						}
					}
				}
				break;*/
			/*case REQUEST_CROP_IMAGE:
				if (resultCode == RESULT_OK) {
					galleryAddPic();
					//imageViewProfile.setImageURI(albumUri);
				}
				break;
				}*/
		}


		/*if (resultCode != RESULT_OK) {
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
		}*/
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

	/*public void cropImage() {
		Log.i("cropImage", "Call");
		Log.i("cropImage", "photoURI: " + photoUri + "albumUri: " + albumUri);

		Intent takePicIntent = new Intent("android.media.action.IMAGE_CAPTURE");

		*//*cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		cropIntent.setDataAndType(photoUri, "image/*");*//*
		takePicIntent.putExtra("crop", "true");
		takePicIntent.putExtra("outputX", 200);
		takePicIntent.putExtra("outputY", 200);
		takePicIntent.putExtra("aspectX", 1);
		takePicIntent.putExtra("aspectY", 1);
		takePicIntent.putExtra("scale", true);
		takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		takePicIntent.putExtra("output", Bitmap.CompressFormat.JPEG.toString());
		startActivityForResult(takePicIntent, REQUEST_CROP_IMAGE);


		*//*cropIntent.setDataAndType(photoUri, "image/*");

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
		}*//*
	}*/


	// 이미지 로테이트 및 사이즈 변경
	public static Bitmap imageRotateAndResize(String path){ // state 1:insert, 2:update
		BitmapFactory.Options options = new BitmapFactory.Options();
		//options.inSampleSize = 8;

		File file = new File(path);
		if (file != null) {
			// 돌아간 앵글각도 알기
			int rotateAngle = setImageOrientation(file.getAbsolutePath());
			Bitmap bitmapTmp = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

			// 사진 바로 보이게 이미지 돌리기
			Bitmap bitmap = imgRotate(bitmapTmp, rotateAngle);

			return bitmap;
		}
		return null;
	}

	// 사진 찍을때 돌린 각도 알아보기 : 가로로 찍는게 기본임
	public static int setImageOrientation(String path){
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int oriention = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
		return oriention;
	}

	// 이미지 돌리기
	public static Bitmap imgRotate(Bitmap bitmap, int orientation){

		Matrix matrix = new Matrix();

		switch (orientation) {
			case ExifInterface.ORIENTATION_NORMAL:
				return bitmap;
			case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
				matrix.setScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				matrix.setRotate(180);
				break;
			case ExifInterface.ORIENTATION_FLIP_VERTICAL:
				matrix.setRotate(180);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_TRANSPOSE:
				matrix.setRotate(90);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				matrix.setRotate(90);
				break;
			case ExifInterface.ORIENTATION_TRANSVERSE:
				matrix.setRotate(-90);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				matrix.setRotate(-90);
				break;
			default:
				return bitmap;
		}
		try {
			Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			bitmap.recycle();
			return bmRotated;
		}
		catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}

	}
}//class