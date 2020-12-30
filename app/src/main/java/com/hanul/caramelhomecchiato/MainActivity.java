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
import com.hanul.caramelhomecchiato.fragment.ProfileFragment;
import com.hanul.caramelhomecchiato.fragment.RecentPostFragment;
import com.hanul.caramelhomecchiato.fragment.RecipeCategoryFragment;
import com.hanul.caramelhomecchiato.fragment.TimerFragment;
import com.hanul.caramelhomecchiato.task.GetProfileTask;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.NetUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity{
	private static final String TAG = "MainActivity";

	private RecentPostFragment recentPostFragment;
	private Fragment recipeCategoryFragment;
	private Fragment timerFragment;
	private Fragment profileFragment;

	private DrawerLayout drawerLayout;
	private TextView textViewProfileName;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textViewProfileName = findViewById(R.id.textViewProfileName);

		recentPostFragment = new RecentPostFragment();
		recipeCategoryFragment = new RecipeCategoryFragment();
		timerFragment = new TimerFragment();
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
		findViewById(R.id.searchFriendsMenu).setOnClickListener(v -> startActivity(new Intent(this, SearchFriendActivity.class)));
		findViewById(R.id.settingsMenu).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

		// BottomNavigation & 프래그먼트 셋업
		BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
		bottomNav.setOnNavigationItemSelectedListener(item -> {
			int id = item.getItemId();
			if(id==R.id.posts) show(recentPostFragment);
			else if(id==R.id.recipes) show(recipeCategoryFragment);
			else if(id==R.id.timer) show(timerFragment);
			else if(id==R.id.profile) show(profileFragment);
			else{
				Log.e(TAG, "onCreate: Invalid navigation item "+id);
				return false;
			}
			return true;
		});

		show(recentPostFragment);
	}

	@Override protected void onResume(){
		super.onResume();
		new GetProfileTask<>(this, Auth.getInstance().getLoginUser())
				.onSucceed((a1, o1) -> {
					if(o1.has("error")){
						String error = o1.get("error").getAsString();
						Log.e(TAG, "postLoad: GetProfileTask 오류: "+error);
						return;
					}
					UserProfile profile = NetUtils.GSON.fromJson(o1, UserProfile.class);
					a1.setProfile(profile);
				})
				.onCancelled((a1, e1) -> {
					Log.e(TAG, "postLoad: GetProfileTask 오류: "+e1);
				}).execute();
	}

	public void setProfile(UserProfile profile){
		textViewProfileName.setText(profile.getUser().getName());
	}

	public void setRecentPosts(List<Post> recentPosts){
		recentPostFragment.setRecentPosts(recentPosts);
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