package com.hanul.caramelhomecchiato.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.User;

public class SearchFriendAdapter extends BaseAdapter<User> {
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_follow, parent, false));
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
