package com.hanul.caramelhomecchiato;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
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
	private Uri photoUri;

	private static final int REQUEST_IMAGE_ALBUM = 1;
	private static final int REQUEST_IMAGE_CAPTURE = 2;

	public String imageRealPathA, imageDbPathA;

	public static final int REQUEST_CODE = 11;

	private File file = null;

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
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, REQUEST_IMAGE_ALBUM);
	}

	/* 카메라로 사진 찍기 */
	private void takePhoto() {
		try{
			file = createFile();
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
		}

	}

	/* 찍은 사진 저장 */
	private File createFile() throws IOException {
		String imageFileName = "My" + tmpDateFormat.format(new Date()) + ".jpg";
		File storageDir = Environment.getExternalStorageDirectory();
		File curFile = new File(storageDir, imageFileName);

		return curFile;
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
}//class

