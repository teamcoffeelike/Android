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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

	private static final int MY_PERMISSION_CAMERA = 1;
	private static final int REQUEST_TAKE_PHOTO = 2;
	private static final int REQUEST_TAKE_ALBUM = 3;
	private static final int REQUEST_IMAGE_CROP = 4;

	public static final int REQUESTCODE = 11;

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
						getAlbum();
						break;
					case R.id.camera:
						break;
				}
				return true;
			});
			popupMenu.show();
		});
	}

	private void getAlbum() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, REQUEST_TAKE_ALBUM);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == REQUEST_TAKE_ALBUM) {
			if (resultCode == RESULT_OK) {
				try {
					InputStream inputStream = getContentResolver().openInputStream(intent.getData());

					Bitmap img = BitmapFactory.decodeStream(inputStream);
					inputStream.close();

					imageViewProfile.setImageBitmap(img);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_SHORT).show();
			}
		}
	}
}//class