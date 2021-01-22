package com.hanul.caramelhomecchiato.event;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public final class RecipeDeleteEvent{
	private RecipeDeleteEvent(){}

	private static final Map<Integer, WeakReference<RecipeDeleteEventBus>> busMap = new LinkedHashMap<>();
	@Nullable private static WeakReference<RecipeDeleteEventBus> globalBus;

	@MainThread public static Ticket subscribe(int recipeId, OnRecipeDeleted listener){
		return getBus(recipeId).subscribe(listener);
	}
	@MainThread public static Ticket subscribeAll(OnRecipeDeleted listener){
		return getGlobalBus().subscribe(listener);
	}

	@MainThread public static void dispatch(int recipeId){
		getBus(recipeId).dispatch(recipeId);
		getGlobalBus().dispatch(recipeId);
	}

	private static RecipeDeleteEventBus getBus(int recipeId){
		WeakReference<RecipeDeleteEventBus> busRef = busMap.get(recipeId);
		RecipeDeleteEventBus bus = busRef==null ? null : busRef.get();
		if(bus==null){
			bus = new RecipeDeleteEventBus();
			busMap.put(recipeId, new WeakReference<>(bus));
		}
		return bus;
	}

	private static RecipeDeleteEventBus getGlobalBus(){
		RecipeDeleteEventBus bus = globalBus==null ? null : globalBus.get();
		if(bus==null){
			bus = new RecipeDeleteEventBus();
			globalBus = new WeakReference<>(bus);
		}
		return bus;
	}


	private static final class RecipeDeleteEventBus implements Bus{
		private final WeakHashMap<Ticket, OnRecipeDeleted> listenerMap = new WeakHashMap<>();

		private int idIncrement;

		public Ticket subscribe(OnRecipeDeleted listener){
			int id = idIncrement++;
			Ticket ticket = new Ticket(this, id);
			if(listenerMap.put(ticket, Objects.requireNonNull(listener))!=null) throw new IllegalStateException("Unexpected");
			return ticket;
		}

		public void dispatch(int recipeId){
			for(OnRecipeDeleted listener : listenerMap.values()){
				listener.onRecipeDeleted(recipeId);
			}
		}

		@Override public void unsubscribe(Ticket ticket){
			if(ticket.bus!=this) throw new IllegalArgumentException("Ticket not registered here");
			listenerMap.remove(ticket);
		}
	}
}
