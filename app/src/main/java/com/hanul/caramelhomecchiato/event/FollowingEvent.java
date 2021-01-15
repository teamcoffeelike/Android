package com.hanul.caramelhomecchiato.event;

import androidx.annotation.MainThread;

import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.data.UserProfile;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;

public final class FollowingEvent{
	private FollowingEvent(){}

	private static final Map<Integer, WeakReference<FollowingEventBus>> busMap = new LinkedHashMap<>();

	@MainThread public static Ticket subscribe(int userId, OnFollowingChanged listener){
		return getBus(userId).subscribe(listener);
	}

	@MainThread public static void dispatch(UserProfile profile){
		dispatch(profile.getUser());
	}
	@MainThread public static void dispatch(User user){
		if(user.getFollowedByYou()!=null) dispatch(user.getId(), user.getFollowedByYou());
	}
	@MainThread public static void dispatch(int userId, boolean following){
		getBus(userId).dispatch(following);
	}

	private static FollowingEventBus getBus(int userId){
		WeakReference<FollowingEventBus> busRef = busMap.get(userId);
		FollowingEventBus bus = busRef==null ? null : busRef.get();
		if(bus==null){
			bus = new FollowingEventBus();
			busMap.put(userId, new WeakReference<>(bus));
		}
		return bus;
	}


	private static final class FollowingEventBus extends BooleanStateBus<OnFollowingChanged>{
		@Override protected void dispatch(OnFollowingChanged listener, boolean newState){
			listener.onFollowingChanged(newState);
		}
	}
}
