package com.hanul.caramelhomecchiato.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.CaramelHomecchiatoApp;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.network.UserService;
import com.hanul.caramelhomecchiato.util.IOUtils;
import com.hanul.caramelhomecchiato.util.SpinnerHandler;
import com.hanul.caramelhomecchiato.util.UriPermissionHandler;
import com.hanul.caramelhomecchiato.util.Validate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity{
	private static final String TAG = "EditProfileActivity";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
	private static final String FILE_PROVIDER_AUTH = "com.hanul.caramelhomecchiato.fileprovider";

	private ImageView imageViewProfile;

	private static final int GRANT_CAMERA_PERMS = 1;
	private static final int GRANT_IMAGE_PERMS = 2;

	private static final int REQUEST_PICK_IMAGE = 3;
	private static final int REQUEST_TAKE_PHOTO = 4;
	private static final int REQUEST_CROP = 5;

	private File tempFile;

	private final SpinnerHandler spinnerHandler = new SpinnerHandler(this);
	private final UriPermissionHandler permissionHandler = new UriPermissionHandler(this);

	private boolean nameChanged = false;
	private boolean motdChanged = false;
	@Nullable private Uri newProfileImage;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);

		ImageButton editProfileSubmit = findViewById(R.id.editProfileSubmit);
		imageViewProfile = findViewById(R.id.imageViewProfile);
		Button buttonEditProfile = findViewById(R.id.buttonEditProfileImage);

		EditText editTextName = findViewById(R.id.editTextName);
		EditText editTextMotd = findViewById(R.id.editTextMotd);
		editTextName.addTextChangedListener(new TextWatcher(){
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count){}
			@Override public void afterTextChanged(Editable s){
				nameChanged = true;
			}
		});
		editTextMotd.addTextChangedListener(new TextWatcher(){
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count){}
			@Override public void afterTextChanged(Editable s){
				motdChanged = true;
			}
		});

		editProfileSubmit.setOnClickListener(v -> {
			String name, motd;

			if(nameChanged){
				name = editTextName.getText().toString().trim();
				if(!Validate.name(name)){
					Toast.makeText(this, "부적합한 이름입니다.", Toast.LENGTH_SHORT).show();
					return;
				}
			}else name = null;
			if(motdChanged){
				motd = editTextMotd.getText().toString().trim();
				if(!Validate.motd(motd)){
					Toast.makeText(this, "부적합한 소개글입니다.", Toast.LENGTH_SHORT).show();
					return;
				}
			}else motd = null;


			ExecutorService executorService = ((CaramelHomecchiatoApp)getApplication()).executorService;
			Future<Response<JsonObject>> setProfileImage , setName , setMotd;

			setProfileImage = newProfileImage==null ? null : executorService.submit(() -> {
				byte[] read;
				read = IOUtils.read(getContentResolver(), newProfileImage);
				return UserService.setProfileImage(read).execute();
			});

			setName = name==null ? null : executorService.submit(() -> {
				return UserService.INSTANCE.setName(name).execute();
			});

			setMotd = motd==null ? null : executorService.submit(() -> {
				return UserService.INSTANCE.setMotd(motd).execute();
			});

			spinnerHandler.show();
			executorService.submit(() -> {
				if(setProfileImage!=null){
					try{
						Response<JsonObject> jsonObjectResponse = setProfileImage.get();
						JsonObject body = jsonObjectResponse.body();
						if(body==null){

						}else if(body.has("error")){
							Log.e(TAG, "setProfileImage: "+body.get("error").getAsString());
							Toast.makeText(this, "프로필 이미지 설정 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
						}
					}catch(ExecutionException e){
						Log.e(TAG, "setProfileImage: Exception", e);
					}catch(InterruptedException e){
						Log.e(TAG, "setProfileImage: Exception", e);
						Thread.currentThread().interrupt();
					}
				}

				if(setName!=null){

				}

				if(setMotd!=null){

				}

				spinnerHandler.dismiss();
			});
		});

		/* 프로필 사진 편집 버튼 클릭 -> 갤러리/사진찍기 팝업메뉴 */
		buttonEditProfile.setOnClickListener(v -> {
			PopupMenu popupMenu = new PopupMenu(this, v);
			popupMenu.getMenuInflater().inflate(R.menu.edit_profile_menu, popupMenu.getMenu());

			popupMenu.setOnMenuItemClickListener(item -> {
				int itemId = item.getItemId();
				if(itemId==R.id.gallery){
					pickImage(true);
				}else if(itemId==R.id.camera){
					takePhoto(true);
				}
				return true;
			});
			popupMenu.show();
		});

		getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true){
			@Override public void handleOnBackPressed(){
				new AlertDialog.Builder(EditProfileActivity.this)
						.setTitle("작성한 내용을 저장하지 않고 창을 닫겠습니까?")
						.setPositiveButton("예", (dialog, which) -> {
							finish();
						})
						.setNegativeButton("계속 작성", (dialog, which) -> {})
						.show();
			}
		});
	}

	@Override protected void onDestroy(){
		super.onDestroy();
		removeTempFile();
	}

	/* 갤러리에서 이미지 가져오기 */
	private void pickImage(boolean requestPermission){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
				if(requestPermission){
					requestPermissions(new String[]{
							Manifest.permission.READ_EXTERNAL_STORAGE
					}, GRANT_IMAGE_PERMS);
				}else new AlertDialog.Builder(this)
						.setMessage("요청을 처리하기 위한 권한이 없습니다.")
						.setPositiveButton("OK", null)
						.show();
				return;
			}

			startActivityForResult(
					new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
							.setType("image/*"),
					REQUEST_PICK_IMAGE);
		}
	}

	/* 카메라로 사진찍기 */
	private void takePhoto(boolean requestPermission){
		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			Toast.makeText(this, "저장 불가", Toast.LENGTH_SHORT).show();
			return;
		}
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			if(checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED||
					checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
				if(requestPermission){
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
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(takePhotoIntent.resolveActivity(getPackageManager())!=null){
			Uri uri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTH, createTempFile());
			Intent intent = takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

			permissionHandler.grantPermissions(intent, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

			startActivityForResult(intent, REQUEST_TAKE_PHOTO);
		}
	}

	/* 이미지 파일 만들기 */
	private File generateNewImageFile(String type){
		String timeStamp = DATE_FORMAT.format(new Date());
		String imageFileName = "Caramel_"+type+"_"+timeStamp+".jpg";

		File storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "caramel");
		if(!storageDir.exists()){
			Log.i("mCurrentPhotoPath", storageDir.toString());
			//noinspection ResultOfMethodCallIgnored
			storageDir.mkdirs();
		}

		return new File(storageDir, imageFileName);
	}

	/* 갤러리에 이미지 저장 */
	private void galleryAddPic(Uri file){
		Log.d(TAG, "galleryAddPic: "+file);
		MediaScannerConnection.scanFile(this,
				new String[]{file.toString()},
				null,
				null);
		Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode==RESULT_OK){
			Uri result = data.getData();
			switch(requestCode){
			case REQUEST_TAKE_PHOTO:
				permissionHandler.revokePermissions();
				cropImage(FileProvider.getUriForFile(this, FILE_PROVIDER_AUTH, tempFile));
				break;
			case REQUEST_PICK_IMAGE:
				if(data!=null){
					Bundle extras = data.getExtras();
					Log.d(TAG, "onActivityResult: REQUEST_PICK_IMAGE "+extras+" "+result);

					File temp = createTempFile();
					try(InputStream is = getContentResolver().openInputStream(result);
					    FileOutputStream fos = new FileOutputStream(temp)){

						IOUtils.copyInto(new BufferedInputStream(is), new BufferedOutputStream(fos));
					}catch(IOException e){
						Log.e(TAG, "onActivityResult: Exception ", e);
					}

					cropImage(FileProvider.getUriForFile(this, FILE_PROVIDER_AUTH, temp));
				}
				break;
			case REQUEST_CROP:
				permissionHandler.revokePermissions();

				Bundle extras = data.getExtras();
				Log.d(TAG, "onActivityResult: REQUEST_CROP "+extras+" "+result);
				if(result!=null){
					galleryAddPic(result);
					imageViewProfile.setImageURI(result);
					newProfileImage = result;
				}
				removeTempFile();
				break;
			}
		}

	}

	/* 이미지 자르기 */
	public void cropImage(Uri photoUri){
		Uri output = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTH, generateNewImageFile("Cropped"));
		Log.i(TAG, "cropImage: photoUri : "+photoUri+" / outputUri : "+output);

		Intent intent = new Intent("com.android.camera.action.CROP")
				.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
				.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
				.setDataAndType(photoUri, "image/*")
				.putExtra("aspectX", 1)
				.putExtra("aspectY", 1)
				.putExtra("scale", true)
				.putExtra(MediaStore.EXTRA_OUTPUT, output)
				.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

		permissionHandler.grantPermissions(intent, output, Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

		startActivityForResult(intent, REQUEST_CROP);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch(requestCode){
		case GRANT_CAMERA_PERMS:
			takePhoto(false);
			break;
		case GRANT_IMAGE_PERMS:
			pickImage(false);
			break;
		}
	}

	private File createTempFile(){
		removeTempFile();
		tempFile = generateNewImageFile("Temp");
		try{
			//noinspection ResultOfMethodCallIgnored
			tempFile.createNewFile();
		}catch(IOException e){
			Log.e(TAG, "pickImage: ", e);
		}
		return tempFile;
	}

	private void removeTempFile(){
		if(tempFile!=null){
			//noinspection ResultOfMethodCallIgnored
			tempFile.delete();
			tempFile = null;
		}
	}
}