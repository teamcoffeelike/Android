package com.hanul.caramelhomecchiato.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Notification;
import com.hanul.caramelhomecchiato.data.NotificationVisitor;

public class NotificationAdapter extends BaseAdapter<Notification>{
	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_notification, parent, false));
	}


	public static final class ViewHolder extends BaseAdapter.ViewHolder<Notification> implements NotificationVisitor{
		private final ImageView imageViewNotifyingUserProfile;
		private final TextView textViewNotification;

		public ViewHolder(@NonNull View itemView){
			super(itemView);
			imageViewNotifyingUserProfile = itemView.findViewById(R.id.imageViewNotifyingUserProfile);
			textViewNotification = itemView.findViewById(R.id.textViewNotification);
		}

		@Override protected void setItem(int position, Notification notification){
			// TODO 프로필 이미지
			notification.accept(this);
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
