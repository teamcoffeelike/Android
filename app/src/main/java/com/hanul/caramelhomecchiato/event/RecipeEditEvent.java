package com.hanul.caramelhomecchiato.event;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public final class RecipeEditEvent{
	private RecipeEditEvent(){}

	private static final Map<Integer, WeakReference<RecipeEditEventBus>> busMap = new LinkedHashMap<>();
	@Nullable private static WeakReference<RecipeEditEventBus> globalBus;

	@MainThread public static Ticket subscribe(int recipeId, OnRecipeEdited listener){
		return getBus(recipeId).subscribe(listener);
	}
	@MainThread public static Ticket subscribeAll(OnRecipeEdited listener){
		return getGlobalBus().subscribe(listener);
	}

	@MainThread public static void dispatch(int recipeId){
		getBus(recipeId).dispatch(recipeId);
		getGlobalBus().dispatch(recipeId);
	}

	private static RecipeEditEventBus getBus(int recipeId){
		WeakReference<RecipeEditEventBus> busRef = busMap.get(recipeId);
		RecipeEditEventBus bus = busRef==null ? null : busRef.get();
		if(bus==null){
			bus = new RecipeEditEventBus();
			busMap.put(recipeId, new WeakReference<>(bus));
		}
		return bus;
	}

	private static RecipeEditEventBus getGlobalBus(){
		RecipeEditEventBus bus = globalBus==null ? null : globalBus.get();
		if(bus==null){
			bus = new RecipeEditEventBus();
			globalBus = new WeakReference<>(bus);
		}
		return bus;
	}


	private static final class RecipeEditEventBus implements Bus{
		private final WeakHashMap<Ticket, OnRecipeEdited> listenerMap = new WeakHashMap<>();

		private int idIncrement;

		public Ticket subscribe(OnRecipeEdited listener){
			int id = idIncrement++;
			Ticket ticket = new Ticket(this, id);
			if(listenerMap.put(ticket, Objects.requireNonNull(listener))!=null) throw new IllegalStateException("Unexpected");
			return ticket;
		}

		public void dispatch(int recipeId){
			for(OnRecipeEdited listener : listenerMap.values()){
				listener.onRecipeEdited(recipeId);
			}
		}

		@Override public void unsubscribe(Ticket ticket){
			if(ticket.bus!=this) throw new IllegalArgumentException("Ticket not registered here");
			listenerMap.remove(ticket);
		}
	}
}
