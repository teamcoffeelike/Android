package com.hanul.caramelhomecchiato;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.hanul.caramelhomecchiato.adapter.SearchFriendAdapter;
import com.hanul.caramelhomecchiato.data.User;

import java.util.ArrayList;

public class SearchFriendActivity extends AppCompatActivity{
	private SearchView searchView;
	private RecyclerView recyclerView;
	ArrayList<User> users;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_friend);

		users = new ArrayList<>();
		searchView = findViewById(R.id.searchView);
		recyclerView = findViewById(R.id.searchFriendRecyclerView);
		//SearchFriendAdapter searchFriendAdapter = new SearchFriendAdapter(users);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));




		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override /* 검색어 작성후 키보드 검색 버튼 눌렀을 때 / 내가 작성한 검색어가 query 매개변수로 들어옴 */
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override /* 검색어를 입력하는 동안 실시간으로 목록을 보여줌 / 검색어가 입력될때마다 newText 매개변수로 들어옴 */
			public boolean onQueryTextChange(String newText) {
				return true;
			}
		});
	}
}