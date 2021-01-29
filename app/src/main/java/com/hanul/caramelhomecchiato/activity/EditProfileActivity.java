package com.hanul.caramelhomecchiato.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.CaramelHomecchiatoApp;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.UserProfile;
import com.hanul.caramelhomecchiato.event.ProfileImageChangeEvent;
import com.hanul.caramelhomecchiato.network.UserService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.GlideUtils;
import com.hanul.caramelhomecchiato.util.IOUtils;
import com.hanul.caramelhomecchiato.util.SignatureManagers;
import com.hanul.caramelhomecchiato.util.Validate;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.SpinnerHandler;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.UriPermissionHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity{
	private static final String TAG = "EditProfileActivity";

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
	private static final String FILE_PROVIDER_AUTH = "com.hanul.caramelhomecchiato.fileprovider";

	public static final String EXTRA_PROFILE = "profile";

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

		Parcelable profileExtra = getIntent().getParcelableExtra(EXTRA_PROFILE);
		if(!(profileExtra instanceof UserProfile)){
			throw new IllegalStateException("EditProfileActivity에 UserProfile 제공되지 않음");
		}
		UserProfile profile = (UserProfile)profileExtra;

		ImageButton editProfileSubmit = findViewById(R.id.editProfileSubmit);
		imageViewProfile = findViewById(R.id.imageViewProfile);

		Glide.with(this)
				.load(profile.getUser().getProfileImage())
				.apply(GlideUtils.profileImage())
				.signature(SignatureManagers.PROFILE_IMAGE.getKeyForId(profile.getUser().getId()))
				.transition(DrawableTransitionOptions.withCrossFade())
				.into(imageViewProfile);

		EditText editTextName = findViewById(R.id.editTextName);
		EditText editTextMotd = findViewById(R.id.editTextMotd);

		editTextName.setText(profile.getUser().getName());
		editTextMotd.setText(profile.getMotd());

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

			enqueueForm(((CaramelHomecchiatoApp)getApplication()).executorService, name, motd);
		});

		/* 프로필 사진 편집 버튼 클릭 -> 갤러리/사진찍기 팝업메뉴 */
		findViewById(R.id.buttonEditProfileImage).setOnClickListener(v -> {
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
	}

	@Override protected void onDestroy(){
		super.onDestroy();
		removeTempFile();
	}

	@Override public void onBackPressed(){
		if(newProfileImage!=null||nameChanged||motdChanged){
			new AlertDialog.Builder(EditProfileActivity.this)
					.setTitle("변경한 내용을 저장하지 않고 창을 닫겠습니까?")
					.setPositiveButton("예", (dialog, which) -> {
						finish();
					})
					.setNegativeButton("계속 작성", (dialog, which) -> {})
					.show();
		}else super.onBackPressed();
	}

	private void enqueueForm(ExecutorService executorService, String name, String motd){
		@Nullable Future<Response<JsonObject>> setProfileImage =
				newProfileImage==null ? null : executorService.submit(() -> {
					byte[] read;
					read = IOUtils.read(getContentResolver(), newProfileImage);
					return UserService.setProfileImage(read).execute();
				});

		@Nullable Future<Response<JsonObject>> setName =
				name==null ? null : executorService.submit(() -> {
					return UserService.INSTANCE.setName(name).execute();
				});

		@Nullable Future<Response<JsonObject>> setMotd =
				motd==null ? null : executorService.submit(() -> {
					return UserService.INSTANCE.setMotd(motd).execute();
				});

		if(setProfileImage==null&&
				setName==null&&
				setMotd==null){
			finish();
			return;
		}
		spinnerHandler.show();
		executorService.submit(() -> {
			List<String> toasts = new ArrayList<>();

			boolean profileImageSucceed = setProfileImage==null||
					checkTransactionResult(setProfileImage,
							"setProfileImage",
							"프로필 이미지",
							toasts);
			boolean nameSucceed = setName==null||
					checkTransactionResult(setName,
							"setName",
							"이름",
							toasts);
			boolean motdSucceed = setMotd==null||
					checkTransactionResult(setMotd,
							"setMotd",
							"소개글",
							toasts);

			ContextCompat.getMainExecutor(this).execute(() -> {
				for(String toast : toasts){
					Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
				}

				if(profileImageSucceed&&setProfileImage!=null){
					SignatureManagers.PROFILE_IMAGE.updateKeyForId(Auth.getInstance().expectLoginUser());
					ProfileImageChangeEvent.dispatch();
				}

				if(profileImageSucceed&&nameSucceed&&motdSucceed) finish();
				else spinnerHandler.dismiss();
			});
		});
	}

	@WorkerThread
	@SuppressWarnings({"ConstantConditions", "RedundantSuppression"}) // "IntelliJ"
	private boolean checkTransactionResult(Future<Response<JsonObject>> transaction,
	                                       String transactionName,
	                                       String transactionFriendlyName,
	                                       List<String> toasts){
		try{
			Response<JsonObject> response = transaction.get();
			if(!response.isSuccessful()){
				Log.e(TAG, transactionName+": "+response.errorBody().string());
				toasts.add(transactionFriendlyName+" 설정 중 오류가 발생했습니다.");
				return false;
			}
			JsonObject body = response.body();
			if(body.has("error")){
				Log.e(TAG, transactionName+": "+body.get("error").getAsString());
				toasts.add(transactionFriendlyName+" 설정 중 오류가 발생했습니다.");
				return false;
			}
			Log.d(TAG, transactionName+": 성공");
			return true;
		}catch(Exception e){
			Log.e(TAG, transactionName+": Exception", e);
			toasts.add(transactionFriendlyName+" 설정 중 예상치 못한 오류가 발생했습니다.");
			return false;
		}
	}

	/* 갤러리에서 이미지 가져오기 */
	private void pickImage(boolean requestPermission){
		boolean permission;
		if(requestPermission) permission = checkPermissionForRequest(GRANT_IMAGE_PERMS,
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE);
		else permission = checkPermission(
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(!permission) return;

		spinnerHandler.show();
		startActivityForResult(
				new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
						.setType("image/*"),
				REQUEST_PICK_IMAGE);
	}

	/* 카메라로 사진찍기 */
	private void takePhoto(boolean requestPermission){
		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			Toast.makeText(this, "저장 불가", Toast.LENGTH_SHORT).show();
			return;
		}
		boolean permission;
		if(requestPermission) permission = checkPermissionForRequest(GRANT_CAMERA_PERMS,
				Manifest.permission.CAMERA,
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE);
		else permission = checkPermission(
				Manifest.permission.CAMERA,
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(!permission) return;

		spinnerHandler.show();
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(takePhotoIntent.resolveActivity(getPackageManager())!=null){
			Uri uri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTH, createTempFile());
			Intent intent = takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

			permissionHandler.grantPermissions(intent, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

			startActivityForResult(intent, REQUEST_TAKE_PHOTO);
		}
	}

	private boolean checkPermissionForRequest(int permissionRequestCode, String... permissions){
		if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) return true;
		for(String permission : permissions){
			if(checkSelfPermission(permission)!=PackageManager.PERMISSION_GRANTED){
				requestPermissions(permissions, permissionRequestCode);
				return false;
			}
		}
		return true;
	}

	private boolean checkPermission(String... permissions){
		if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) return true;
		for(String permission : permissions){
			if(checkSelfPermission(permission)!=PackageManager.PERMISSION_GRANTED){
				new AlertDialog.Builder(this)
						.setMessage("요청을 처리하기 위한 권한이 없습니다.")
						.setPositiveButton("OK", null).show();
				return false;
			}
		}
		return true;
	}

	private Uri generatePublicImageFile(String type){
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, generateFilename(type));
		contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
		contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
		return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
	}

	private File generateNewImageFile(String type){
		return new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), generateFilename(type));
	}

	private String generateFilename(String type){
		return "Caramel_"+type+"_"+DATE_FORMAT.format(new Date())+".jpg";
	}

	// TODO 작동안함
	private void addToMedia(Uri file){
		Log.d(TAG, "addToMedia: "+file);
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
			Uri result = data==null ? null : data.getData();
			switch(requestCode){
				case REQUEST_TAKE_PHOTO:
					permissionHandler.revokePermissions();
					cropImage(FileProvider.getUriForFile(this, FILE_PROVIDER_AUTH, tempFile));
					break;
				case REQUEST_PICK_IMAGE:
					if(data!=null){
						Log.d(TAG, "onActivityResult: REQUEST_PICK_IMAGE "+result);

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

					Log.d(TAG, "onActivityResult: REQUEST_CROP "+result);
					if(result!=null){
						addToMedia(result);
						Glide.with(this)
								.load(result)
								.apply(GlideUtils.profileImage())
								.transition(DrawableTransitionOptions.withCrossFade())
								.into(imageViewProfile);
						newProfileImage = result;
					}
					removeTempFile();
					spinnerHandler.dismiss();
					break;
			}
		}else{
			spinnerHandler.dismiss();
			permissionHandler.revokePermissions();
		}

	}

	public void cropImage(Uri photoUri){
		Uri output = generatePublicImageFile("Cropped");
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