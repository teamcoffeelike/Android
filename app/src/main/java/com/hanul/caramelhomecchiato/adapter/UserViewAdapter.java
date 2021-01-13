package com.hanul.caramelhomecchiato.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.UserViewHandler;

public class UserViewAdapter extends BaseAdapter<User>{
	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_user, parent, false));
	}


	public static final class ViewHolder extends BaseAdapter.ViewHolder<User>{
		private final UserViewHandler userViewHandler;

		public ViewHolder(@NonNull View itemView){
			super(itemView);
			userViewHandler = new UserViewHandler(itemView);
		}

		@Override protected void setItem(int position, User user){
			userViewHandler.setUser(user);
		}
	}
}
