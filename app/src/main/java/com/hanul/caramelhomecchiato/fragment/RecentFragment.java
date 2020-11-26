package com.hanul.caramelhomecchiato.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.NotificationAdapter;
import com.hanul.caramelhomecchiato.data.Notification;
import com.hanul.caramelhomecchiato.data.User;

import java.util.List;

public class RecentFragment extends Fragment{
	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_recent, container, false);

		RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

		Context context = getContext();
		if(context!=null){
			recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

			NotificationAdapter adapter = new NotificationAdapter();
			List<Notification> notifications = adapter.elements();
			notifications.add(new Notification.Reaction(new User(1, "가나다", null), 15));
			notifications.add(new Notification.Like(new User(1, "가나다", null)));
			notifications.add(new Notification.Follow(new User(1, "가나다", null)));
			notifications.add(new Notification.Reaction(new User(1, "aaa", null), 15));
			notifications.add(new Notification.Like(new User(1, "aaa", null)));
			notifications.add(new Notification.Follow(new User(1, "aaa", null)));
			notifications.add(new Notification.Reaction(new User(1, "bbb", null), 15));
			notifications.add(new Notification.Like(new User(1, "bbb", null)));
			notifications.add(new Notification.Follow(new User(1, "bbb", null)));
			notifications.add(new Notification.Reaction(new User(1, "123", null), 15));
			notifications.add(new Notification.Like(new User(1, "123", null)));
			notifications.add(new Notification.Follow(new User(1, "123", null)));
			notifications.add(new Notification.Reaction(new User(1, "456256", null), 15));
			notifications.add(new Notification.Like(new User(1, "2561345", null)));
			notifications.add(new Notification.Follow(new User(1, "vbsdfgdfdddcc", null)));

			recyclerView.setAdapter(adapter);
		}

		return view;
	}
}
