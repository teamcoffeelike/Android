package com.hanul.caramelhomecchiato.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Notification;
import com.hanul.caramelhomecchiato.data.NotificationVisitor;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
	private final List<Notification> posts = new ArrayList<>();
	private final Context ctx;

	public NotificationAdapter(Context ctx){
		this.ctx = ctx;
	}

	public List<Notification> notifications(){
		return posts;
	}

	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_notification, parent, false));
	}
	@Override public void onBindViewHolder(@NonNull ViewHolder holder, int position){
		holder.setItem(position);
	}
	@Override public int getItemCount(){
		return posts.size();
	}


	public final class ViewHolder extends RecyclerView.ViewHolder implements NotificationVisitor{
		private final ImageView imageViewNotifyingUserProfile;
		private final TextView textViewNotification;

		public ViewHolder(@NonNull View itemView){
			super(itemView);
			imageViewNotifyingUserProfile = itemView.findViewById(R.id.imageViewNotifyingUserProfile);
			textViewNotification = itemView.findViewById(R.id.textViewNotification);
		}

		private void setItem(int position){ // TODO 프로필 이미지
			Notification post = posts.get(position);
			post.accept(this);
		}

		@Override public void visit(Notification.Reaction reaction){
			textViewNotification.setText(reaction.getNotifyingUser().getName()+"가 리액션 달았대,,,,,,,,");
		}
		@Override public void visit(Notification.Like like){
			textViewNotification.setText(like.getNotifyingUser().getName()+"가 니가 좋대,,,,,,,,");
		}
		@Override public void visit(Notification.Follow follow){
			textViewNotification.setText(follow.getNotifyingUser().getName()+"가 너 따라다닌대,,,,,,,,");
		}
	}
}
