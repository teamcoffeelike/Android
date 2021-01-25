package com.hanul.caramelhomecchiato.event;

import androidx.annotation.MainThread;

import com.hanul.caramelhomecchiato.data.Post;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public final class PostLikeEvent{
	private PostLikeEvent(){}

	private static final Map<Integer, WeakReference<PostLikeEventBus>> busMap = new LinkedHashMap<>();

	@MainThread public static Ticket subscribe(int postId, OnPostLiked listener){
		return getBus(postId).subscribe(listener);
	}

	@MainThread public static void dispatch(Post post){
		if(post.getLikedByYou()!=null) dispatch(post.getId(), post.getLikedByYou(), post.getLikes());
	}
	@MainThread public static void dispatch(int postId, boolean liked, int likes){
		getBus(postId).dispatch(liked, likes);
	}

	private static PostLikeEventBus getBus(int postId){
		WeakReference<PostLikeEventBus> busRef = busMap.get(postId);
		PostLikeEventBus bus = busRef==null ? null : busRef.get();
		if(bus==null){
			bus = new PostLikeEventBus();
			busMap.put(postId, new WeakReference<>(bus));
		}
		return bus;
	}


	private static final class PostLikeEventBus implements Bus{
		private final WeakHashMap<Ticket, OnPostLiked> listenerMap = new WeakHashMap<>();

		private int idIncrement;

		private boolean liked;
		private int likes;

		public Ticket subscribe(OnPostLiked listener){
			int id = idIncrement++;
			Ticket ticket = new Ticket(this, id);
			if(listenerMap.put(ticket, Objects.requireNonNull(listener))!=null) throw new IllegalStateException("Unexpected");
			listener.onPostLiked(liked, likes);
			return ticket;
		}

		public void dispatch(boolean liked, int likes){
			if(this.liked!=liked||this.likes!=likes){
				this.liked = liked;
				this.likes = likes;
				for(OnPostLiked listener : listenerMap.values()){
					listener.onPostLiked(liked, likes);
				}
			}
		}

		@Override public void unsubscribe(Ticket ticket){
			if(ticket.bus!=this) throw new IllegalArgumentException("Ticket not registered here");
			listenerMap.remove(ticket);
		}
	}
}
