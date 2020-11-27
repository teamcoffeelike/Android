package com.hanul.caramelhomecchiato;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hanul.caramelhomecchiato.fragment.SimpleImageFragment;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class WritePostActivity extends AppCompatActivity{
	private static final int GRANT_CAMERA_PERMS = 1;
	private static final int GRANT_IMAGE_PERMS = 2;
	private static final int TAKE_PHOTO = 3;
	private static final int PICK_IMAGE = 4;

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
	private final List<Uri> images = new ArrayList<>();

	private PagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_post);

		ViewPager viewPager = findViewById(R.id.viewPager);
		viewPager.setAdapter(adapter = new PagerAdapter(getSupportFragmentManager()));

		findViewById(R.id.buttonTakePhoto).setOnClickListener(v -> takePhoto(true));
		findViewById(R.id.buttonPickImage).setOnClickListener(v -> pickImage(true));
	}

	private void takePhoto(boolean requestPermission){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			if(checkSelfPermission(Manifest.permission.CAMERA)!=PERMISSION_GRANTED||
					checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PERMISSION_GRANTED){
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
		startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAKE_PHOTO);
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
		startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), PICK_IMAGE);
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case PICK_IMAGE:
			if(resultCode==RESULT_OK){
				Uri uri = data.getData();
				if(uri!=null) addImage(uri);
			}
			break;
		case TAKE_PHOTO:
			if(resultCode==RESULT_OK){ // TODO 작동안함. 왜? 저도몰라요
				File storage = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
				try{
					File tempFile = File.createTempFile("Photo_"+DATE_FORMAT.format(new Date())+"_",
							".jpg",
							storage);
					addImage(Uri.fromFile(tempFile));
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			break;
		}
	}

	private void addImage(Uri uri){
		images.add(uri);
		adapter.sync(images);
	}

	@Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
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

	private static final class PagerAdapter extends FragmentStatePagerAdapter{
		private final List<SimpleImageFragment> list = new ArrayList<>();

		public PagerAdapter(@NonNull FragmentManager fm){
			super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		}

		@NonNull @Override public Fragment getItem(int position){
			return list.get(position);
		}
		@Override public int getCount(){
			return list.size();
		}

		public void sync(List<Uri> images){
			if(list.size()>images.size()){
				for(int i = list.size()-images.size(); i>0; i--) list.remove(i);
			}else if(list.size()<images.size()){
				for(int i = images.size()-list.size(); i>0; i--) list.add(new SimpleImageFragment());
			}

			for(int i = 0; i<images.size(); i++){
				Bundle args = new Bundle();
				args.putParcelable("image", images.get(i));
				list.get(i).setArguments(args);
			}

			notifyDataSetChanged();
		}
	}
}