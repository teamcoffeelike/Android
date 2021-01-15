package com.hanul.caramelhomecchiato.event;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

import com.hanul.caramelhomecchiato.data.Post;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public final class PostDeleteEventDispatcher{
	private PostDeleteEventDispatcher(){}

	private static final Map<Integer, WeakReference<PostDeleteEventBus>> busMap = new LinkedHashMap<>();
	@Nullable private static WeakReference<PostDeleteEventBus> globalBus;

	@MainThread public static Ticket subscribe(int postId, OnPostDeleted listener){
		return getBus(postId).subscribe(listener);
	}
	@MainThread public static Ticket subscribeAll(OnPostDeleted listener){
		return getGlobalBus().subscribe(listener);
	}

	@MainThread public static void dispatch(Post post){
		dispatch(post.getId());
	}
	@MainThread public static void dispatch(int postId){
		getBus(postId).dispatch(postId);
		getGlobalBus().dispatch(postId);
	}

	private static PostDeleteEventBus getBus(int postId){
		WeakReference<PostDeleteEventBus> busRef = busMap.get(postId);
		PostDeleteEventBus bus = busRef==null ? null : busRef.get();
		if(bus==null){
			bus = new PostDeleteEventBus();
			busMap.put(postId, new WeakReference<>(bus));
		}
		return bus;
	}

	private static PostDeleteEventBus getGlobalBus(){
		PostDeleteEventBus bus = globalBus==null ? null : globalBus.get();
		if(bus==null){
			bus = new PostDeleteEventBus();
			globalBus = new WeakReference<>(bus);
		}
		return bus;
	}


	private static final class PostDeleteEventBus implements Bus{
		private final WeakHashMap<Ticket, OnPostDeleted> listenerMap = new WeakHashMap<>();

		private int idIncrement;

		public Ticket subscribe(OnPostDeleted listener){
			int id = idIncrement++;
			Ticket ticket = new Ticket(this, id);
			if(listenerMap.put(ticket, Objects.requireNonNull(listener))!=null) throw new IllegalStateException("Unexpected");
			return ticket;
		}

		public void dispatch(int postId){
			for(OnPostDeleted listener : listenerMap.values()){
				listener.onPostDeleted(postId);
			}
		}

		@Override public void unsubscribe(Ticket ticket){
			if(ticket.bus!=this) throw new IllegalArgumentException("Ticket not registered here");
			listenerMap.remove(ticket);
		}
	}
}
