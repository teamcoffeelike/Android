package com.hanul.caramelhomecchiato.event;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public final class RecipeRateEvent{
	private RecipeRateEvent(){}

	private static final Map<Integer, WeakReference<RecipeRateEventBus>> busMap = new LinkedHashMap<>();
	@Nullable private static WeakReference<RecipeRateEventBus> globalBus;

	@MainThread public static Ticket subscribe(int recipeId, OnRecipeRated listener){
		return getBus(recipeId).subscribe(listener);
	}
	@MainThread public static Ticket subscribeAll(OnRecipeRated listener){
		return getGlobalBus().subscribe(listener);
	}

	@MainThread public static void dispatch(int recipeId){
		getBus(recipeId).dispatch(recipeId);
		getGlobalBus().dispatch(recipeId);
	}

	private static RecipeRateEventBus getBus(int recipeId){
		WeakReference<RecipeRateEventBus> busRef = busMap.get(recipeId);
		RecipeRateEventBus bus = busRef==null ? null : busRef.get();
		if(bus==null){
			bus = new RecipeRateEventBus();
			busMap.put(recipeId, new WeakReference<>(bus));
		}
		return bus;
	}

	private static RecipeRateEventBus getGlobalBus(){
		RecipeRateEventBus bus = globalBus==null ? null : globalBus.get();
		if(bus==null){
			bus = new RecipeRateEventBus();
			globalBus = new WeakReference<>(bus);
		}
		return bus;
	}


	private static final class RecipeRateEventBus implements Bus{
		private final WeakHashMap<Ticket, OnRecipeRated> listenerMap = new WeakHashMap<>();

		private int idIncrement;

		public Ticket subscribe(OnRecipeRated listener){
			int id = idIncrement++;
			Ticket ticket = new Ticket(this, id);
			if(listenerMap.put(ticket, Objects.requireNonNull(listener))!=null) throw new IllegalStateException("Unexpected");
			return ticket;
		}

		public void dispatch(int recipeId){
			for(OnRecipeRated listener : listenerMap.values()){
				listener.onRecipeRated(recipeId);
			}
		}

		@Override public void unsubscribe(Ticket ticket){
			if(ticket.bus!=this) throw new IllegalArgumentException("Ticket not registered here");
			listenerMap.remove(ticket);
		}
	}
}
