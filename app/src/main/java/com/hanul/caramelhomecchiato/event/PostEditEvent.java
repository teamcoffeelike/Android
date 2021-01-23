package com.hanul.caramelhomecchiato.event;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

import com.hanul.caramelhomecchiato.data.Post;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public final class PostEditEvent{
	private PostEditEvent(){}

	private static final Map<Integer, WeakReference<PostEditEventBus>> busMap = new LinkedHashMap<>();
	@Nullable private static WeakReference<PostEditEventBus> globalBus;

	@MainThread public static Ticket subscribe(int postId, OnPostEdited listener){
		return getBus(postId).subscribe(listener);
	}
	@MainThread public static Ticket subscribeAll(OnPostEdited listener){
		return getGlobalBus().subscribe(listener);
	}

	@MainThread public static void dispatch(Post post){
		dispatch(post.getId());
	}
	@MainThread public static void dispatch(int postId){
		getBus(postId).dispatch(postId);
		getGlobalBus().dispatch(postId);
	}

	private static PostEditEventBus getBus(int postId){
		WeakReference<PostEditEventBus> busRef = busMap.get(postId);
		PostEditEventBus bus = busRef==null ? null : busRef.get();
		if(bus==null){
			bus = new PostEditEventBus();
			busMap.put(postId, new WeakReference<>(bus));
		}
		return bus;
	}

	private static PostEditEventBus getGlobalBus(){
		PostEditEventBus bus = globalBus==null ? null : globalBus.get();
		if(bus==null){
			bus = new PostEditEventBus();
			globalBus = new WeakReference<>(bus);
		}
		return bus;
	}


	private static final class PostEditEventBus implements Bus{
		private final WeakHashMap<Ticket, OnPostEdited> listenerMap = new WeakHashMap<>();

		private int idIncrement;

		public Ticket subscribe(OnPostEdited listener){
			int id = idIncrement++;
			Ticket ticket = new Ticket(this, id);
			if(listenerMap.put(ticket, Objects.requireNonNull(listener))!=null) throw new IllegalStateException("Unexpected");
			return ticket;
		}

		public void dispatch(int postId){
			for(OnPostEdited listener : listenerMap.values()){
				listener.onPostEdited(postId);
			}
		}

		@Override public void unsubscribe(Ticket ticket){
			if(ticket.bus!=this) throw new IllegalArgumentException("Ticket not registered here");
			listenerMap.remove(ticket);
		}
	}
}
