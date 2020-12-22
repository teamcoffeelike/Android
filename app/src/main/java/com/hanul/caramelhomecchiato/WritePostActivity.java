package com.hanul.caramelhomecchiato;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hanul.caramelhomecchiato.fragment.SimpleImageFragment;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class WritePostActivity extends AppCompatActivity{
	private static final int GRANT_IMAGE_PERMS = 2;
	private static final int PICK_IMAGE = 4;

	private final List<Uri> images = new ArrayList<>(); // TODO 사진 갯수 제한

	private PagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_post);

		ViewPager viewPager = findViewById(R.id.viewPager);
		viewPager.setAdapter(adapter = new PagerAdapter(getSupportFragmentManager()));

		findViewById(R.id.buttonPickImage).setOnClickListener(v -> pickImage(true));
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
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
			intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		}
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==PICK_IMAGE){
			if(resultCode==RESULT_OK&&data!=null){
				Uri uri = data.getData();
				if(uri!=null) addImage(uri);
				else{
					ClipData clipData = data.getClipData();
					if(clipData!=null){
						for(int i = 0; i<clipData.getItemCount(); i++){
							Uri uri1 = clipData.getItemAt(i).getUri();
							addImage(uri1);
						}
					}
				}
			}
		}
	}

	private void addImage(Uri uri){
		images.add(uri);
		adapter.sync(images);
	}

	@Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode==GRANT_IMAGE_PERMS){
			pickImage(false);
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
				list.get(i).setImage(images.get(i));
			}

			notifyDataSetChanged();
		}
	}
}