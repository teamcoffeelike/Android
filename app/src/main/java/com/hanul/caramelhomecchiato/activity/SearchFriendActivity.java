package com.hanul.caramelhomecchiato.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.SearchFriendAdapter;

public class SearchFriendActivity extends AppCompatActivity{
	private SearchView searchView;
	private RecyclerView recyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_friend);

		searchView = findViewById(R.id.searchView);
		recyclerView = findViewById(R.id.searchFriendRecyclerView);
		SearchFriendAdapter searchFriendAdapter = new SearchFriendAdapter();
		recyclerView.setAdapter(searchFriendAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override /* 검색어 작성후 키보드 검색 버튼 눌렀을 때 / 내가 작성한 검색어가 query 매개변수로 들어옴 */
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override /* 검색어를 입력하는 동안 실시간으로 목록을 보여줌 / 검색어가 입력될때마다 newText 매개변수로 들어옴 */
			public boolean onQueryTextChange(String newText) {
				Log.d("newText", "onQueryTextChange: " + newText);
				return true;
			}
		});

	}
}