package com.hanul.caramelhomecchiato;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hanul.caramelhomecchiato.fragment.PopularPostFragment;
import com.hanul.caramelhomecchiato.fragment.ProfileFragment;
import com.hanul.caramelhomecchiato.fragment.RecentFragment;
import com.hanul.caramelhomecchiato.fragment.RecipeCategoryFragment;
import com.hanul.caramelhomecchiato.fragment.TimerFragment;

public class MainActivity extends AppCompatActivity{
	private static final String TAG = "MainActivity";

	private Fragment popularPostFragment;
	private Fragment recipeCategoryFragment;
	private Fragment timerFragment;
	private Fragment recentFragment;
	private Fragment profileFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		popularPostFragment = new PopularPostFragment();
		recipeCategoryFragment = new RecipeCategoryFragment();
		timerFragment = new TimerFragment();
		recentFragment = new RecentFragment();
		profileFragment = new ProfileFragment();

		BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
		bottomNav.setOnNavigationItemSelectedListener(item -> {
			int id = item.getItemId();
			if(id==R.id.topPosts) show(popularPostFragment);
			else if(id==R.id.recipe) show(recipeCategoryFragment);
			else if(id==R.id.timer) show(timerFragment);
			else if(id==R.id.recent) show(recentFragment);
			else if(id==R.id.profile) show(profileFragment);
			else{
				Log.e(TAG, "onCreate: Invalid navigation item "+id);
				return false;
			}
			return true;
		});

		show(popularPostFragment);
	}

	private void show(Fragment f){
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.mainFrame, f)
				.commit();
	}
}