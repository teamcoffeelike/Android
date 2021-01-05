package com.hanul.caramelhomecchiato.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.fragment.FindEmailPasswordFragment;
import com.hanul.caramelhomecchiato.fragment.FindPhonePasswordFragment;

import java.util.ArrayList;

public class FindPasswordActivity extends AppCompatActivity {
	private ViewPager viewPager;
	private ViewPagerAdapter pagerAdapter;
	private TabLayout tabLayout;

	private Button buttonSendCode;
	private Button buttonCheckCode;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_password);

		checkDangerousPermissions();

		viewPager = findViewById(R.id.viewPager);
		pagerAdapter = new FindPasswordActivity.ViewPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(pagerAdapter);

		tabLayout = findViewById(R.id.tabLayout);
		tabLayout.setupWithViewPager(viewPager);

		buttonSendCode = findViewById(R.id.buttonSendCode);
		buttonCheckCode = findViewById(R.id.buttonCheckCode);

	}

	private static final class ViewPagerAdapter extends FragmentStatePagerAdapter {
		private ArrayList<Fragment> items = new ArrayList<>();
		private ArrayList<String> tabText = new ArrayList<String>();

		public ViewPagerAdapter(@NonNull FragmentManager fm) {
			super(fm);
			items.add(new FindPhonePasswordFragment());
			items.add(new FindEmailPasswordFragment());

			tabText.add("휴대폰번호로 찾기");
			tabText.add("이메일로 찾기");
		}

		@Nullable
		@Override
		public CharSequence getPageTitle(int position) {
			return tabText.get(position);
		}

		public void addItem(Fragment item) {
			items.add(item);
		}

		@NonNull
		@Override
		public Fragment getItem(int position) { return items.get(position); }

		@Override
		public int getCount() { return items.size(); }
	}

	private void checkDangerousPermissions() {
		String[] permissions = {
				Manifest.permission.RECEIVE_SMS
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
}