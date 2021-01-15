package com.hanul.caramelhomecchiato.event;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

import com.hanul.caramelhomecchiato.data.Post;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public final class LikeEventDispatcher{
	private LikeEventDispatcher(){}

	private static final Map<Integer, WeakReference<LikeEventBus>> busMap = new LinkedHashMap<>();

	@MainThread public static Ticket subscribe(int postId, OnLikeChanged listener){
		return getBus(postId).subscribe(listener);
	}

	@MainThread public static void dispatch(Post post){
		dispatch(post.getId(), post.getLikes(), post.getLikedByYou());
	}
	@MainThread public static void dispatch(int userId, int likes, @Nullable Boolean likedByYou){
		getBus(userId).dispatch(likes, likedByYou);
	}

	private static LikeEventBus getBus(int postId){
		WeakReference<LikeEventBus> busRef = busMap.get(postId);
		LikeEventBus bus = busRef==null ? null : busRef.get();
		if(bus==null){
			bus = new LikeEventBus();
			busMap.put(postId, new WeakReference<>(bus));
		}
		return bus;
	}


	private static final class LikeEventBus implements Bus{
		private final WeakHashMap<Ticket, OnLikeChanged> listenerMap = new WeakHashMap<>();

		private int likes;
		private boolean state = false;
		private int idIncrement;

		public Ticket subscribe(OnLikeChanged listener){
			int id = idIncrement++;
			Ticket ticket = new Ticket(this, id);
			if(listenerMap.put(ticket, Objects.requireNonNull(listener))!=null) throw new IllegalStateException("Unexpected");
			dispatch(likes, state);
			return ticket;
		}

		public void dispatch(int newLikes, @Nullable Boolean newState){
			if(likes!=newLikes||(newState!=null&&state!=newState)){
				likes = newLikes;
				if(newState!=null) state = newState;
				for(OnLikeChanged listener : listenerMap.values()){
					listener.onLikeChanged(newLikes, newState==null ? state : newState);
				}
			}
		}

		@Override public void unsubscribe(Ticket ticket){
			if(ticket.bus!=this) throw new IllegalArgumentException("Ticket not registered here");
			listenerMap.remove(ticket);
		}
	}
}
