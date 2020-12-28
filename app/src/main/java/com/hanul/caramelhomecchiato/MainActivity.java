package com.hanul.caramelhomecchiato;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.data.UserProfile;
import com.hanul.caramelhomecchiato.fragment.PopularPostFragment;
import com.hanul.caramelhomecchiato.fragment.ProfileFragment;
import com.hanul.caramelhomecchiato.fragment.RecentFragment;
import com.hanul.caramelhomecchiato.fragment.RecipeCategoryFragment;
import com.hanul.caramelhomecchiato.fragment.TimerFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity{
	private static final String TAG = "MainActivity";

	public static final String EXTRA_PROFILE = "profile";
	public static final String EXTRA_RECENT_POSTS = "recentPosts";

	private Fragment popularPostFragment;
	private Fragment recipeCategoryFragment;
	private Fragment timerFragment;
	private Fragment recentFragment;
	private Fragment profileFragment;

	private DrawerLayout drawerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		UserProfile profile = getIntent().getParcelableExtra(EXTRA_PROFILE);
		if(profile==null) throw new IllegalStateException("Profile not available");

		List<Post> recentPosts = getIntent().getParcelableArrayListExtra(EXTRA_RECENT_POSTS);
		if(recentPosts==null) throw new IllegalStateException("Recent posts not available");

		TextView textViewProfileName = findViewById(R.id.textViewProfileName);
		textViewProfileName.setText(profile.getUser().getName());

		popularPostFragment = new PopularPostFragment();
		recipeCategoryFragment = new RecipeCategoryFragment();
		timerFragment = new TimerFragment();
		recentFragment = new RecentFragment();
		profileFragment = new ProfileFragment();

		// 툴바 셋업
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		drawerLayout = findViewById(R.id.drawerLayout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this,
				drawerLayout,
				toolbar,
				R.string.drawer_open,
				R.string.drawer_close);
		toggle.setDrawerIndicatorEnabled(false);
		drawerLayout.addDrawerListener(toggle);
		toggle.syncState();

		findViewById(R.id.imageViewAppBarUserProfile).setOnClickListener(v -> {
			if(drawerLayout.isDrawerVisible(GravityCompat.END)) drawerLayout.closeDrawer(GravityCompat.END);
			else drawerLayout.openDrawer(GravityCompat.END);
		});

		// 내비
		findViewById(R.id.nav).setClickable(true);

		findViewById(R.id.myRecipesMenu).setOnClickListener(v -> {
			startActivity(new Intent(this, RecipeListActivity.class)
					.putExtra(RecipeListActivity.EXTRA_MY_RECIPE, true));
		});
		findViewById(R.id.followsMenu).setOnClickListener(v -> startActivity(new Intent(this, FollowsActivity.class)));
		findViewById(R.id.likesMenu).setOnClickListener(v -> startActivity(new Intent(this, LikesActivity.class)));
		findViewById(R.id.recentActivityMenu).setOnClickListener(v -> {
			drawerLayout.closeDrawer(GravityCompat.END);
			show(recentFragment);
		});
		findViewById(R.id.searchFriendsMenu).setOnClickListener(v -> startActivity(new Intent(this, SearchFriendActivity.class)));
		findViewById(R.id.settingsMenu).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));


		// BottomNavigation & 프래그먼트 셋업
		BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
		bottomNav.setOnNavigationItemSelectedListener(item -> {
			int id = item.getItemId();
			if(id==R.id.topPosts) show(popularPostFragment);
			else if(id==R.id.recipes) show(recipeCategoryFragment);
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

	@Override public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK&&drawerLayout.isDrawerVisible(GravityCompat.END)){
			drawerLayout.closeDrawer(GravityCompat.END);
			return true;
		}else return super.onKeyDown(keyCode, event);
	}

	private void show(Fragment f){
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.mainFrame, f)
				.commit();
	}
}