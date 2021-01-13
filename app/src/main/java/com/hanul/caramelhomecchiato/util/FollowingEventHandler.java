package com.hanul.caramelhomecchiato.util;

import android.util.Log;

import androidx.annotation.MainThread;

import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.data.UserProfile;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class FollowingEventHandler{
	private FollowingEventHandler(){}

	//private static final String TAG = "FollowingEventHandler";

	private static final Map<Integer, Bus> busMap = new LinkedHashMap<>();

	@MainThread
	public static Ticket subscribe(int userId, OnFollowingChanged listener){
		return getBus(userId).subscribe(listener);
	}

	@MainThread
	public static void dispatch(UserProfile profile){
		dispatch(profile.getUser());
	}

	@MainThread
	public static void dispatch(User user){
		if(user.getFollowedByYou()!=null) dispatch(user.getId(), user.getFollowedByYou());
	}

	@MainThread
	public static void dispatch(int userId, boolean following){
		getBus(userId).dispatch(following);
	}

	private static Bus getBus(int userId){
		Bus bus = busMap.get(userId);
		if(bus==null){
			bus = new Bus(userId);
			busMap.put(userId, bus);
		}
		return bus;
	}


	private static final class Bus{
		private final int userId;
		private final Map<Integer, OnFollowingChanged> listenerMap = new LinkedHashMap<>();

		private boolean state = false;
		private int idIncrement;

		public Bus(int userId){
			this.userId = userId;
		}

		public Ticket subscribe(OnFollowingChanged listener){
			int id = idIncrement++;
			if(listenerMap.put(id, Objects.requireNonNull(listener))!=null) throw new IllegalStateException("Unexpected");
			listener.onFollowingChanged(state);
			//Log.d(TAG, userId+":"+id+" subscribed, "+listenerMap.size()+" listeners");
			return new Ticket(this, id);
		}

		public void dispatch(boolean following){
			if(state!=following){
				state = following;
				for(OnFollowingChanged listener : listenerMap.values()){
					listener.onFollowingChanged(following);
				}
			}
		}

		public void unsubscribe(int id){
			listenerMap.remove(id);
			//Log.d(TAG, userId+":"+id+" unsubscribed, "+listenerMap.size()+" listeners");
		}
	}

	public static final class Ticket{
		private final Bus bus;
		private final int id;

		private boolean unsubscribed = false;

		private Ticket(Bus bus, int id){
			this.bus = bus;
			this.id = id;
		}

		@MainThread
		public void unsubscribe(){
			if(!unsubscribed){
				bus.unsubscribe(id);
				unsubscribed = true;
			}
		}
	}
}
