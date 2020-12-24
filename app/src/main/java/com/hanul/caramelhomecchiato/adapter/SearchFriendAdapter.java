package com.hanul.caramelhomecchiato.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.SearchFriendActivity;
import com.hanul.caramelhomecchiato.data.User;

import java.util.ArrayList;

public class SearchFriendAdapter extends BaseAdapter<User> {
	private ArrayList<User> data = null;

	@NonNull
	@Override	//화면 붙이기
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(parent.getContext().LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.view_follow, parent, false);
		SearchFriendAdapter.ViewHolder viewHolder = new SearchFriendAdapter.ViewHolder(view);

		return viewHolder;
	}

	public static final class ViewHolder extends BaseAdapter.ViewHolder<User> {
		private final ImageView userProfileImage;
		private final TextView userName;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			userProfileImage = itemView.findViewById(R.id.imageViewFollowUserProfile);
			userName = itemView.findViewById(R.id.textViewFollowUserName);
		}

		@Override
		protected void setItem(int position, User user) {
			userProfileImage.setImageURI(Uri.parse(user.getProfileImage()));
			userName.setText(user.getName());
		}
	}

}
